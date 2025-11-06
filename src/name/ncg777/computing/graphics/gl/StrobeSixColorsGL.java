package name.ncg777.computing.graphics.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import java.util.Enumeration;
import org.opencv.core.Mat;

/**
 * Fullscreen GL demo for the six-color stroboscopic shader.
 * The shader produces a dominant-color strobe whose cycle length equals the animation duration;
 * i.e., strobe frequency = 1 / durSeconds.
 */
public class StrobeSixColorsGL {

  /**
   * Run a windowed demo.
   * @param width  window width in pixels
   * @param height window height in pixels
  * @param fps    target frames per second
  * @param durSeconds animation duration in seconds for one complete cycle (strobe frequency = 1/durSeconds)
   * @param seed   seed forwarded to shader for subtle pattern variance
   * @param vsync  true to use swap interval vsync, false for manual pacing
   */
  public static void runWindow(int width, int height, int fps, double durSeconds,
                               long seed, boolean vsync) {
    long win = GLUtils.createWindow(width, height, "Six-Color Strobe (GL)", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/strobe_six_colors.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locSeed  = glGetUniformLocation(prog, "uSeed");
    int locCycle = glGetUniformLocation(prog, "uCycleIndex");

  // One full cycle spans the animation duration: framesPerCycle ≈ fps * durSeconds
  int framesPerCycle = Math.max(1, (int)Math.round(fps * durSeconds));

    // We still track duration for overall runtime pacing if needed
    // (phase always wraps at framesPerCycle regardless of durSeconds).
  // int totalFrames = Math.max(1, (int)Math.round(durSeconds * fps)); // not needed for phase control

    int frame = 0;
    long startNs = System.nanoTime();
    while (!glfwWindowShouldClose(win)) {
    // Phase repeats every framesPerCycle frames -> frequency = 1/durSeconds
    int k = frame % framesPerCycle;
      float phase = (float)k / (float)framesPerCycle; // [0,1)
    int cycleIndex = frame / framesPerCycle;

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locPhase, phase);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));
    glUniform1i(locCycle, cycleIndex);
      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      frame++;

      if (!vsync) {
        long targetNs = (long)(1_000_000_000.0 / Math.max(1, fps));
        long now = System.nanoTime();
        long elapsed = now - startNs;
        long sleepNs = targetNs - (elapsed % targetNs);
        if (sleepNs > 0) {
          try { Thread.sleep(sleepNs / 1_000_000, (int)(sleepNs % 1_000_000)); } catch (InterruptedException ignored) {}
        }
      }
    }

    glDeleteVertexArrays(vao);
    glDeleteProgram(prog);
    GLUtils.destroyWindowAndTerminate(win);
  }

  /**
   * Offscreen enumeration of stroboscopic frames as OpenCV Mats (RGBA 8UC4).
   * Creates a hidden context, renders each frame, reads back pixels, and cleans up at end.
  * Phase repeats every framesPerCycle frames. One cycle length = durSeconds (frequency = 1/durSeconds).
   *
   * @param width  image width in pixels
   * @param height image height in pixels
  * @param fps    frames per second
   * @param durSeconds total duration in seconds (enumeration length = round(fps*durSeconds))
   * @param seed   seed forwarded to shader for subtle pattern variance
   * @return Enumeration over Mats; last nextElement auto-destroys GL resources
   */
  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  long seed) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/strobe_six_colors.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locSeed  = glGetUniformLocation(prog, "uSeed");
    int locCycle = glGetUniformLocation(prog, "uCycleIndex");

  final int total = Math.max(1, (int)Math.round(durSeconds * fps));
  final int framesPerCycle = total; // exactly one cycle across the provided duration

    return new Enumeration<Mat>() {
      int k = 0;
      public boolean hasMoreElements() { return k < total; }
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
    int cycIdx = k % framesPerCycle;
        float phase = (float)cycIdx / (float)framesPerCycle; // [0,1)
    int cycleIndex = k / framesPerCycle;

        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(prog);
        glUniform2f(locRes, width, height);
        glUniform1f(locPhase, phase);
        glUniform1f(locSeed, (float)(seed % 1_000_003L));
    glUniform1i(locCycle, cycleIndex);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        Mat mat = GLUtils.readFramebufferToMatRGBA(width, height);

        k++;
        if (!hasMoreElements()) {
          glDeleteVertexArrays(vao);
          glDeleteProgram(prog);
          GLUtils.destroyWindowAndTerminate(win);
        }
        return mat;
      }
    };
  }

  public static void main(String[] args) {
  int width = 1280, height = 720, fps = 30;
  double dur = 1.0; // one full strobe/shape cycle lasts 'dur' seconds; frequency = 1/dur
    runWindow(width, height, fps, dur, System.nanoTime(), true);
  }
}
