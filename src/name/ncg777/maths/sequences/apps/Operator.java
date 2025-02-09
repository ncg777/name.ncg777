package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import name.ncg777.maths.Numbers;
import name.ncg777.maths.sequences.Sequence;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Operator {

  private JFrame frmAddSequences;
  private JTextField textX;
  private JTextField textY;
  private JTextField textMultArt;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Operator window = new Operator();
          window.frmAddSequences.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private Map<Operation,BiFunction<Integer, Integer, Integer>> ops;
  private enum Scale {
    Recycled, 
    Articulated
  }
  private int getLength(Sequence x, Sequence y) {
    return
        scale == Scale.Recycled ? (int)Numbers.lcm(x.size(), y.size()) : x.size()*y.size();
  }
  private enum Operation {
    Add, 
    Mult,
    And,
    Or,
    Xor
  }
  private Scale scale = Scale.Recycled;
  private Operation operation = Operation.Add;
  private JComboBox<Scale> comboScale;
  private JComboBox<Operation> comboOperation;
  public Operator() {
    ops = 
        new TreeMap<Operation,BiFunction<Integer,Integer,Integer>>();
    
    ops.put(Operation.Add, (x,y) -> x+y);
    ops.put(Operation.Mult, (x,y) -> x*y);
    ops.put(Operation.And, (x,y) -> x&y);
    ops.put(Operation.Or, (x,y) -> x|y);
    ops.put(Operation.Xor, (x,y) -> x^y);
    initialize();
  }

  private Sequence getResult(Sequence x, Sequence y) {
    
    int n = getLength(x,y);
    
    Sequence o = new Sequence();
    for(int i=0;i<n;i++) {
      o.add(this.ops.get(operation)
          .apply(
              x.get(i%x.size()),
              this.scale == Scale.Recycled ? y.get(i%y.size()) : y.get(i/x.size())
           )
      );
    }
    return o;
  }
  
  private void initialize() {
   
    frmAddSequences = new JFrame();
    frmAddSequences.setTitle("Operator");
    frmAddSequences.setBounds(100, 100, 450, 260);
    frmAddSequences.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmAddSequences.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Sequence x:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(0, 11, 76, 14);
    frmAddSequences.getContentPane().add(lblNewLabel);
    
    textX = new JTextField();
    textX.setBounds(80, 8, 344, 17);
    frmAddSequences.getContentPane().add(textX);
    textX.setColumns(10);
    
    textY = new JTextField();
    textY.setColumns(10);
    textY.setBounds(80, 31, 344, 17);
    frmAddSequences.getContentPane().add(textY);
    
    JLabel lblSequenceY = new JLabel("y:");
    lblSequenceY.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSequenceY.setBounds(0, 34, 76, 14);
    frmAddSequences.getContentPane().add(lblSequenceY);
    
    JButton btnNewButton = new JButton("Apply");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var x = Sequence.parse(textX.getText());
        var y = Sequence.parse(textY.getText());
        
        textMultArt.setText(getResult(x, y).toString());
      }
    });
    btnNewButton.setBounds(80, 53, 344, 23);
    frmAddSequences.getContentPane().add(btnNewButton);
    
    textMultArt = new JTextField();
    textMultArt.setColumns(10);
    textMultArt.setBounds(80, 183, 344, 17);
    frmAddSequences.getContentPane().add(textMultArt);
    
    JLabel lblYixi = new JLabel("x[i%]⋅y[i% or /]:");
    lblYixi.setHorizontalAlignment(SwingConstants.RIGHT);
    lblYixi.setBounds(80, 158, 82, 14);
    frmAddSequences.getContentPane().add(lblYixi);
    
    comboScale = new JComboBox<Scale>(new DefaultComboBoxModel<>(Scale.values()));
    comboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        scale = (Scale)comboScale.getSelectedItem();
      }
    });
    comboScale.setBounds(80, 87, 344, 23);
    frmAddSequences.getContentPane().add(comboScale);
    
    comboOperation = new JComboBox<Operation>(new DefaultComboBoxModel<>(Operation.values()));
    comboOperation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        operation = (Operation)comboOperation.getSelectedItem();
      }
    });
    comboOperation.setBounds(80, 121, 344, 23);
    frmAddSequences.getContentPane().add(comboOperation);
  }
}
