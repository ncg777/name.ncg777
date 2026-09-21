package name.ncg777.computing;

import java.math.BigInteger;
import java.util.*;
import java.util.function.*;
import name.ncg777.maths.Trit;

/** Recursive functional language with a microcoded, single-head ternary tape runtime.
 * BigInteger is used only at the input/output boundary, never for runtime arithmetic. */
public final class TernaryLanguage {
  private TernaryLanguage() {}
  private static final List<String> GATES = List.of((TernaryExpression.UNARY + " " + TernaryExpression.BINARY).split(" "));
  private static final List<String> BUILTINS;
  static {
    var names = new ArrayList<>(GATES);
    names.addAll(List.of("+", "-", "*", "compare"));
    BUILTINS = List.copyOf(names);
  }
  public record Limits(int tapeCells, int integerDigits) {
    public Limits {
      if (tapeCells < 1024 || tapeCells > 1000000 || integerDigits < 1 || integerDigits > 4096)
        throw new IllegalArgumentException("Limits: 1024..1000000 tape cells and 1..4096 integer trits");
    }
    public static Limits defaults() { return new Limits(131072, 256); }
  }
  private record Form(String atom, List<Form> list) {}
  private record Expr(String kind, int symbol, int[] children, int[] parameters, int[] digits) {}

  public static final class Compiled {
    private final List<Expr> expressions;
    private final List<String> symbols;
    private final List<String> inputs;
    private final Map<Integer, Integer> definitions;
    private final int main;
    private Compiled(Parser parser, int main) {
      expressions = List.copyOf(parser.expressions); symbols = List.copyOf(parser.symbols);
      inputs = List.copyOf(parser.inputs); definitions = Map.copyOf(parser.definitions); this.main = main;
    }
    public List<String> inputs() { return inputs; }
    public Execution start(Map<String, BigInteger> values) { return start(values, Limits.defaults()); }
    public Execution start(Map<String, BigInteger> values, Limits limits) { return new Execution(this, values, limits); }
  }

  public static Compiled compile(String source) { return new Parser(source).compile(); }

  public static final class Execution {
    private final Engine engine;
    private final TernaryMachine machine;
    private String failure;
    private Execution(Compiled compiled, Map<String, BigInteger> values, Limits limits) {
      Objects.requireNonNull(values); Objects.requireNonNull(limits);
      if (!values.keySet().equals(new HashSet<>(compiled.inputs)))
        throw new IllegalArgumentException("Expected inputs " + compiled.inputs);
      engine = new Engine(compiled, limits);
      machine = new TernaryMachine(engine);
      engine.initialize(machine, values);
    }
    /** A budget is additional tape transitions; a false return can be resumed. */
    public boolean run(long steps) {
      if (failure != null) throw new IllegalStateException(failure);
      if (steps < 0) throw new IllegalArgumentException("Negative step budget");
      try { return machine.run(steps); }
      catch (RuntimeException e) { failure = e.getMessage(); throw e; }
    }
    public boolean halted() { return machine.halted(); }
    public long steps() { return machine.steps(); }
    public int allocatedCells() { return engine.r[HP]; }
    public int maximumContinuationDepth() { return engine.maximumDepth; }
    public boolean functionResult() {
      requireHalted();
      int value = engine.pointer(machine, 0);
      return engine.pointer(machine, value) != 0;
    }
    public BigInteger integerResult() {
      requireHalted();
      int value = engine.pointer(machine, 0);
      if (engine.pointer(machine, value) != 0) throw new IllegalStateException("Result is a function");
      int digit = engine.pointer(machine, value + engine.width);
      BigInteger result = BigInteger.ZERO, power = BigInteger.ONE;
      while (digit != 0) {
        result = result.add(power.multiply(BigInteger.valueOf(machine.read(BigInteger.valueOf(digit)))));
        power = power.multiply(BigInteger.valueOf(3));
        digit = engine.pointer(machine, digit + engine.width);
      }
      return result;
    }
    private void requireHalted() {
      if (failure != null) throw new IllegalStateException(failure);
      if (!halted()) throw new IllegalStateException("Execution has not halted; no result yet");
    }
  }

