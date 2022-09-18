import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class NGram {

  public static void main(String[] args) throws IOException {
    
    BufferedReader bis = new BufferedReader(
      new InputStreamReader(new FileInputStream(args[0])));
    
    // "([a-zA-Z[\\-'\\.!?\\(\\)\\&\\:\"\\,]]+)"
    PatternTokenizer p = new PatternTokenizer(Pattern.compile("([a-zA-Z[\\-']]+)"), 1);
    p.setReader(bis);
    PrintWriter w = new PrintWriter(args[0] + "words.txt");
    p.reset();
    
    
    while(p.incrementToken()){
      w.println(p.getAttribute(CharTermAttribute.class));
      
    }
    
    p.close();
    w.close();

  }

}
