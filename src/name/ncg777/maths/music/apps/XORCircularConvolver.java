package name.ncg777.maths.music.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.BinaryNumber;
import name.ncg777.maths.phrases.QuartalNumbersSequence;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class XORCircularConvolver {

  private JFrame frmXorCircularConvolver;
  private JTextField txtCarrier;
  private JTextField txtImpulse;
  private JTextField txtResult;
  private JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          XORCircularConvolver window = new XORCircularConvolver();
          window.frmXorCircularConvolver.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public XORCircularConvolver() {
    initialize();
  }

  private void initialize() {
    frmXorCircularConvolver = new JFrame();
    frmXorCircularConvolver.setResizable(false);
    frmXorCircularConvolver.setTitle("XOR Circular Convolver");
    frmXorCircularConvolver.setBounds(100, 100, 450, 186);
    frmXorCircularConvolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblCarrier = new JLabel("Carrier :");
    lblCarrier.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblImpulse = new JLabel("Impulse :");
    lblImpulse.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtCarrier = new JTextField();
    txtCarrier.setColumns(10);
    
    txtImpulse = new JTextField();
    txtImpulse.setColumns(10);
    
    JButton btnConvolve = new JButton("Convolve");
    btnConvolve.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var abc = (Cipher.Name)comboBox.getSelectedItem();
        BinaryNumber carrier = new QuartalNumbersSequence(abc, txtCarrier.getText()).toBinaryWord().reverse();
        BinaryNumber impulse = new QuartalNumbersSequence(abc, txtImpulse.getText()).toBinaryWord().reverse();
        
        BitSet bs = new BitSet(carrier.getN());
        
        for(int i=0;i<carrier.getN();i++) {
          if(carrier.get(i)) {
            for(int j=0;j<impulse.getN();j++) {
              int index = (i+j)%carrier.getN();
              bs.set(index, impulse.get(j) != bs.get(index));
            }  
          }
        }
        txtResult.setText(
            (new QuartalNumbersSequence(
                abc,
                (new BinaryNumber(bs, carrier.getN())).reverse()
            )).toString());
      }
    });
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmXorCircularConvolver.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addComponent(lblResult, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblCarrier, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblImpulse, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(txtImpulse, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
            .addComponent(txtCarrier, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
            .addComponent(btnConvolve, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblCarrier)
            .addComponent(txtCarrier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblImpulse)
            .addComponent(txtImpulse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnConvolve)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(35, Short.MAX_VALUE))
    );
    frmXorCircularConvolver.getContentPane().setLayout(groupLayout);
  }
}