  // These bounded registers carry only addresses, code identifiers, counts and local trits.
  private static final int HP=0, ENV=1, K=2, V=3, E=4, H=5, DEPTH=6;
  private static final int A=7, B=8, C=9, D=10, F=11, ARGS=12, COUNT=13, IDX=14, NODE=15;
  private static final int LEFT=16, RIGHT=17, SIGN=18, RET=19, OUT=20;
  private static final int LP=21, RP=22, CARRY=23, LD=24, RD=25, DIGIT=26, HEAD=27, TAIL=28, LAST=29, SIZE=30;
  private static final int SHIFT=31, MULT=32, ACC=33, OP=34, RESULT=35, ARG1=36, ARG2=37, FREE=38;
  private static final int SEEK=60, IO=61, INDEX=62, POWER=63;
  private static final class Label { int pc = -1; }
  @FunctionalInterface private interface Instruction { TernaryMachine.Rule execute(int read, int pc); }

  private static final class Engine implements TernaryMachine.Controller {
    // Tape records are width-sized fields; zero is the null pointer.
    // Integer: [0, digitHead]; closure: [1, lambdaId, environment]; builtin: [2, id].
    // Digit: [raw balanced trit, next]; environment: [symbol, value, parent].
    // Argument pair: [value, previous]. Continuation:
    // [kind, parent, callerEnvironment, expressionId, function, arguments, argumentIndex].
    // Literal/input loading happens before stepping. Runtime instructions access
    // the tape exclusively through the current read symbol and returned Rule.
    final Compiled source;
    final Limits limits;
    final int width;
    final int[] r = new int[64];
    final List<Instruction> code = new ArrayList<>();
    final Label evaluate=new Label(), returning=new Label(), apply=new Label(), add=new Label();
    int[] entries;
    final int[] literals;
    int maximumDepth;
    Engine(Compiled source, Limits limits) {
      this.source=source; this.limits=limits;
      literals=new int[source.expressions.size()];
      int w=1, capacity=3;
      while (capacity <= Math.max(limits.tapeCells(), 16384)) { w++; capacity*=3; }
      width=w;
      build();
    }
    @Override public TernaryMachine.Rule rule(int state, int read) {
      return code.get(state).execute(read, state);
    }
    void mark(Label label) { label.pc=code.size(); }
    void instruction(Instruction instruction) {
      if(code.size()>=100000)throw new IllegalArgumentException("Microcode exceeds 100000 instructions");
      code.add(instruction);
    }
    void act(Consumer<int[]> action) {
      instruction((read, pc) -> { action.accept(r); return new TernaryMachine.Rule(read,0,pc+1); });
    }
    void set(int register, int value) { act(q -> q[register]=value); }
    void copy(int to, int from) { act(q -> q[to]=q[from]); }
    void jump(Label label) { instruction((read, pc) -> new TernaryMachine.Rule(read,0,label.pc)); }
    void branch(Predicate<int[]> test, Label yes) {
      instruction((read, pc) -> new TernaryMachine.Rule(read,0,test.test(r)?yes.pc:pc+1));
    }
    void fail(String message) { act(q -> { throw new IllegalArgumentException(message); }); }
    void seek() {
      instruction((read, pc) -> {
        if (r[SEEK] < 0 || r[SEEK] >= limits.tapeCells()) throw new IllegalStateException("Tape address out of bounds");
        int move=Integer.compare(r[SEEK],r[H]); r[H]+=move;
        return new TernaryMachine.Rule(read,move,move==0?pc+1:pc);
      });
    }
    void readField(int dest, int base, int field) {
      act(q -> { q[SEEK]=q[base]+field*width; q[IO]=0; q[INDEX]=0; q[POWER]=1; });
      Label loop=new Label(); mark(loop); seek();
      instruction((read, pc) -> {
        r[IO]+=(read==-1?2:read)*r[POWER]; r[POWER]*=3; r[INDEX]++; r[SEEK]++;
        return new TernaryMachine.Rule(read,0,r[INDEX]<width?loop.pc:pc+1);
      });
      copy(dest,IO);
    }
    void writeField(int base, int field, int value) {
      act(q -> { q[SEEK]=q[base]+field*width; q[IO]=q[value]; q[INDEX]=0;
        if (q[IO]<0 || q[IO]>=qCapacity()) throw new IllegalStateException("Control field overflow"); });
      Label loop=new Label(); mark(loop); seek();
      instruction((read, pc) -> {
        int digit=r[IO]%3; r[IO]/=3; r[INDEX]++; r[SEEK]++;
        return new TernaryMachine.Rule(digit==2?-1:digit,0,r[INDEX]<width?loop.pc:pc+1);
      });
    }
    int qCapacity() { int result=1; for(int i=0;i<width;i++)result*=3; return result; }
    void readDigit(int dest, int address) {
      copy(SEEK,address); seek();
      instruction((read,pc)->{r[dest]=read;return new TernaryMachine.Rule(read,0,pc+1);});
    }
    void writeDigit(int address, int value) {
      copy(SEEK,address); seek();
      instruction((read,pc)->new TernaryMachine.Rule(r[value],0,pc+1));
    }
    int allocate(int fields) {
      int address=r[HP];
      if (address+fields*width>limits.tapeCells()) throw new IllegalStateException("Tape allocation budget exhausted");
      r[HP]+=fields*width; return address;
    }
    void alloc(int dest, int fields) { act(q -> q[dest]=allocate(fields)); }
    void zeroField(int base,int field) { set(RESULT,0); writeField(base,field,RESULT); }
    void integer(int head) {
      alloc(V,2); zeroField(V,0); writeField(V,1,head); jump(returning);
    }
    void push(int kind, int expression) {
      Label fresh=new Label(),allocated=new Label(); branch(q->q[FREE]==0,fresh);
      copy(NODE,FREE); readField(FREE,NODE,1); jump(allocated);
      mark(fresh); alloc(NODE,7); mark(allocated);
      set(A,kind); writeField(NODE,0,A); writeField(NODE,1,K);
      writeField(NODE,2,ENV); set(A,expression); writeField(NODE,3,A);
      zeroField(NODE,4); zeroField(NODE,5); zeroField(NODE,6);
      copy(K,NODE); act(q->{q[DEPTH]++;maximumDepth=Math.max(maximumDepth,q[DEPTH]);});
    }
    void pop() {
      copy(NODE,K); readField(K,K,1); writeField(NODE,1,FREE); copy(FREE,NODE);
      act(q->q[DEPTH]--);
    }
    void requireInteger(int value) {
      readField(A,value,0); Label ok=new Label(); branch(q->q[A]==0,ok); fail("Expected an integer"); mark(ok);
    }
    void trit(int value, int dest) {
      requireInteger(value); readField(B,value,1); set(dest,0);
      Label done=new Label(); branch(q->q[B]==0,done);
      readDigit(dest,B); readField(C,B,1); branch(q->q[C]==0,done);
      fail("Expected a single trit (-1, 0 or 1)"); mark(done);
    }

