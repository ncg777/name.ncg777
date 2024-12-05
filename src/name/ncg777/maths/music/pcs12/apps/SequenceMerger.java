package name.ncg777.maths.music.pcs12.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.music.pcs12.Pcs12List;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class SequenceMerger {

  private JFrame frmChordSequenceMerger;
  private JTextField txtResult;
  private JTextArea txtArea = new JTextArea();
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceMerger window = new SequenceMerger();
          window.frmChordSequenceMerger.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceMerger() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmChordSequenceMerger = new JFrame();
    frmChordSequenceMerger.setResizable(false);
    frmChordSequenceMerger.setTitle("Chord Sequence TetragraphMerger");
    frmChordSequenceMerger.setBounds(100, 100, 450, 222);
    frmChordSequenceMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JButton btnMerge = new JButton("Merge");
    btnMerge.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ArrayList<Pcs12List> arr = new ArrayList<>();
        var ss = txtArea.getText().trim().split("\n+");
        for(var s :ss) {arr.add(Pcs12List.parseForte(s));}
        txtResult.setText(Pcs12List.merge(arr).toString((c) -> c.toForteNumberString()));
      }
    });
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    JScrollPane scrollPane = new JScrollPane();

    GroupLayout groupLayout = new GroupLayout(frmChordSequenceMerger.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblResult)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(btnMerge, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnMerge)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(19, Short.MAX_VALUE))
    );
    
    
    txtArea.setText("1-1.00 1-1.02 1-1.04\r\n1-1.05 1-1.07");
    scrollPane.setViewportView(txtArea);
    frmChordSequenceMerger.getContentPane().setLayout(groupLayout);
  }
}
