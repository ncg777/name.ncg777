package name.ncg777.maths.music;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import com.opencsv.CSVWriter;

import name.ncg777.maths.music.pcs12.Pcs12;


public class Main {
  public static void addInfo(File file, CSVWriter w, PrintWriter err) {
    System.out.println("reading: " + file.getAbsolutePath());
    if(!file.getName().contains(".")) return;
    String extension = file.getName().substring(file.getName().lastIndexOf(".")+1).toLowerCase();
    if (file.isFile() && (extension.equals("mid") || extension.equals("midi"))) {
      javax.sound.midi.Sequence sequence;
      try {
        sequence = MidiSystem.getSequence(file);
        String[] str = {
                        file.getAbsolutePath().replace("\\", "/"), 
                        Integer.toString(sequence.getTracks().length),
                        Float.toString(sequence.getDivisionType()),
                        Integer.toString(sequence.getResolution()),
                        Long.toString(sequence.getTickLength())
        };
        w.writeNext(str);
      } catch (InvalidMidiDataException e) {
        err.println(file.getAbsolutePath());
      } catch (IOException e) {
        err.println(file.getAbsolutePath());
      }
      
    }
  }
  static public void main(String[] args) {
  
    
    try {
      var chords = Pcs12.getChords();
      for(var c: chords) {
        System.out.println("s/" + c.toString() + "/" + c.toForteNumberString() + "/g");
      }
      
      /*
      var chords = Pcs12.getChords();
      var chords_sorted = new ArrayList<Pcs12>();
      
      chords_sorted.addAll(chords);
      chords_sorted.sort(new Comparator<Pcs12>() {

        @Override
        public int compare(Pcs12 a, Pcs12 b) {
          // TODO Auto-generated method stub
          return a.asSequence().compareTo(b.asSequence());
        }
        
      });
      
      var pw = new PrintWriter("d:/pcs12_forte.csv");
      pw.println("Pcs12,ForteNumber,CommonName,PitchClasses,Intervals");
      for(var ch : chords_sorted) {
        
        if(ch.getK() > 0) {
          var prime = ch.transpose(-ch.getForteNumberRotation()).getComposition().asSequence();
          pw.println(ch.toString() + "," + ch.getForteNumber() + "."+String.format("%02d", ch.getForteNumberRotation()) + ",\""+ (ch.getCommonName() == null ? "" : ch.getCommonName()) + "\"," +Joiner.on(" ").join(ch.asSequence()) + "," + prime.toString());
        }
      }
      pw.close();
      */
      /*
      var rh48 = Rhythm48.Generate();
      
      var pw = new PrintWriter("d:/SCIrhythm48.csv");
      var pred = new ShadowContourIsomorphic();
      pw.println("RHYTHM48,K");
      for(var rh : rh48) {
        if(pred.apply(rh)) {
          pw.println(rh.toString()+","+rh.getK());
        }
      }
      pw.close();
      */
      /*
      Relation<Pcs12, Pcs12> rel_horiz = Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1)));
      var allChords = Pcs12.getChords();
      CollectionUtils.filter(allChords, new Consonant());
      var d = new MarkableDirectedGraph<Pcs12>(allChords, rel_horiz);
      PrintWriter pw = new PrintWriter("D:/ChordsCircuits.csv");
      
      Consumer<List<Pcs12>> printer = (List<Pcs12> c) -> {
        pw.println(Joiner.on(" ").join(c));
      };
      
      CircuitFinder<Pcs12> cf = new CircuitFinder<Pcs12>(d,printer);
      
      for(var n : d.getVertices()) {
        cf.findCircuitsFrom(n, 4);
      }
      
      pw.close();
      */
      /*
      Relation<Pcs12, Pcs12> rel_horiz = Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1)));
      Relation<Pcs12, Pcs12> rel_vert = new PredicatedUnion(new Consonant());
      
      var allChords = Pcs12.getChords();
      CollectionUtils.filter(allChords, new Consonant());
      
      GraphUtils.writeToCsv(new MarkableDirectedGraph<Pcs12>(allChords, rel_horiz), "d:/pcs12_horiz.csv");
      GraphUtils.writeToCsv(new MarkableDirectedGraph<Pcs12>(allChords, rel_vert), "d:/pcs12_ConsonantUnion.csv");
      */
      /*
      var s = Sequence.parse("0 1 1 0");
      
      var y = s.getSymmetries();
      
      for(var d : y) {System.out.println(d);}
      */
      /*
      var pred = new SeqAllRhythmsSCI();
      
      var ns = Necklace.generate(4, 4);
      var pw = new PrintWriter("d:/Music making/necklaces-4-4-SCI.csv");
      pw.println("NECKLACE,MIN,MAX,SPAN,DISTINCT,REP");
      for(var n : ns) {
        if(pred.apply(n)) {
          pw.println(n.toString() + ","+n.getMin()+","+n.getMax()+","+(1+n.getMax()-n.getMin())+","+n.distinct().size()+ ","+(n.cyclicalForwardDifference().contains(0)?"1":"0"));
        }
      }
      pw.flush();
      pw.close();
      */
      /*
      var pred = new SpectrumRising();
      var rhs = HexadecimalWord.Generate();
      
      var pw = new PrintWriter("d:/SpectrumRising_rhythm16.csv");
      pw.println("HEX,K,SPECTRUM");
      for(HexadecimalWord r : rhs) {
        
        if(pred.apply(r)) {
          var s = HexadecimalWord.calcSpectrum(r);
          pw.println(r.toString() + "," + r.getK() + "," + (s.toString()));
          
        }
        
      }
      pw.flush();
      pw.close();
      */
      
      /*
      var f = new FileReader("d:/Music making/necklaces-16-5-SCI.csv");
      BufferedReader b = new BufferedReader(f);
      var sequences = new TreeSet<Sequence>();
      
      while(true) {
        var line = b.readLine();
        if(line == null) break;
        
        var s = Sequence.parse(line);
        sequences.add(s);        
      }
      
      var p = new PrintWriter("d:/Music making/necklaces-16-5-SCI-2.csv");
      p.println("NECKLACE,MIN,MAX,SPAN,DISTINCT,REP");
      for(var n : sequences) {
        p.println(n.toString() + ","+n.getMin()+","+n.getMax()+","+(1+n.getMax()-n.getMin())+","+n.distinct().size()+ ","+(n.cyclicalForwardDifference().contains(0)?"1":"0"));
      }
      p.flush();
      p.close();
      
      b.close();
      
      */
      
      /*
      Sequence lens = Sequence.parse("4 8");
      System.out.println("Generating...");
      for(var len: lens) {
        System.out.println(Integer.toString(len));
        var we = new WordEnumeration(len,8);
        
        //var pred = new PredicatedSeqRhythms(new LowEntropy());
        var printed = new TreeSet<Sequence>();
        PrintWriter pw0 = new PrintWriter("d:/necklaces-N" + Integer.toString(len) + "-K8-norep.csv");
        PrintWriter pw1 = new PrintWriter("d:/necklaces-N" + Integer.toString(len) + "-K8.csv");
        pw1.println("NECKLACE,MIN,MAX,SPAN,DISTINCT,REP,MAXABSDELTA");
        
        while(we.hasMoreElements()) {
          
          var s = new Sequence(we.nextElement());
          //if(pred.apply(s)) {
            var t = s.getMininumRotation();
            int maxabsdelta = t.cyclicalDifference().apply((i) -> Math.abs(i)).getMax();
            if(maxabsdelta <= 3 && !printed.contains(t)) {
              printed.add(t);
              if(!s.cyclicalDifference().contains(0)) pw0.println(t.toString());
              pw1.println(t.toString() + ","+t.getMin()+","+t.getMax()+","+(1+t.getMax()-t.getMin())+","+t.distinct().size()+ ","+(t.cyclicalDifference().contains(0)?"1":"0")+","+Integer.toString(maxabsdelta));
            }
          //}
        }
        
        pw0.flush();
        pw0.close();

        pw1.flush();
        pw1.close();
      }
      */
      
      
      /*
      Sequence ns = Sequence.parse("4 8");
      
      for(var n: ns) {
        InputStream ist = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/necklaces-N" + n.toString()+ "-K8-SCI-norep.csv");
        var isr = new InputStreamReader(ist);
        BufferedReader br = new BufferedReader(isr);
        var theset = new TreeSet<Sequence>();
        
        while(true) {
          String r = null;
          try {
            r = br.readLine();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          if(r==null || r.isEmpty()) break;
          
          var s0 = Sequence.parse(r);
          for(int i=0;i<s0.size();i++) {
            var sr = s0.rotate(i);
            theset.add(sr);
          }
        }
        var pred = new SeqAllRhythmsSCI();
        FiniteHomoRelation<Sequence> r = new FiniteHomoRelation<>(theset, (a,b) -> pred.apply(a.juxtapose(b)));
        r.writeToCSV(Printers.sequencePrinter, "d:/Music making/SequenceRel-SCI-N" + n + "-K8.csv");
      }
      
      */
      
      /*
      PrintWriter pw = new PrintWriter("d:/riseandfalls.csv");
      pw.println("SEQUENCE,N,LEN");
      for(int n=1;n<=40;n++) {
        var f = Numbers.factors(n);
        
        TreeSet<HomoPair<Integer>> p = new TreeSet<>();
        for(int i : f) {
          int a = i;
          int b = n/i;
          
          p.add(HomoPair.makeHomogeneousPair(Math.min(a, b),Math.max(a,b)));
        }
        for(var p1: p) {
          
          for(var p2: p) {
            Sequence s1 = new Sequence();
            s1 = s1.juxtapose(Sequence.stair(0, p1.getFirst(), p1.getSecond()));
            s1 = s1.juxtapose(Sequence.stair(n, p2.getFirst(), -p2.getSecond()));
            
            Sequence s2 = new Sequence();
            s2 = s2.juxtapose(Sequence.stair(0, p1.getSecond(), p1.getFirst()));
            s2 = s2.juxtapose(Sequence.stair(n, p2.getFirst(), -p2.getSecond()));
            
            Sequence s3 = new Sequence();
            s3 = s3.juxtapose(Sequence.stair(0, p1.getFirst(), p1.getSecond()));
            s3 = s3.juxtapose(Sequence.stair(n, p2.getSecond(), -p2.getFirst()));
            
            Sequence s4 = new Sequence();
            s4 = s4.juxtapose(Sequence.stair(0, p1.getSecond(), p1.getFirst()));
            s4 = s4.juxtapose(Sequence.stair(n, p2.getSecond(), -p2.getFirst()));
            
            Sequence[] sequences = {s1,s2,s3,s4};
            
            for(var s : sequences) {
              
              pw.println(s.toString() + "," + Integer.toString(n) + "," + s.size());
            }
          }
        }  
      }
      pw.flush();
      pw.close();
      */
      /*
      var chords = Pcs12.getChords();
      
      PrintWriter pw = new PrintWriter("d:/pcs12.csv");
      pw.println("Pcs12, PITCHES, INTERVAL_VECTOR, FORTE_NUMBER");
      for(var chord : chords) {
        String forteNumber = chord.getForteNumber() + "+" + Integer.toString(chord.getForteNumberRotation());
        String iv = chord.getIntervalVector().toString();
        String pitches = chord.asSequence().toString();
        pw.println(chord.toString() + ", " + pitches + ", " +  iv + ", " + forteNumber);
        
      }
      pw.flush();
      pw.close();
      */
//      TreeSet<Rhythm48> mr = Rhythm48.Generate();
//      
//      FileOutputStream os = new FileOutputStream("d:/rhythm48.bin");
//      ObjectOutputStream oos = new ObjectOutputStream(os);
//      oos.writeObject(mr);
//      oos.flush();
//      oos.close();
      
      
      //var sequencer = MidiSystem.getSequencer();
      //sequencer.open();
      //sequencer.setSequence(sequence);

      // Start playing
      //sequencer.start();
//      TreeSet<Pcs12> t0 = Pcs12.getChords();
//      TreeSet<Pcs12> t = new TreeSet<Pcs12>();
//      Pcs12 scale = Pcs12.parse("07-43.11");
//      Predicate<Pcs12> pred = new SubsetOf(scale);
//      
//      for(Pcs12 r : t0){
//        if(pred.apply(r) && r.getK() == 4) {
//          t.add(r);
//        }
//      }
//      
//      var rel = new FiniteRelation<Pcs12, String>(t, (Pcs12 c) -> c.getForteNumber());
//      
//      rel.writeToCSV(Printers.PCS12Printer, Printers.stringPrinter, "d:/tetrachord-fortenumbers.csv");
      
//      FiniteRelation<Pcs12, Integer> rel = new FiniteRelation<>(
//          Pcs12.getChords().stream()
//          .filter((c) -> c.getK() == 4).filter(Functional.convertFromGuava(new SubsetOf(Pcs12.parse("07-43.11"))))
//          .collect(Collectors.toCollection(TreeSet<Pcs12>::new)),
//          IntStream.range(0, 12).boxed().collect(Collectors.toList()),
//          Relation.fromBiPredicate((Pcs12 c, Integer i) -> c.get(i))
//          );
//      
//      rel.writeToCSV(Printers.PCS12Printer, Printers.integerPrinter, "d:/tetrachords");
      
//       System.out.println("Started...");
 
//       TreeSet<HexadecimalWord> t = HexadecimalWord.getRhythms16();
//       PrintWriter p = new PrintWriter("d:/sci.csv"); 
//       
//       CollectionUtils.filter(t, new ShadowContourIsomorphic());
//       
//       for(HexadecimalWord r : t) {
//           p.println(String.format("%02d-%03d.%02d", r.getK(), r.getOrder(), r.getPhase())+ "\t" + r.toString());  
//       }
//         
//       p.close();
       
//       
//       
//       Double mins[] = new Double[32];
//       Double maxs[] = new Double[32];
//       
//       for(int i=0;i<32;i++) {
//         mins[i] = Double.MAX_VALUE;
//         maxs[i] = Double.MIN_VALUE;
//       }
//       
//       int counter = 0;
//       CompositionEnumeration ce = new CompositionEnumeration(32);
//       while(ce.hasMoreElements()) {
//         Sequence comp = ce.nextElement().asSequence();
//         double e = comp.entropy();
//         if(e<mins[comp.size()-1]) mins[comp.size()-1] = e;
//         if(e>maxs[comp.size()-1]) maxs[comp.size()-1] = e;
//         
//         System.out.println("" + String.format("%.04f", 100.0 * ((double)++counter / 2147483648.0)) + "%");
//       }
//       
//       PrintWriter p = new PrintWriter("d:/minmaxentropy32.txt");
//       p.println("k, min, max");
//       for(int i=0;i<32;i++) {
//         p.println("" + i+1 + ", " +  mins[i] + ", " + maxs[i]);
//       }
//       
//       p.flush();
//       p.close();
      
//      TreeSet<Pcs12> t0 = Pcs12.getChords();
//      TreeSet<Pcs12> t = new TreeSet<Pcs12>();
//      Pcs12 scale = Pcs12.parse("07-43.11");
//      Predicate<Pcs12> pred = new SubsetOf(scale);
//      
//      for(Pcs12 r : t0){
//        if(pred.apply(r) && r.getK() == 4) {
//          t.add(r);
//        }
//      }
//      
//     
//      Relation<Pcs12, Pcs12> rel_horiz = Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1)));
//      
//      MarkableDirectedGraph<Pcs12> d = new MarkableDirectedGraph<>(t, rel_horiz);
//      PrintWriter p = new PrintWriter("d:/horiz.txt");
//      for(Pcs12 u : d.getVertices()) {
//        for(Pcs12 v : d.getNeighbors(u)) {
//          p.println("" + u.toString() + ", " +  v.toString());
//        }
//      }
//      
//      p.flush();
//      p.close();
//      
    } catch (Exception e) {
        e.printStackTrace();
    }


  }

}
