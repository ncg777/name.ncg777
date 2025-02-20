package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import name.ncg777.maths.Numbers;
import name.ncg777.maths.sequences.Sequence;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Operator {

  private JFrame frmAddSequences;
  private JTextField textX;
  private JTextField textY;
  private JTextField textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Operator window = new Operator();
          window.frmAddSequences.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private Map<Operation,BiFunction<Integer, Integer, Integer>> ops;
  private enum Combiner {
    Product,
    Triangular,
    LCM,
    Apply,
    Reduce,
  }
  
  private enum Operation {
    Add,
    Subtract,
    Multiply,
    Divide,
    X,
    Y,
    Power,
    Log,
    Min,
    Max,
    Modulo,
    Bounce,
    And,
    Nand,
    Or,
    Nor,
    Implication,
    ReverseImplication,
    Xor,
    Xnor,
    ShiftLeft,
    ShiftRight,
    LCM,
    GCD,
    Equal,
    NotEqual,
    LessThan,
    LessThanOrEqual,
    GreaterThan,
    GreaterThanOrEqual,
  }
  private Combiner scale = Combiner.Product;
  private Operation operation = Operation.Add;
  private JComboBox<Combiner> comboCombiner;
  private JComboBox<Operation> comboOperation;
  public Operator() {
    ops = new TreeMap<Operation,BiFunction<Integer,Integer,Integer>>();
    
    ops.put(Operation.Add, (x,y) -> x+y);
    ops.put(Operation.Subtract, (x,y) -> x-y);
    ops.put(Operation.Multiply, (x,y) -> x*y);
    ops.put(Operation.Divide, (x,y) -> x/y);
    ops.put(Operation.X, (x,y) -> x);
    ops.put(Operation.Y, (x,y) -> y);
    ops.put(Operation.Power, (x,y) -> (int)Math.round(Math.pow(x,y)));
    ops.put(Operation.Log, (x,y) -> (int)Math.floor(Math.log(x)/Math.log(y)));
    ops.put(Operation.Min, (x,y) -> Math.min(x,y));
    ops.put(Operation.Max, (x,y) -> Math.max(x,y));
    ops.put(Operation.Modulo, (x,y) -> x%y);
    ops.put(Operation.Bounce, (x,y) -> x%(2*y) <= y ? x%(2*y) : ((2*y)-(x%(2*y))));
    ops.put(Operation.And, (x,y) -> x&y);
    ops.put(Operation.Nand, (x,y) -> ~(x&y));
    ops.put(Operation.Or, (x,y) -> x|y);
    ops.put(Operation.Nor, (x,y) -> ~(x|y));
    ops.put(Operation.Implication, (x,y) -> (~x)|y);
    ops.put(Operation.ReverseImplication, (x,y) -> (~y)|x);
    ops.put(Operation.Xor, (x,y) -> x^y);
    ops.put(Operation.Xnor, (x,y) -> ~(x^y));
    ops.put(Operation.ShiftLeft, (x,y) -> (x<<y));
    ops.put(Operation.ShiftRight, (x,y) -> (x>>y));
    ops.put(Operation.LCM, (x,y) -> (int)Numbers.lcm(x, y));
    ops.put(Operation.GCD, (x,y) -> (int)Numbers.gcd(x, y));
    ops.put(Operation.Equal, (x,y) -> x==y ? 1 :0);
    ops.put(Operation.NotEqual, (x,y) -> x!=y ? 1 :0);
    ops.put(Operation.LessThan, (x,y) -> x<y ? 1 :0);
    ops.put(Operation.LessThanOrEqual, (x,y) -> x<=y ? 1 :0);
    ops.put(Operation.GreaterThan, (x,y) -> x>y ? 1 :0);
    ops.put(Operation.GreaterThanOrEqual, (x,y) -> x>=y ? 1 :0);
    initialize();
  }

  private Sequence getResult(Sequence x, Sequence y) {
    var o = new Sequence();
    var operationFn = this.ops.get(operation);
    switch (this.scale) {
      case Combiner.Apply:
        for (int i = 0; i < y.size(); i++) {
            o.add(operationFn.apply(x.get(y.get(i) % x.size()), y.get(i % y.size())));
        }
        break;
      case Combiner.LCM:
        int l = (int)Numbers.lcm(x.size(),y.size());
        for (int i = 0; i < l; i++) { 
            o.add(operationFn.apply(x.get(i/(l/x.size())), y.get(i/(l/y.size()))));
        }
        break;
      case Combiner.Product:
        for (int i = 0; i < x.size(); i++) {
          for (int j = 0; j < y.size(); j++) {  
              o.add(operationFn.apply(x.get(i), y.get(j)));
          }
        }
        break;
      case Combiner.Triangular:
        for (int i = 0; i < x.size(); i++) {
          for (int j = 0; j < y.size(); j++) {
            if (j<=i) {
              o.add(operationFn.apply(x.get(i), y.get(j)));
            }
          }
        }
        break;
      case Combiner.Reduce:
        for (int i = 0; i < x.size(); i++) {
          o.add(y.stream().reduce(x.get(i), (a,b) -> operationFn.apply(a, b)));
        }
        break;
    }
    return o;
  }
  
  private void initialize() {
   
    frmAddSequences = new JFrame();
    frmAddSequences.setTitle("Operator");
    frmAddSequences.setBounds(100, 100, 450, 260);
    frmAddSequences.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmAddSequences.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Sequence x:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(0, 11, 76, 14);
    frmAddSequences.getContentPane().add(lblNewLabel);
    
    textX = new JTextField();
    textX.setBounds(80, 8, 344, 17);
    frmAddSequences.getContentPane().add(textX);
    textX.setColumns(10);
    
    textY = new JTextField();
    textY.setColumns(10);
    textY.setBounds(80, 31, 344, 17);
    frmAddSequences.getContentPane().add(textY);
    
    JLabel lblSequenceY = new JLabel("y:");
    lblSequenceY.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSequenceY.setBounds(0, 34, 76, 14);
    frmAddSequences.getContentPane().add(lblSequenceY);
    
    JButton btnNewButton = new JButton("Apply");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var x = Sequence.parse(textX.getText());
        var y = Sequence.parse(textY.getText());
        
        textResult.setText(getResult(x, y).toString());
      }
    });
    btnNewButton.setBounds(80, 53, 344, 23);
    frmAddSequences.getContentPane().add(btnNewButton);
    
    textResult = new JTextField();
    textResult.setColumns(10);
    textResult.setBounds(80, 183, 344, 17);
    frmAddSequences.getContentPane().add(textResult);
    
    JLabel lblYixi = new JLabel("Result (x[i/ or % or y[i]]⋅y[i%]):");
    lblYixi.setHorizontalAlignment(SwingConstants.LEFT);
    lblYixi.setBounds(80, 158, 182, 14);
    frmAddSequences.getContentPane().add(lblYixi);
    
    comboCombiner = new JComboBox<Combiner>(new DefaultComboBoxModel<>(Combiner.values()));
    comboCombiner.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        scale = (Combiner)comboCombiner.getSelectedItem();
      }
    });
    comboCombiner.setBounds(80, 87, 344, 23);
    frmAddSequences.getContentPane().add(comboCombiner);
    
    comboOperation = new JComboBox<Operation>(new DefaultComboBoxModel<>(Operation.values()));
    comboOperation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        operation = (Operation)comboOperation.getSelectedItem();
      }
    });
    comboOperation.setBounds(80, 121, 344, 23);
    frmAddSequences.getContentPane().add(comboOperation);
    
    JButton btnNewButton_1 = new JButton("x");
    btnNewButton_1.setBounds(250, 154, 48, 23);
    btnNewButton_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textX.setText(textResult.getText());
      }
    });
    frmAddSequences.getContentPane().add(btnNewButton_1);
    
    JButton btnNewButton_1_1 = new JButton("y");
    btnNewButton_1_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textY.setText(textResult.getText());
      }
    });
    btnNewButton_1_1.setBounds(305, 154, 48, 23);
    frmAddSequences.getContentPane().add(btnNewButton_1_1);
  }
}