    void build() {
      jump(evaluate);
      entries=new int[source.expressions.size()];
      for(int id=0;id<source.expressions.size();id++) {
        entries[id]=code.size(); Expr expression=source.expressions.get(id);
        switch(expression.kind()) {
          case "number" -> {
            int literalId=id; act(q->q[V]=literals[literalId]); jump(returning);
          }
          case "variable" -> {
            copy(NODE,ENV); Label loop=new Label(),found=new Label(); mark(loop);
            Label present=new Label(); branch(q->q[NODE]!=0,present); fail("Unbound variable"); mark(present);
            readField(A,NODE,0); branch(q->q[A]==expression.symbol(),found);
            readField(NODE,NODE,2); jump(loop); mark(found); readField(V,NODE,1); jump(returning);
          }
          case "lambda" -> {
            alloc(V,3); set(A,1); writeField(V,0,A); set(A,id); writeField(V,1,A); writeField(V,2,ENV); jump(returning);
          }
          case "case" -> { push(3,id); set(E,expression.children()[0]); jump(evaluate); }
          case "call" -> { push(1,id); set(E,expression.children()[0]); jump(evaluate); }
          default -> throw new IllegalStateException("Unknown expression");
        }
      }
      mark(evaluate);
      instruction((read,pc)->new TernaryMachine.Rule(read,0,entries[r[E]]));
      mark(returning);
      Label haveFrame=new Label(); branch(q->q[K]!=0,haveFrame);
      set(NODE,0); writeField(NODE,0,V); set(SEEK,0); seek();
      instruction((read,pc)->new TernaryMachine.Rule(read,0,TernaryMachine.HALT));
      mark(haveFrame); readField(A,K,0);
      Label callOperator=new Label(),callArgument=new Label(),caseReturn=new Label();
      branch(q->q[A]==1,callOperator); branch(q->q[A]==2,callArgument); jump(caseReturn);
      mark(caseReturn); trit(V,DIGIT); readField(E,K,3);
      act(q->q[E]=source.expressions.get(q[E]).children()[q[DIGIT]+2]);
      readField(ENV,K,2); pop(); jump(evaluate);
      mark(callOperator); writeField(K,4,V); set(A,2); writeField(K,0,A);
      Label nextArgument=new Label(),ready=new Label(); jump(nextArgument);
      mark(callArgument);
      readField(B,K,5); alloc(NODE,2); writeField(NODE,0,V); writeField(NODE,1,B); writeField(K,5,NODE);
      readField(IDX,K,6); act(q->q[IDX]++); writeField(K,6,IDX);
      mark(nextArgument); readField(E,K,3); readField(IDX,K,6);
      branch(q->q[IDX]==source.expressions.get(q[E]).children().length-1,ready);
      act(q->q[E]=source.expressions.get(q[E]).children()[q[IDX]+1]);
      readField(ENV,K,2); jump(evaluate);
      mark(ready); readField(F,K,4); readField(ARGS,K,5); copy(COUNT,IDX); pop(); jump(apply);
      buildApply(); buildAddition();
    }

