import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;

public class GateTriggerSequencerPresets {
  private static String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
  private static String bidule_presets_start = "<BidulePresets xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation='BidulePresets.xsd' type=\"com.plogue.GateTriggerSequencer_32\">";
  private static String bidule_presets_end = "</BidulePresets>";
  private static String preset_format = "<Preset id=\"%d\" name=\"%s\" readonly=\"yes\">";
  private static String preset_end = "</Preset>";
  private static String preset_head = "<Parameter id=\"-7\" userMin=\"0\" userMax=\"1\" lockedForRandom=\"true\" mutationFactor=\"0.0\">1</Parameter>\n<Parameter id=\"-2\" userMin=\"0\" userMax=\"2\" lockedForRandom=\"true\" mutationFactor=\"0.0\">0.0</Parameter>";

  private static String parameter_format = "<Parameter id=\"%d\" userMin=\"0\" userMax=\"1\" lockedForRandom=\"true\" mutationFactor=\"0.0\">%d</Parameter>";

  static public void main(String[] args) throws FileNotFoundException{
    PrintWriter pw = new PrintWriter("d:/com.plogue.GateTriggerSequencer_32_2.bprs");
    pw.println(header);
    pw.println(bidule_presets_start);
    int pid = 0;
    Map<String,Rhythm16> cl = new TreeMap<String,Rhythm16>();
    TreeSet<Rhythm16> r = Rhythm16.getRhythms16();
    CollectionUtils.filter(r, new ShadowContourIsomorphic());
    for(Rhythm16 x : r){cl.put(x.getId(), x);}
    ArrayList<String> ids = new ArrayList<String>();
    ids.addAll(cl.keySet());
    ids.sort(null);
    
    for(int j = ids.size()-1;j>=0;j--){
      Rhythm16 c = cl.get(ids.get(j));
      
      pw.println(String.format(preset_format,pid++,c.getId()));
      pw.println(preset_head);
      
      for(int id=0;id<32;id++){
        pw.println(String.format(parameter_format, id, c.get(id)?1:0));
      }
      pw.println(String.format(parameter_format, 32, 1));
      pw.println(String.format(parameter_format, 33, 16));
      pw.println(preset_end);
      
    }
    pw.println(bidule_presets_end);
    
    
    pw.close();
    
  }
    
    
}
