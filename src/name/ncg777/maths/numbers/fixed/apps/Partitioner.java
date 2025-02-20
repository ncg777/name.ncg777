package name.ncg777.maths.numbers.fixed.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.LineBorder;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.Quartal;
import name.ncg777.maths.numbers.fixed.Quartal.NaturalSequence;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence;

import java.awt.Color;
import javax.swing.JScrollPane;

public class Partitioner {

  private JFrame frmRPartitioner;
  private JTextField txtR;
  private JTextField txtPartition;
  private JTextArea txtResult;
  private JSpinner spinner;
  private JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Partitioner window = new Partitioner();
          window.frmRPartitioner.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Partitioner() {
    initialize();
  }

  private void initialize() {
    frmRPartitioner = new JFrame();
    frmRPartitioner.setTitle("Partitioner");
    frmRPartitioner.setBounds(100, 100, 456, 411);
    frmRPartitioner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblR = new JLabel("FixedLength:");
    lblR.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblR.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtR = new JTextField();
    txtR.setFont(new Font("Unifont", Font.PLAIN, 11));
    txtR.setColumns(10);
    
    JLabel lblPartition = new JLabel("Partition:");
    lblPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblPartition.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtPartition = new JTextField();
    txtPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
    txtPartition.setColumns(10);
    
    JButton btnPartition = new JButton("Partition");
    btnPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
    spinner = new JSpinner();
    spinner.setFont(new Font("Unifont", Font.PLAIN, 11));
    btnPartition.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var abc = (Cipher.Name)comboBox.getSelectedItem();
        
        NaturalSequence r = Quartal.instance.newNaturalSequence(abc, txtR.getText().trim());
        BinaryNatural r1 = r.toBinaryNatural();
        Sequence p0 = Sequence.parse(txtPartition.getText());
        Sequence p = p0.asOrdinalsUnipolar().addToEach(-1);
        int k = p.size();
        int pdistinct = p.distinct().size();
        int n= r1.getN();
        ArrayList<BinaryNatural> output = new ArrayList<BinaryNatural>();
        
        for(int i=0;i<pdistinct;i++) output.add(BinaryNatural.build(new BitSet(), n));
        for(int i=0;i<n;i++) {
          output.get(p.get(i%k)).set(i, r1.get(-1+n-i));
        }
        
        String strOut = "";
        for(int i=0; i<output.size();i++) {
          strOut += Quartal.instance.expand(Quartal.instance.newNaturalSequence(abc, output.get(i)), (int)spinner.getValue(), true).toString() + "\n";
        }
        txtResult.setText(strOut);       
      }
    });
    
    
    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
    JLabel lblMult = new JLabel("Mult:");
    lblMult.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblMult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JScrollPane scrollPane = new JScrollPane();
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmRPartitioner.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(7)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addComponent(lblMult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblPartition, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblR, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(comboBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
            .addComponent(txtPartition, Alignment.LEADING)
            .addComponent(btnPartition, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addComponent(txtR, Alignment.LEADING)
            .addComponent(spinner, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
          .addGap(19))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
          .addGap(12)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblR)
            .addComponent(txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblPartition)
            .addComponent(txtPartition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblMult))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnPartition)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
          .addContainerGap())
    );
    txtResult = new JTextArea();
    scrollPane.setViewportView(txtResult);
    txtResult.setBorder(new LineBorder(new Color(0, 0, 0)));
    txtResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 13));
    txtResult.setEditable(false);
    frmRPartitioner.getContentPane().setLayout(groupLayout);
  }
}