    void buildApply() {
      mark(apply); readField(A,F,0);
      Label closure=new Label(),builtin=new Label(); branch(q->q[A]==1,closure); branch(q->q[A]==2,builtin);
      fail("Attempted to call an integer");
      mark(closure); readField(E,F,1); readField(ENV,F,2);
      Label arityOk=new Label(); branch(q->q[COUNT]==source.expressions.get(q[E]).parameters().length,arityOk);
      fail("Wrong number of function arguments"); mark(arityOk); copy(IDX,COUNT);
      Label bind=new Label(),bound=new Label(); mark(bind); branch(q->q[IDX]==0,bound);
      act(q->q[IDX]--); readField(B,ARGS,0); readField(ARGS,ARGS,1); alloc(NODE,3);
      act(q->q[A]=source.expressions.get(q[E]).parameters()[q[IDX]]);
      writeField(NODE,0,A); writeField(NODE,1,B); writeField(NODE,2,ENV); copy(ENV,NODE); jump(bind);
      mark(bound); act(q->q[E]=source.expressions.get(q[E]).children()[0]); jump(evaluate);
      mark(builtin); readField(OP,F,1);
      Label good=new Label(); branch(q->q[COUNT]==(q[OP]<14?1:2),good); fail("Wrong number of built-in arguments"); mark(good);
      readField(ARG2,ARGS,0); Label unary=new Label(),loaded=new Label(); branch(q->q[COUNT]==1,unary);
      readField(ARGS,ARGS,1); readField(ARG1,ARGS,0); jump(loaded);
      mark(unary); copy(ARG1,ARG2); mark(loaded);
      Label arithmetic=new Label(); branch(q->q[OP]>=GATES.size(),arithmetic);
      trit(ARG1,LD); trit(ARG2,RD);
      act(q->q[DIGIT]=q[OP]<14?Trit.unaryOperator(GATES.get(q[OP]),q[LD]):Trit.binaryOperator(GATES.get(q[OP]),q[LD],q[RD]));
      Label makeTrit=new Label(); jump(makeTrit);
      mark(arithmetic); requireInteger(ARG1); requireInteger(ARG2);
      readField(LEFT,ARG1,1); readField(RIGHT,ARG2,1);
      Label multiply=new Label(),afterAdd=new Label(); branch(q->q[OP]==GATES.size()+2,multiply);
      act(q->{q[SIGN]=q[OP]==GATES.size()?1:-1;q[RET]=afterAdd.pc;}); jump(add);
      mark(afterAdd); Label comparison=new Label(); branch(q->q[OP]==GATES.size()+3,comparison); integer(OUT);
      mark(comparison); set(DIGIT,0); Label scan=new Label(),scanned=new Label(); mark(scan); branch(q->q[OUT]==0,scanned);
      readDigit(DIGIT,OUT); readField(OUT,OUT,1); jump(scan); mark(scanned); jump(makeTrit);
      mark(multiply); copy(SHIFT,LEFT); copy(MULT,RIGHT); set(ACC,0);
      Label loop=new Label(),done=new Label(),shift=new Label(),summed=new Label(); mark(loop); branch(q->q[MULT]==0,done);
      readDigit(SIGN,MULT); readField(MULT,MULT,1); branch(q->q[SIGN]==0,shift);
      copy(LEFT,ACC); copy(RIGHT,SHIFT); act(q->q[RET]=summed.pc); jump(add);
      mark(summed); copy(ACC,OUT);
      mark(shift); branch(q->q[MULT]==0,done); branch(q->q[SHIFT]==0,loop);
      alloc(NODE,2); set(DIGIT,0); writeDigit(NODE,DIGIT); writeField(NODE,1,SHIFT); copy(SHIFT,NODE); jump(loop);
      mark(done); integer(ACC);
      mark(makeTrit); set(HEAD,0); Label zero=new Label(); branch(q->q[DIGIT]==0,zero);
      alloc(HEAD,2); writeDigit(HEAD,DIGIT); zeroField(HEAD,1); mark(zero); integer(HEAD);
    }

