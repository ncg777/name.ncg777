package name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Font;

import name.NicolasCoutureGrenier.CS.DataStructures.HomoPair;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.DefaultComboBoxModel;
import name.NicolasCoutureGrenier.Music.Rn;

public class Application {

  private JFrame frmKernelEvaluator;
  private JTextField delta;
  private JComboBox<Rn> comboBox_1 = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Application window = new Application();
          window.frmKernelEvaluator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Application() {
    initialize();
  }
  JComboBox<String> comboBox = new JComboBox<String>();
  private JTextField rhythm;
  private JTextField seq;
  JTextArea params = new JTextArea();
  private Kernel getSelectedKernel() {
    return Engine.KernelsByName.get((String)comboBox.getSelectedItem());
  }
  JCheckBox chckbxDelta = new JCheckBox("Delta");
  JTextArea doc = new JTextArea();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmKernelEvaluator = new JFrame();
    frmKernelEvaluator.setTitle("Kernel Evaluator");
    frmKernelEvaluator.setBounds(100, 100, 481, 546);
    frmKernelEvaluator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    comboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        doc.setText(getSelectedKernel().getDocumentation());
        params.setText(Kernel.getEmptyParameterString(getSelectedKernel()));
      }
    });
    
    for(String s : Engine.KernelsByName.keySet()) {
      comboBox.addItem(s);
    }
    comboBox.setSelectedIndex(0);
    
    JLabel lblKernel = new JLabel("Kernel :");
    lblKernel.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblKernel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblDocumentation = new JLabel("Documentation");
    lblDocumentation.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblDocumentation.setHorizontalAlignment(SwingConstants.CENTER);
    
    
    
    JLabel lblParameters = new JLabel("Parameters");
    lblParameters.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblParameters.setHorizontalAlignment(SwingConstants.CENTER);
    
    JLabel lblOutput = new JLabel("\u0394 Sequence :");
    lblOutput.setHorizontalAlignment(SwingConstants.RIGHT);
    lblOutput.setFont(new Font("Unifont", Font.PLAIN, 11));
    
    delta = new JTextField();
    delta.setEditable(false);
    delta.setFont(new Font("Unifont", Font.PLAIN, 11));
    delta.setColumns(10);
    
    JButton btnEvaluate = new JButton("Evaluate");
    btnEvaluate.setFont(new Font("Unifont", Font.PLAIN, 11));
    btnEvaluate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          btnEvaluate.setEnabled(false);
          Engine engine = new Engine(chckbxDelta.isSelected());
          HomoPair<Sequence> result = null;
          if(comboBox_1.getSelectedItem() == Rn.Hex) {
            result = engine.evaluate(
              R16List.parseR16Seq(rhythm.getText()).asRhythm(), 
              params.getText(), 
              getSelectedKernel());
          }
          if(comboBox_1.getSelectedItem() == Rn.Octal) {
            result = engine.evaluate(
              R12List.parseR12Seq(rhythm.getText()).asRhythm(), 
              params.getText(), 
              getSelectedKernel());
          }
          if(comboBox_1.getSelectedItem() == Rn.Tribble) {
            result = engine.evaluate(
              R48List.parseR48Seq(rhythm.getText()).asRhythm(), 
              params.getText(), 
              getSelectedKernel());
          }
          /* */
          
          delta.setText(result.getFirst().toString().replace("(", "").replace(")", ""));
          seq.setText(result.getSecond().toString().replace("(", "").replace(")", ""));  
        } catch(RuntimeException x) {
          String error = "Error!";
          delta.setText(error);
          seq.setText(error);
        } finally {
          btnEvaluate.setEnabled(true);
        }
      }
    });
    
    JLabel lblRhythm = new JLabel("Rhythm :");
    lblRhythm.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    rhythm = new JTextField();
    rhythm.setFont(new Font("Unifont", Font.PLAIN, 11));
    rhythm.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Sequence :");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("Unifont", Font.PLAIN, 11));
    
    seq = new JTextField();
    seq.setEditable(false);
    seq.setFont(new Font("Unifont", Font.PLAIN, 11));
    seq.setColumns(10);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    

    
    
    GroupLayout groupLayout = new GroupLayout(frmKernelEvaluator.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(btnEvaluate, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
              .addContainerGap())
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDocumentation, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
              .addGap(5))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(lblKernel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRhythm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(comboBox, 0, 399, Short.MAX_VALUE)
                .addGroup(groupLayout.createSequentialGroup()
                  .addComponent(rhythm, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(chckbxDelta)))
              .addContainerGap())
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOutput, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(seq, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addComponent(delta, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
              .addContainerGap())
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addComponent(lblParameters, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
              .addContainerGap())))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblKernel)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
          .addGap(14)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythm)
            .addComponent(rhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(chckbxDelta))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblDocumentation)
            .addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
          .addGap(16)
          .addComponent(lblParameters, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnEvaluate, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(delta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel)
            .addComponent(seq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(79))
    );
    doc = new JTextArea();
    scrollPane_1.setViewportView(doc);
    doc.setFont(new Font("Unifont", Font.PLAIN, 13));
    doc.setEditable(false);
    scrollPane.setViewportView(params);
    
    
    params.setFont(new Font("Unifont", Font.PLAIN, 13));
    frmKernelEvaluator.getContentPane().setLayout(groupLayout);
  }
}
