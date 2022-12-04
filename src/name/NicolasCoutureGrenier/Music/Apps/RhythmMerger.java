package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.NicolasCoutureGrenier.Music.Rn;

public class RhythmMerger {

  private JFrame frmRhythmMerger;
  private JTextField txtRhythma;
  private JTextField txtRhythmb;
  private JTextField txtResult;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmMerger window = new RhythmMerger();
          window.frmRhythmMerger.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RhythmMerger() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRhythmMerger = new JFrame();
    frmRhythmMerger.setResizable(false);
    frmRhythmMerger.setTitle("Rhythm Merger");
    frmRhythmMerger.setBounds(100, 100, 450, 212);
    frmRhythmMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythmA = new JLabel("Rhythm A:");
    lblRhythmA.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblRhythmB = new JLabel("Rhythm B:");
    lblRhythmB.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtRhythma = new JTextField();
    txtRhythma.setColumns(10);
    
    txtRhythmb = new JTextField();
    txtRhythmb.setColumns(10);
    
    JButton btnMerge = new JButton("Merge");
    btnMerge.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(comboBox.getSelectedItem()==Rn.Hex) {
          R16List a = R16List.parseR16Seq(txtRhythma.getText());
          R16List b = R16List.parseR16Seq(txtRhythmb.getText());
          R16List o = new R16List();
          
          int n = Numbers.lcm(a.size(), b.size())*2;
          
          boolean alt = true;
          int ca = 0;
          int cb = 0;
          
          for(int i=0;i<n;i++) {
            if(alt) {
              o.add(a.get((ca++)%a.size()));
            }
            else {
              o.add(b.get((cb++)%b.size()));
            }
            alt = !alt;
          }
          
          txtResult.setText(o.toString());
        } else if(comboBox.getSelectedItem()==Rn.Octal) {
          R12List a = R12List.parseR12Seq(txtRhythma.getText());
          R12List b = R12List.parseR12Seq(txtRhythmb.getText());
          R12List o = new R12List();
          
          int n = Numbers.lcm(a.size(), b.size())*2;
          
          boolean alt = true;
          int ca = 0;
          int cb = 0;
          
          for(int i=0;i<n;i++) {
            if(alt) {
              o.add(a.get((ca++)%a.size()));
            }
            else {
              o.add(b.get((cb++)%b.size()));
            }
            alt = !alt;
          }
          
          txtResult.setText(o.toString());
        } else if(comboBox.getSelectedItem()==Rn.Tribble) {
          R48List a = R48List.parseR48Seq(txtRhythma.getText());
          R48List b = R48List.parseR48Seq(txtRhythmb.getText());
          R48List o = new R48List();
          
          int n = Numbers.lcm(a.size(), b.size())*2;
          
          boolean alt = true;
          int ca = 0;
          int cb = 0;
          
          for(int i=0;i<n;i++) {
            if(alt) {
              o.add(a.get((ca++)%a.size()));
            }
            else {
              o.add(b.get((cb++)%b.size()));
            }
            alt = !alt;
          }
          
          txtResult.setText(o.toString());
        }
          
        
      }
    });
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmMerger.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(lblResult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRhythmB))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addComponent(btnMerge, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addComponent(txtRhythmb, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblRhythmA)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtRhythma, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addContainerGap(18, Short.MAX_VALUE)
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythmA)
            .addComponent(txtRhythma, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythmB)
            .addComponent(txtRhythmb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(btnMerge)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(31))
    );
    frmRhythmMerger.getContentPane().setLayout(groupLayout);
  }
}
