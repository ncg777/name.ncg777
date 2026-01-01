package name.ncg777.computing.graphics.gl;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main launcher GUI for GL animations.
 * Each animation has a button that launches it, plus a help button showing keyboard controls.
 */
public class GLAnimationLauncher {

  // Animation registry: name -> { launcher runnable, controls documentation }
  private static final Map<String, AnimationEntry> ANIMATIONS = new LinkedHashMap<>();

  static {
    ANIMATIONS.put("Neon Isoclines", new AnimationEntry(
      () -> NeonIsoclinesGL.main(new String[]{}),
      """
      Neon Isoclines Controls:
      ─────────────────────────
      Q / A  : Components +/-
      W / S  : Iso Bands +/-
      E / D  : Line Thickness +/-
      R / F  : Noise Amount +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Terrain Isoclines", new AnimationEntry(
      () -> TerrainIsoclinesGL.main(new String[]{}),
      """
      Terrain Isoclines Controls:
      ────────────────────────────
      1 / 2  : Scale +/-
      3 / 4  : Octaves +/-
      5 / 6  : Lacunarity +/-
      7 / 8  : Gain +/-
      Q / A  : Iso Bands +/-
      W / S  : Line Thickness +/-
      E / D  : Bubble Amplitude +/-
      R / F  : Bubble Frequency +/-
      T / G  : Bubble Detail +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Tanh Terrain Isoclines", new AnimationEntry(
      () -> TanhTerrainIsoclinesGL.main(new String[]{}),
      """
      Tanh Terrain Isoclines Controls:
      ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?ƒ"?
      1 / 2  : Scale +/-
      3 / 4  : Octaves +/-
      5 / 6  : Lacunarity +/-
      7 / 8  : Gain +/-
      Q / A  : Iso Bands +/-
      W / S  : Line Thickness +/-
      E / D  : Bubble Amplitude +/-
      R / F  : Bubble Frequency +/-
      T / G  : Bubble Detail +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Polar Rose", new AnimationEntry(
      () -> PolarRoseGL.main(new String[]{}),
      """
      Polar Rose Controls:
      ─────────────────────
      1 / 2  : Symmetry +/-
      3 / 4  : Subdivisions +/-
      5 / 6  : Scale +/-
      Q / A  : Sin Amplitude +/-
      W / S  : Base Frequency +/-
      E / D  : Mod Amplitude +/-
      R / F  : Mod Frequency ×/÷
      T / G  : Mod Divisor +/-
      Y / H  : Theta Scale +/-
      U / J  : Line Width +/-
      I / K  : Hue Cycles +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Brownian Loop Tunnel", new AnimationEntry(
      () -> BrownianLoopTunnelGL.main(new String[]{}),
      """
      Brownian Loop Tunnel Controls:
      ───────────────────────────────
      1 / 2  : Speed +/-
      3 / 4  : Twist +/-
      5 / 6  : Noise Scale +/-
      7 / 8  : Noise Amplitude +/-
      Q / A  : Color Cycle +/-
      W / S  : Fog Density +/-
      E / D  : Base Red +/-
      R / F  : Base Green +/-
      T / G  : Base Blue +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Koch Snowflake", new AnimationEntry(
      () -> KochSnowflakeGL.main(new String[]{}),
      """
      Koch Snowflake Controls:
      ─────────────────────────
      1 / 2  : Iterations +/-
      3 / 4  : Scale +/-
      5 / 6  : Rotation +/-
      7 / 8  : Glow Intensity +/-
      Q / A  : Primary Red +/-
      W / S  : Primary Green +/-
      E / D  : Primary Blue +/-
      R / F  : Secondary Red +/-
      T / G  : Secondary Green +/-
      Y / H  : Secondary Blue +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("Quasi Snowflake", new AnimationEntry(
      () -> QuasiSnowGL.main(new String[]{}),
      """
      Quasi Snowflake Controls:
      ──────────────────────────
      1 / 2  : Iterations +/-
      3 / 4  : Scale +/-
      5 / 6  : Rotation +/-
      7 / 8  : Glow Intensity +/-
      Q / A  : Primary Red +/-
      W / S  : Primary Green +/-
      E / D  : Primary Blue +/-
      R / F  : Secondary Red +/-
      T / G  : Secondary Green +/-
      Y / H  : Secondary Blue +/-
      
      Hold SHIFT for larger steps.
      ESC or close window to exit.
      """
    ));

    ANIMATIONS.put("FFT Disk", new AnimationEntry(
      () -> {
        String audioPath = chooseAudioFilePath();
        if (audioPath == null || audioPath.isBlank()) return;
        try {
          FFTDiskGL.runWindow(1280, 720, audioPath);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      },
      """
      FFT Disk Controls:
      ─────────────────
      ESC or close window to exit.
      """
    ));

    
  }

  private static String chooseAudioFilePath() {
    final String[] selected = new String[1];
    try {
      SwingUtilities.invokeAndWait(() -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select audio file for FFT Disk");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileFilter(new FileNameExtensionFilter(
          "WAV audio (*.wav)",
          "wav"));

        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          if (file != null) {
            selected[0] = file.getAbsolutePath();
          }
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (InvocationTargetException e) {
      return null;
    }

    return selected[0];
  }

  private record AnimationEntry(Runnable launcher, String controls) {}

  public static void main(String[] args) {
    SwingUtilities.invokeLater(GLAnimationLauncher::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("GL Animation Launcher");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 600);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Title
    JLabel title = new JLabel("GL Animations");
    title.setFont(new Font("SansSerif", Font.BOLD, 18));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(title);
    mainPanel.add(Box.createVerticalStrut(10));

    // Instructions
    JLabel instructions = new JLabel("<html><center>Select an animation to launch.<br>Click [?] to see keyboard controls.</center></html>");
    instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(instructions);
    mainPanel.add(Box.createVerticalStrut(15));

    // Animation buttons
    for (Map.Entry<String, AnimationEntry> entry : ANIMATIONS.entrySet()) {
      JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
      row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

      JButton launchBtn = new JButton(entry.getKey());
      launchBtn.setPreferredSize(new Dimension(200, 30));
      launchBtn.addActionListener((ActionEvent e) -> {
        // Launch in a new thread so GUI stays responsive
        new Thread(() -> {
          try {
            entry.getValue().launcher().run();
          } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> 
              JOptionPane.showMessageDialog(frame, 
                "Error launching animation:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE));
          }
        }, "GL-" + entry.getKey()).start();
      });

      JButton helpBtn = new JButton("?");
      helpBtn.setPreferredSize(new Dimension(45, 30));
      helpBtn.setToolTipText("Show keyboard controls");
      helpBtn.addActionListener((ActionEvent e) -> {
        showControlsDialog(frame, entry.getKey(), entry.getValue().controls());
      });

      row.add(launchBtn);
      row.add(helpBtn);
      mainPanel.add(row);
    }

    mainPanel.add(Box.createVerticalGlue());

    // Footer
    JLabel footer = new JLabel("<html><small>Tip: Hold SHIFT for larger parameter steps</small></html>");
    footer.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(footer);

    JScrollPane scrollPane = new JScrollPane(mainPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    frame.add(scrollPane);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static void showControlsDialog(JFrame parent, String animationName, String controls) {
    JTextArea textArea = new JTextArea(controls);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    textArea.setBackground(new Color(40, 40, 40));
    textArea.setForeground(new Color(220, 220, 220));
    textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(350, 300));

    JOptionPane.showMessageDialog(parent, scrollPane, 
      animationName + " - Controls", JOptionPane.PLAIN_MESSAGE);
  }
}
