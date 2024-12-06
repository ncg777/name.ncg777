package name.ncg777.maths.phrases.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.Matrix;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JComboBox;

public class FourCharsMatrixSequencer {

  private JFrame frmRseq;
  private JTextArea textFourCharsList;
  private JTextField textSequence;
  private JTextArea textResult;
  private JComboBox<Alphabet.Name> comboBox = new JComboBox<Alphabet.Name>(Alphabet.Name.values());

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          FourCharsMatrixSequencer window = new FourCharsMatrixSequencer();
          window.frmRseq.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public FourCharsMatrixSequencer() {
    initialize();
  }

  private void initialize() {
    frmRseq = new JFrame();
    frmRseq.setTitle("FourCharsMatrixSequencer");
    frmRseq.setBounds(100, 100, 471, 587);
    frmRseq.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblNewLabel = new JLabel("Matrix :");
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblNewLabel_1 = new JLabel("Sequence :");
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textSequence = new JTextField();
    textSequence.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSequence.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          try {
            Sequence s = Sequence.parse(textSequence.getText());
            int n = s.size();
            String[] lines = textFourCharsList.getText().split("\n+");
            int m = lines.length;
            Matrix<String> mat = new Matrix<String>(m, n);
            
            for(int i=0;i<m;i++) {
              String l = lines[i].replaceAll("\\s+", "");
              if(l.length()%4!=0) throw new IllegalArgumentException();
              
              String _cells[] = new String[l.length()/4];
              for(int x=0;x<l.length()/4;x++) {
                _cells[x] = l.substring(x*4, (x+1)*4);
              }
              for(int j=0;j<n;j++) {
                String cell = _cells[s.get(j)%_cells.length];
                StringBuilder sb = new StringBuilder(cell);
                sb.insert(2, " ");
                
                mat.set(i,j, sb.toString());
              }
            }
             
            textResult.setText(mat.toString());
          } catch(Exception ex) {
            textResult.setText("Index out of bounds");
          }
        }
      
    });
    
    JLabel lblNewLabel_2 = new JLabel("Result :");
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    
    GroupLayout groupLayout = new GroupLayout(frmRseq.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(textSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_1)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblNewLabel_2)
            .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
          .addContainerGap())
    );
    
    textResult = new JTextArea();
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane_1.setViewportView(textResult);
    textResult.setColumns(10);
    
    textFourCharsList = new JTextArea();
    textFourCharsList.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane.setViewportView(textFourCharsList);
    textFourCharsList.setColumns(10);
    frmRseq.getContentPane().setLayout(groupLayout);
  }
}
