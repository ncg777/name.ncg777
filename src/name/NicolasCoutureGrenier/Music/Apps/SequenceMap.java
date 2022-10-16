package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SequenceMap {

  private JFrame frmSequenceMap;
  private JTextField textSequence;
  private JTextField textMap;
  private JTextField textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceMap window = new SequenceMap();
          window.frmSequenceMap.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceMap() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceMap = new JFrame();
    frmSequenceMap.setTitle("Sequence Map");
    frmSequenceMap.setBounds(100, 100, 450, 219);
    frmSequenceMap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblNewLabel = new JLabel("Sequence :");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textSequence = new JTextField();
    textSequence.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    textSequence.setColumns(10);
    
    JLabel lblNewLabel_1 = new JLabel("Map :");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textMap = new JTextField();
    textMap.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    textMap.setColumns(10);
    
    JButton btnMap = new JButton("Map");
    btnMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Sequence s = Sequence.parse(textSequence.getText());
          Sequence m = Sequence.parse(textMap.getText());       
          Sequence result = s.map(m);
          textResult.setText(result.toString().replaceAll("[()]", ""));
        } catch(Exception ex) {
          textResult.setText("Error");
        }
      }
    });
    btnMap.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblNewLabel_2 = new JLabel("Result :");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textResult = new JTextField();
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    textResult.setColumns(10);
    
    JButton btnFeedback = new JButton("Feedback");
    btnFeedback.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textSequence.setText(textResult.getText());
      }
    });
    btnFeedback.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    GroupLayout groupLayout = new GroupLayout(frmSequenceMap.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED))
            .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(textResult, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .addComponent(textMap, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .addComponent(textSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .addComponent(btnMap, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .addComponent(btnFeedback, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_1)
            .addComponent(textMap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(btnMap)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_2)
            .addComponent(textResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnFeedback)
          .addContainerGap(24, Short.MAX_VALUE))
    );
    frmSequenceMap.getContentPane().setLayout(groupLayout);
  }
}