    void buildAddition() {
      mark(add); copy(LP,LEFT); copy(RP,RIGHT); set(CARRY,0); set(HEAD,0); set(TAIL,0); set(LAST,0); set(SIZE,0);
      Label loop=new Label(),finish=new Label(); mark(loop);
      branch(q->q[LP]==0&&q[RP]==0&&q[CARRY]==0,finish);
      set(LD,0); set(RD,0); Label leftDone=new Label(),rightDone=new Label(); branch(q->q[LP]==0,leftDone);
      readDigit(LD,LP); readField(LP,LP,1); mark(leftDone); branch(q->q[RP]==0,rightDone);
      readDigit(RD,RP); readField(RP,RP,1); mark(rightDone);
      act(q->{int sum=q[LD]+q[SIGN]*q[RD]+q[CARRY]; q[CARRY]=sum>1?1:sum< -1?-1:0;q[DIGIT]=sum-3*q[CARRY];q[SIZE]++;});
      // At most one transient leading zero is needed before canonicalization.
      act(q->{if(q[SIZE]>limits.integerDigits()+1)throw new IllegalStateException("Integer digit budget exhausted");});
      alloc(NODE,2); writeDigit(NODE,DIGIT); zeroField(NODE,1);
      Label first=new Label(),linked=new Label(); branch(q->q[TAIL]==0,first); writeField(TAIL,1,NODE); jump(linked);
      mark(first); copy(HEAD,NODE); mark(linked); copy(TAIL,NODE);
      act(q->{if(q[DIGIT]!=0){if(q[SIZE]>limits.integerDigits())throw new IllegalStateException("Integer digit budget exhausted");q[LAST]=q[NODE];}});
      jump(loop); mark(finish);
      Label nonzero=new Label(),result=new Label(); branch(q->q[LAST]!=0,nonzero); set(OUT,0); jump(result);
      mark(nonzero); zeroField(LAST,1); copy(OUT,HEAD); mark(result);
      instruction((read,pc)->new TernaryMachine.Rule(read,0,r[RET]));
    }

