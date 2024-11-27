package name.ncg777.Maths.Apps;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import name.ncg777.Maths.Enumerations.MixedRadixEnumeration;

import javax.swing.event.ChangeEvent;
import javax.swing.JButton;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

public class MixedRadix {

  private JFrame frmMixedRadix;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MixedRadix window = new MixedRadix();
          window.frmMixedRadix.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public MixedRadix() {
    initialize();
  }
  private void updateTextArea(){
    if(str.size() >= current){
      str.set(current-1, textArea.getText());
    }
    current = slider.getValue();
    if(str.size() >= current){
      textArea.setText(str.get(current-1));
    }
  }
  ArrayList<String> str = new ArrayList<String>();
  JTextArea textArea = new JTextArea();
  JSpinner spinner = new JSpinner();
  JSlider slider = new JSlider();
  int current = 1;
  private final JScrollPane scrollPane_1 = new JScrollPane();
  private final JTextArea txtrOutput = new JTextArea();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmMixedRadix = new JFrame();
    frmMixedRadix.setTitle("Mixed radix");
    frmMixedRadix.setBounds(100, 100, 560, 267);
    frmMixedRadix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    str.add("");str.add("");str.add("");
    JScrollPane scrollPane = new JScrollPane();
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        updateTextArea();
      }
    });
    
    slider.setMinimum(1);
    slider.setValue(1);
    slider.setPaintLabels(true);
    slider.setMaximum(3);
    slider.setMajorTickSpacing(1);

    
    spinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        int m = (Integer)spinner.getValue();
        while(str.size() > m){str.remove(str.size()-1);}
        while(str.size() < m){str.add("");}
        if(slider.getValue() > m){
          slider.setValue(m); updateTextArea();
        }

        slider.setMaximum(m);
      }
    });
    spinner.setModel(new SpinnerNumberModel(3, 2, null, 1));
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        updateTextArea();
        ArrayList<String[]> s = new ArrayList<String[]>();
        int[] base = new int[str.size()];
        for(int i=0;i<str.size();i++){
          String[] sa = str.get(i).split("\n");
          if(sa.length == 0){return;}
          s.add(sa);
          base[i] = sa.length;
        }
        MixedRadixEnumeration mre = new MixedRadixEnumeration(base);
        String output = "";
        while(mre.hasMoreElements()){
          int[] e = mre.nextElement();
          for(int i=0;i<e.length;i++){
            output += s.get(i)[e[i]] + ((i!=e.length-1)?" ":"");
          }
          output+="\n";
        }
        txtrOutput.setText(output);
      }
    });
    
    JLabel lblWriteEachItem = new JLabel("Write each item on a separate line");
    
    JLabel lblOutput = new JLabel("Output");
    GroupLayout groupLayout = new GroupLayout(frmMixedRadix.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addComponent(slider, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addComponent(lblWriteEachItem))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(134)
              .addComponent(lblOutput))
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblWriteEachItem)
            .addComponent(lblOutput))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(scrollPane_1)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(btnGenerate)
                .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addGap(8))
            .addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
    );
    
    scrollPane_1.setViewportView(txtrOutput);
    
    
    scrollPane.setViewportView(textArea);
    frmMixedRadix.getContentPane().setLayout(groupLayout);
  }
}
