package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSpinner;

import java.awt.BorderLayout;

import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;

import name.ncg.Maths.Graphs.DiGraph;
import name.ncg.Maths.Graphs.GraphUtils;
import name.ncg.Music.Rhythm16Partition;
import name.ncg.Music.RhythmListPrinters.Rhythm16PartitionListPrinter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.zip.ZipInputStream;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import java.awt.Color;

public class RandomR16PartitionGenerator {

  private JFrame frmRhythmicPartitionGenerator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RandomR16PartitionGenerator window = new RandomR16PartitionGenerator();
          window.frmRhythmicPartitionGenerator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public RandomR16PartitionGenerator() throws IOException, ClassNotFoundException {
    initialize();
  }

  private DiGraph<Rhythm16Partition> graph;
  /**
   * Initialize the contents of the frame.
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  @SuppressWarnings("unchecked")
  private void initialize() throws IOException, ClassNotFoundException {
    frmRhythmicPartitionGenerator = new JFrame();
    frmRhythmicPartitionGenerator.setBackground(Color.DARK_GRAY);
    frmRhythmicPartitionGenerator.setTitle("Rhythmic partition generator");
    frmRhythmicPartitionGenerator.setBounds(100, 100, 450, 300);
    frmRhythmicPartitionGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JPanel panel = new JPanel();
    panel.setBackground(Color.DARK_GRAY);
    frmRhythmicPartitionGenerator.getContentPane().add(panel, BorderLayout.NORTH);

    
    final JSpinner spinner = new JSpinner();
    spinner.setForeground(Color.WHITE);
    spinner.setBackground(Color.BLACK);
    spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
    
    final JCheckBox chckbxCircuit = new JCheckBox("Circuit");
    chckbxCircuit.setBackground(Color.BLACK);
    chckbxCircuit.setForeground(Color.WHITE);
    GroupLayout gl_panel = new GroupLayout(panel);
    gl_panel.setHorizontalGroup(
      gl_panel.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
          .addContainerGap(236, Short.MAX_VALUE)
          .addComponent(chckbxCircuit)
          .addGap(26)
          .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
          .addContainerGap())
    );
    gl_panel.setVerticalGroup(
      gl_panel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_panel.createSequentialGroup()
          .addContainerGap()
          .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(chckbxCircuit))
          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    panel.setLayout(gl_panel);
    
    final JTextArea textArea = new JTextArea();
    textArea.setBackground(Color.BLACK);
    textArea.setForeground(Color.WHITE);
    textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
    frmRhythmicPartitionGenerator.getContentPane().add(textArea, BorderLayout.CENTER);
    
    final JButton btnGenerate = new JButton("Loading...");
    btnGenerate.setForeground(Color.WHITE);
    btnGenerate.setBackground(Color.BLACK);
    btnGenerate.setEnabled(false);
    new Thread(new Runnable(){

      @Override
      public void run() {
        ZipInputStream zis = new ZipInputStream(
          Object.class.getResourceAsStream("/resources/graph.data.zip"));
        
        try {
          zis.getNextEntry();
          ObjectInputStream ois;
          ois = new ObjectInputStream(zis);
          graph = (DiGraph<Rhythm16Partition>)ois.readObject();
          ois.close();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        btnGenerate.setText("Generate");
        btnGenerate.setEnabled(true);
      }
      
    }).start();
    
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        btnGenerate.setEnabled(false);
        try{
        Rhythm16PartitionListPrinter p = new Rhythm16PartitionListPrinter(new PrintWriter(os));
        if(chckbxCircuit.isSelected()) {
          p.apply(GraphUtils.getRandomCircuit(graph, (Integer)spinner.getValue()));
        } else {
          p.apply(GraphUtils.getRandomWalk(graph, (Integer)spinner.getValue()));
        }
        p.close();
        textArea.setText(os.toString());
        } catch(Exception ex) {
          ;
        }
        btnGenerate.setEnabled(true);
      }
    });
    frmRhythmicPartitionGenerator.getContentPane().add(btnGenerate, BorderLayout.SOUTH);
  }
}
