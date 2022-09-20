package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg.Maths.DataStructures.Sequence;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Mapper {

  private JFrame frmMapper;
  private JTextField txtSequence;
  private JTextField txtMap;
  private JTextField txtResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Mapper window = new Mapper();
          window.frmMapper.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Mapper() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmMapper = new JFrame();
    frmMapper.setTitle("Mapper");
    frmMapper.setBounds(100, 100, 443, 165);
    frmMapper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequence = new JLabel("Sequence :");
    lblSequence.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblMap = new JLabel("Map :");
    lblMap.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtSequence = new JTextField();
    txtSequence.setColumns(10);
    
    txtMap = new JTextField();
    txtMap.setColumns(10);
    
    JButton btnMap = new JButton("Map");
    btnMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence seq = Sequence.parse(txtSequence.getText());
        Sequence map = Sequence.parse(txtMap.getText());
        int sz = map.size();
        
        if(seq.getMax() >= sz || seq.getMin() < 0) {
          txtResult.setText("Error");
        } else {
          txtResult.setText(seq.map(map).toString());
        }
      }
    });
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmMapper.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
              .addComponent(lblMap, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblSequence, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblResult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(txtSequence, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
            .addComponent(txtMap, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
            .addComponent(btnMap, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequence)
            .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblMap)
            .addComponent(txtMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnMap)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblResult))
          .addContainerGap(149, Short.MAX_VALUE))
    );
    frmMapper.getContentPane().setLayout(groupLayout);
  }
}
