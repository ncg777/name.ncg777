package name.NicolasCoutureGrenier.Music.Apps.ChordVisual;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.border.LineBorder;

import name.NicolasCoutureGrenier.Music.PCS12;

public class KeyVisual extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = -2303640931691547468L;
  PCS12 chord = null;
  JLabel lbl00 = new JLabel("");
  JLabel lbl01 = new JLabel("");
  JLabel lbl02 = new JLabel("");
  JLabel lbl03 = new JLabel("");
  JLabel lbl04 = new JLabel("");
  JLabel lbl05 = new JLabel("");
  JLabel lbl06 = new JLabel("");
  JLabel lbl07 = new JLabel("");
  JLabel lbl08 = new JLabel("");
  JLabel lbl09 = new JLabel("");
  JLabel lbl10 = new JLabel("");
  JLabel lbl11 = new JLabel("");
  private final JLabel lblName = new JLabel("");
  private final JLabel lblIV = new JLabel("");
  public void setChord(String chord) {this.setChord(PCS12.parse(chord));}
  public void setChord(PCS12 chord) {
    this.chord = chord;
    lbl00.setText(chord.get(0) ? "X":"");
    lbl01.setText(chord.get(1) ? "X":"");
    lbl02.setText(chord.get(2) ? "X":"");
    lbl03.setText(chord.get(3) ? "X":"");
    lbl04.setText(chord.get(4) ? "X":"");
    lbl05.setText(chord.get(5) ? "X":"");
    lbl06.setText(chord.get(6) ? "X":"");
    lbl07.setText(chord.get(7) ? "X":"");
    lbl08.setText(chord.get(8) ? "X":"");
    lbl09.setText(chord.get(9) ? "X":"");
    lbl10.setText(chord.get(10) ? "X":"");
    lbl11.setText(chord.get(11) ? "X":"");
    lblName.setText(chord.toString());
    lblIV.setText(chord.getIntervalVector().toString());
    this.repaint();
  }
  /**
   * Create the panel.
   */
  public KeyVisual() {
    setLayout(null);
    
    
    lbl01.setOpaque(true);
    lbl01.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl01.setForeground(Color.WHITE);
    lbl01.setBackground(Color.BLACK);
    lbl01.setBounds(0, 84, 75, 12);
    add(lbl01);
    
    
    lbl03.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl03.setOpaque(true);
    lbl03.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl03.setForeground(Color.WHITE);
    lbl03.setBackground(Color.BLACK);
    lbl03.setBounds(0, 69, 75, 12);
    add(lbl03);
    
    
    lbl06.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl06.setOpaque(true);
    lbl06.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl06.setForeground(Color.WHITE);
    lbl06.setBackground(Color.BLACK);
    lbl06.setBounds(0, 39, 75, 12);
    add(lbl06);
    
    
    lbl08.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl08.setOpaque(true);
    lbl08.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl08.setForeground(Color.WHITE);
    lbl08.setBackground(Color.BLACK);
    lbl08.setBounds(0, 24, 75, 12);
    add(lbl08);
    
    
    lbl10.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl10.setOpaque(true);
    lbl10.setForeground(new Color(255, 255, 255));
    lbl10.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl10.setBackground(new Color(0, 0, 0));
    lbl10.setBounds(0, 9, 75, 12);
    add(lbl10);
    
    
    lbl11.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl11.setOpaque(true);
    lbl11.setBackground(new Color(255, 255, 255));
    lbl11.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl11.setBounds(0, 1, 120, 15);
    add(lbl11);
    
    
    lbl09.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl09.setOpaque(true);
    lbl09.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl09.setBackground(Color.WHITE);
    lbl09.setBounds(0, 16, 120, 15);
    add(lbl09);
    
    
    lbl07.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl07.setOpaque(true);
    lbl07.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl07.setBackground(Color.WHITE);
    lbl07.setBounds(0, 31, 120, 15);
    add(lbl07);
    
    
    lbl05.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl05.setOpaque(true);
    lbl05.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl05.setBackground(Color.WHITE);
    lbl05.setBounds(0, 46, 120, 15);
    add(lbl05);
    
    
    lbl04.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl04.setOpaque(true);
    lbl04.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl04.setBackground(Color.WHITE);
    lbl04.setBounds(0, 61, 120, 15);
    add(lbl04);
    
    
    lbl02.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl02.setOpaque(true);
    lbl02.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl02.setBackground(Color.WHITE);
    lbl02.setBounds(0, 76, 120, 15);
    add(lbl02);
    
    
    lbl00.setBorder(new LineBorder(new Color(0, 0, 0)));
    lbl00.setOpaque(true);
    lbl00.setHorizontalAlignment(SwingConstants.RIGHT);
    lbl00.setBackground(Color.WHITE);
    lbl00.setBounds(0, 91, 120, 15);
    add(lbl00);
    lblName.setBackground(new Color(192, 192, 192));
    lblName.setBorder(new LineBorder(new Color(0, 0, 0)));
    lblName.setHorizontalAlignment(SwingConstants.CENTER);
    lblName.setBounds(0, 106, 120, 15);
    
    add(lblName);
    lblIV.setBackground(new Color(192, 192, 192));
    lblIV.setBorder(new LineBorder(new Color(0, 0, 0)));
    lblIV.setHorizontalAlignment(SwingConstants.CENTER);
    lblIV.setBounds(0, 121, 120, 15);
    
    add(lblIV);

  }
}
