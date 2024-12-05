package name.ncg777.maths.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.ncg777.maths.Necklace;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.TreeSet;
import java.awt.event.ActionEvent;

public class NecklaceGenerator {

  private JFrame frmNecklaceGenerator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          NecklaceGenerator window = new NecklaceGenerator();
          window.frmNecklaceGenerator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public NecklaceGenerator() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmNecklaceGenerator = new JFrame();
    frmNecklaceGenerator.setResizable(false);
    frmNecklaceGenerator.setTitle("Necklace generator");
    frmNecklaceGenerator.setBounds(100, 100, 607, 428);
    frmNecklaceGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmNecklaceGenerator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("N:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 11, 57, 21);
    frmNecklaceGenerator.getContentPane().add(lblNewLabel);
    
    JSpinner spinnerN = new JSpinner();
    spinnerN.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spinnerN.setBounds(74, 11, 57, 21);
    frmNecklaceGenerator.getContentPane().add(spinnerN);
    
    JLabel lblNewLabel_1 = new JLabel("K:");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_1.setBounds(20, 42, 46, 14);
    frmNecklaceGenerator.getContentPane().add(lblNewLabel_1);
    
    JSpinner spinnerK = new JSpinner();
    spinnerK.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spinnerK.setBounds(74, 39, 57, 21);
    frmNecklaceGenerator.getContentPane().add(spinnerK);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 73, 572, 298);
    frmNecklaceGenerator.getContentPane().add(scrollPane);
    
    JTextArea textArea = new JTextArea();
    scrollPane.setViewportView(textArea);
    
    JButton btnNewButton = new JButton("Generate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TreeSet<Necklace> output = Necklace.generate((Integer)spinnerN.getValue(), (Integer)spinnerK.getValue());
        StringBuilder sb = new StringBuilder();
        
        for(Necklace n: output) {
          sb.append(n.getOrder() + ", " + n.toString().replace("(", "").replace(")", "") + "\n");
        }
        textArea.setText(sb.toString());
        
      }
    });
    btnNewButton.setBounds(145, 10, 437, 53);
    frmNecklaceGenerator.getContentPane().add(btnNewButton);
  }
}
