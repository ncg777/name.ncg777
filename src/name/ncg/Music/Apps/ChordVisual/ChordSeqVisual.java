package name.ncg.Music.Apps.ChordVisual;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class ChordSeqVisual {
  private JPanel panel = new JPanel();
  JScrollPane scrollPane = new JScrollPane();
  private JFrame frmChordseqvisual;
  
  private JTextField textField;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ChordSeqVisual window = new ChordSeqVisual();
          window.frmChordseqvisual.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public ChordSeqVisual() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmChordseqvisual = new JFrame();
    frmChordseqvisual.setTitle("ChordSeq Visual");
    frmChordseqvisual.setBounds(100, 100, 741, 250);
    frmChordseqvisual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    panel.setBorder(new LineBorder(new Color(0, 0, 0)));
    
    
    scrollPane.setViewportView(panel);
    panel.setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Chords:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textField = new JTextField();
    textField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateVisual();
      }
    });
    textField.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmChordseqvisual.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(10)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
              .addGap(5)
              .addComponent(textField, GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE))
          .addGap(9))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(8)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(3)
              .addComponent(lblNewLabel))
            .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(14)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
          .addGap(9))
    );
    frmChordseqvisual.getContentPane().setLayout(groupLayout);
  }
  
  private void updateVisual() {
    while(panel.getComponentCount() > 0) panel.remove(0);
    String[] parsed = textField.getText().trim().split("\\s+");
    for(int i=0;i<parsed.length;i++) {
      KeyVisual kv = new KeyVisual();
      kv.setChord(parsed[i].trim());
      
      panel.add(kv);
      kv.setBounds(i*120, 0, 120,136);
    }
    panel.setPreferredSize(new Dimension(parsed.length*120, 136));
    scrollPane.setViewportView(panel);
    scrollPane.repaint();
  }
}