    void putPointer(TernaryMachine machine,int address,int value) {
      for(int i=0;i<width;i++){int digit=value%3;value/=3;machine.write(BigInteger.valueOf(address+i),digit==2?-1:digit);}
    }
    int pointer(TernaryMachine machine,int address) {
      int value=0,power=1;
      for(int i=0;i<width;i++){int digit=machine.read(BigInteger.valueOf(address+i));value+=(digit==-1?2:digit)*power;power*=3;}
      return value;
    }
    int loadInteger(TernaryMachine machine,BigInteger value) {
      Objects.requireNonNull(value);
      if(value.abs().bitLength()>limits.integerDigits()*2)
        throw new IllegalArgumentException("Input exceeds integer digit budget");
      return loadDigits(machine,digits(value));
    }
    int loadDigits(TernaryMachine machine,int[] digits) {
      if(digits.length>limits.integerDigits())throw new IllegalArgumentException("Input exceeds integer digit budget");
      int head=0;
      for(int i=digits.length-1;i>=0;i--){int node=allocate(2);machine.write(BigInteger.valueOf(node),digits[i]);putPointer(machine,node+width,head);head=node;}
      int result=allocate(2);putPointer(machine,result,0);putPointer(machine,result+width,head);return result;
    }
    int bindInitial(TernaryMachine machine,int symbol,int value,int parent) {
      int node=allocate(3);putPointer(machine,node,symbol);putPointer(machine,node+width,value);putPointer(machine,node+2*width,parent);return node;
    }
    void initialize(TernaryMachine machine,Map<String,BigInteger> values) {
      r[HP]=width; int environment=0;
      for(int i=0;i<source.expressions.size();i++) {
        Expr expression=source.expressions.get(i);
        if(expression.kind().equals("number"))literals[i]=loadDigits(machine,expression.digits());
      }
      for(int i=0;i<BUILTINS.size();i++) {
        int value=allocate(3);putPointer(machine,value,2);putPointer(machine,value+width,i);
        environment=bindInitial(machine,source.symbols.indexOf(BUILTINS.get(i)),value,environment);
      }
      for(String name:source.inputs) environment=bindInitial(machine,source.symbols.indexOf(name),loadInteger(machine,values.get(name)),environment);
      Map<Integer,Integer> closures=new LinkedHashMap<>();
      for(var definition:source.definitions.entrySet()) {
        int value=allocate(3);putPointer(machine,value,1);putPointer(machine,value+width,definition.getValue());
        closures.put(definition.getKey(),value);
        environment=bindInitial(machine,definition.getKey(),value,environment);
      }
      for(int value:closures.values())putPointer(machine,value+2*width,environment);
      r[ENV]=environment;r[E]=source.main;
    }
  }

  private static int[] digits(BigInteger value) {
    List<Integer> result=new ArrayList<>(); BigInteger three=BigInteger.valueOf(3);
    while(value.signum()!=0){int digit=value.mod(three).intValue();if(digit==2)digit=-1;result.add(digit);value=value.subtract(BigInteger.valueOf(digit)).divide(three);}
    return result.stream().mapToInt(Integer::intValue).toArray();
  }

