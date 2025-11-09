package name.ncg777.computing.graphics.gl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import java.util.Enumeration;
import org.opencv.core.Mat;

/**
 * Fullscreen GL demo for the aperiodic (Penrose-like) tiling with concentric shapes.
 * Uses resources/shaders/fullscreen.vert + resources/shaders/aperiodic_tiling_concentric.frag
 */
public class AperiodicTilingConcentricGL {

  /**
   * Run a windowed demo.
   * @param width      window width in pixels
   * @param height     window height in pixels
   * @param fps        target frames per second for pacing (when vsync=false)
   * @param vsync      if true, uses swap interval for pacing
   * @param scale      tiling density (shader uniform uScale)
   * @param rings      number of concentric rings per tile (shader uniform uRings)
   * @param thickness  ring thickness in tile UV (shader uniform uThickness)
   * @param seed       randomization seed forwarded to shader (uSeed)
   * @param shape      0=circle, 1=diamond, 2=square-ish (shader uniform uShape)
   */
  public static void runWindow(int width, int height, int fps, boolean vsync,
                               float scale, int rings, float thickness,
                               long seed, int shape) {
    long win = GLUtils.createWindow(width, height, "Aperiodic Tiling (GL)", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/aperiodic_tiling_concentric.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    // Uniform locations
    int locRes      = glGetUniformLocation(prog, "uResolution");
    int locTime     = glGetUniformLocation(prog, "uTime");
    int locScale    = glGetUniformLocation(prog, "uScale");
    int locRings    = glGetUniformLocation(prog, "uRings");
    int locThick    = glGetUniformLocation(prog, "uThickness");
    int locSeed     = glGetUniformLocation(prog, "uSeed");
    int locShape    = glGetUniformLocation(prog, "uShape");

    long start = System.nanoTime();
    while (!glfwWindowShouldClose(win)) {
      // Compute time in seconds since start
      long now = System.nanoTime();
      float timeSec = (now - start) * 1.0e-9f;

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locTime, timeSec);
      glUniform1f(locScale, scale);
      glUniform1i(locRings, rings);
      glUniform1f(locThick, thickness);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));
      glUniform1i(locShape, shape);

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      if (!vsync) {
        long targetNs = (long)(1_000_000_000.0 / Math.max(1, fps));
        long end = System.nanoTime();
        long dt = end - now;
        long sleepNs = targetNs - dt;
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
   * Offscreen enumeration of frames as OpenCV Mats (RGBA 8UC4), advancing time at 1/fps.
   * Cleans up GL resources after the final frame.
   */
  public static Enumeration<Mat> asMatEnumeration(int width, int height,
                                                  double fps, double seconds,
                                                  float scale, int rings, float thickness,
                                                  long seed, int shape) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/aperiodic_tiling_concentric.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes      = glGetUniformLocation(prog, "uResolution");
    int locTime     = glGetUniformLocation(prog, "uTime");
    int locScale    = glGetUniformLocation(prog, "uScale");
    int locRings    = glGetUniformLocation(prog, "uRings");
    int locThick    = glGetUniformLocation(prog, "uThickness");
    int locSeed     = glGetUniformLocation(prog, "uSeed");
    int locShape    = glGetUniformLocation(prog, "uShape");

    final int total = Math.max(1, (int)Math.round(seconds * fps));

    return new Enumeration<Mat>() {
      int k = 0;
      public boolean hasMoreElements() { return k < total; }
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();

        float timeSec = (float)(k / Math.max(1.0, fps));

        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(prog);
        glUniform2f(locRes, width, height);
        glUniform1f(locTime, timeSec);
        glUniform1f(locScale, scale);
        glUniform1i(locRings, rings);
        glUniform1f(locThick, thickness);
        glUniform1f(locSeed, (float)(seed % 1_000_003L));
        glUniform1i(locShape, shape);

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
    boolean vsync = true;
    float scale = 3.0f;
    int rings = 9;
    float thickness = 0.12f;
    long seed = System.nanoTime();
    int shape = 0; // 0=circle, 1=diamond, 2=square-ish

    runWindow(width, height, fps, vsync, scale, rings, thickness, seed, shape);
  }
}
