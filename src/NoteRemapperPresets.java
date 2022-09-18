import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

public class NoteRemapperPresets {
private static String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
private static String bidule_presets_start = "<BidulePresets xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation='BidulePresets.xsd' type=\"com.plogue.MIDINoteRemapperMulti\">";
private static String bidule_presets_end = "</BidulePresets>";
private static String preset_format = "<Preset id=\"%d\" name=\"%s\" readonly=\"yes\">";
private static String preset_end = "</Preset>";
private static String preset_head = "<Parameter id=\"-7\" userMin=\"0\" userMax=\"1\" lockedForRandom=\"true\" mutationFactor=\"0.0\">1</Parameter>\n<Parameter id=\"-2\" userMin=\"0\" userMax=\"2\" lockedForRandom=\"true\" mutationFactor=\"0.0\">0.0</Parameter>";

private static String parameter_format = "<Parameter id=\"%d\" userMin=\"0\" userMax=\"127\" lockedForRandom=\"true\" mutationFactor=\"0.0\">%d</Parameter>";

static public void main(String[] args) throws FileNotFoundException{
  PrintWriter pw = new PrintWriter("d:/com.plogue.MIDINoteRemapperMulti_2.bprs");
  pw.println(header);
  pw.println(bidule_presets_start);
  int pid = 0;
  ArrayList<String> cl = new ArrayList<String>();
  cl.addAll(PCS12.getChordDict().keySet());
  
  for(int j = cl.size()-1;j>=0;j--){
    PCS12 c = PCS12.getChordDict().get(cl.get(j));
    if(c.isEmpty()){continue;}
    pw.println(String.format(preset_format,pid++,c.toString()));
    pw.println(preset_head);
    
    Sequence s = c.asSequence();
    Map<Integer, Integer> m = new TreeMap<Integer,Integer>();
    int k = 0;
    int dir = 1;
    for(int i=0;i<12;i++){
      m.put(i, s.get(k));
      if(c.getK()>1){
        k+=dir;
        if(k==s.size()){k-=2;dir=-1;}
        else if(k==-1){k+=2;dir=1;}
      }
    }
    for(int id=0;id<128;id++){
      pw.println(String.format(parameter_format, id, (12*(id/12))+m.get(id%12)));
    }
    pw.println(preset_end);
    
  }
  pw.println(bidule_presets_end);
  
  
  pw.close();
  
}
  
  
}