  private static final class Parser {
    final String source; int position,forms;
    final List<String> symbols=new ArrayList<>(BUILTINS),inputs=new ArrayList<>();
    final List<Expr> expressions=new ArrayList<>();
    final Map<Integer,Integer> definitions=new LinkedHashMap<>();
    Parser(String source){this.source=Objects.requireNonNull(source);if(source.length()>16384)throw error("Source exceeds 16384 characters");}
    IllegalArgumentException error(String message){return new IllegalArgumentException(message+" at character "+(position+1));}
    void space(){while(position<source.length()){char c=source.charAt(position);if(Character.isWhitespace(c))position++;else if(c==';'){while(position<source.length()&&source.charAt(position)!='\n')position++;}else break;}}
    Form read(int depth){
      space();if(depth>64||++forms>4096)throw error("Source nesting/form budget exceeded");if(position==source.length())throw error("Unexpected end");
      if(source.charAt(position)=='('){position++;List<Form> list=new ArrayList<>();space();while(position<source.length()&&source.charAt(position)!=')'){list.add(read(depth+1));space();}if(position==source.length())throw error("Unclosed parenthesis");position++;return new Form(null,List.copyOf(list));}
      int start=position;while(position<source.length()&&!Character.isWhitespace(source.charAt(position))&&"();".indexOf(source.charAt(position))<0)position++;
      if(start==position)throw error("Unexpected parenthesis");return new Form(source.substring(start,position),null);
    }
    String atom(Form form){if(form.atom()==null)throw error("Expected a name");return form.atom();}
    List<Form> list(Form form){if(form.list()==null)throw error("Expected a list");return form.list();}
    String name(Form form){String name=atom(form);if(!name.matches("[A-Za-z_][A-Za-z_0-9]*")||Set.of("T","def","inputs","let","lambda","case").contains(name)||BUILTINS.contains(canonical(name)))throw error("Invalid or reserved name "+name);return name;}
    String canonical(String name){String upper=name.toUpperCase(Locale.ROOT);return GATES.contains(upper)?upper:name;}
    int symbol(String name){int id=symbols.indexOf(name);if(id<0){id=symbols.size();symbols.add(name);}return id;}
    void size(List<Form> parts,int expected){if(parts.size()!=expected)throw error("Wrong number of form elements");}
    int add(Expr expression){int id=expressions.size();expressions.add(expression);return id;}
    Compiled compile(){
      List<Form> top=new ArrayList<>();space();while(position<source.length()){top.add(read(0));space();}if(top.isEmpty())throw error("Expected an expression");
      Set<String> scope=new HashSet<>(BUILTINS);boolean declared=false;
      for(Form form:top.subList(0,top.size()-1)){
        var p=list(form);if(p.isEmpty())throw error("Empty declaration");
        switch(atom(p.get(0))){
          case "inputs" -> {if(declared)throw error("Duplicate inputs");declared=true;for(Form input:p.subList(1,p.size())){String n=name(input);if(!scope.add(n))throw error("Duplicate name "+n);inputs.add(n);symbol(n);}if(inputs.size()>32)throw error("At most 32 inputs");}
          case "def" -> {size(p,4);String n=name(p.get(1));if(!scope.add(n))throw error("Duplicate name "+n);symbol(n);}
          default -> throw error("Only declarations may precede the final expression");
        }
      }
      for(Form form:top.subList(0,top.size()-1)){var p=list(form);if(atom(p.get(0)).equals("def"))definitions.put(symbol(atom(p.get(1))),lambda(p.get(2),p.get(3),scope));}
      return new Compiled(this,expression(top.get(top.size()-1),scope));
    }
    int lambda(Form parameters,Form body,Set<String> scope){
      Set<String> names=new HashSet<>(),nested=new HashSet<>(scope);List<Integer> ids=new ArrayList<>();
      for(Form parameter:list(parameters)){String n=name(parameter);if(!names.add(n))throw error("Duplicate parameter "+n);nested.add(n);ids.add(symbol(n));}
      int code=expression(body,nested);return add(new Expr("lambda",0,new int[]{code},ids.stream().mapToInt(Integer::intValue).toArray(),null));
    }
    int expression(Form form,Set<String> scope){
      if(form.atom()!=null){String text=form.atom();if(text.equals("T")||text.matches("-?[0-9]+")){BigInteger value=text.equals("T")?BigInteger.valueOf(-1):new BigInteger(text);return add(new Expr("number",0,null,null,digits(value)));}
        String n=canonical(text);if(!scope.contains(n))throw error("Unbound variable "+text);return add(new Expr("variable",symbol(n),null,null,null));}
      var p=list(form);if(p.isEmpty())throw error("Empty expression");String keyword=p.get(0).atom();
      if("lambda".equals(keyword)){size(p,3);return lambda(p.get(1),p.get(2),scope);}
      if("let".equals(keyword)){size(p,4);String n=name(p.get(1));int value=expression(p.get(2),scope);int function=lambda(new Form(null,List.of(new Form(n,null))),p.get(3),scope);return add(new Expr("call",0,new int[]{function,value},null,null));}
      if("case".equals(keyword)){size(p,5);int[] children=p.subList(1,5).stream().mapToInt(f->expression(f,scope)).toArray();return add(new Expr("case",0,children,null,null));}
      int[] children=p.stream().mapToInt(f->expression(f,scope)).toArray();return add(new Expr("call",0,children,null,null));
    }
  }
}
