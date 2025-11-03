package name.ncg777.computing.graphics.gl;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import org.opencv.core.Mat;

public class NeonIsoclinesGL {

  public static void runWindow(int width, int height, int fps, double durSeconds,
                               int components, int isoBands, float lineThickness,
                               float noiseAmount, long seed, boolean vsync) {
    long win = GLUtils.createWindow(width, height, "Neon Isoclines (GL)", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/neon_isoclines.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locComp = glGetUniformLocation(prog, "uComponents");
    int locBands = glGetUniformLocation(prog, "uIsoBands");
    int locLT = glGetUniformLocation(prog, "uLineThickness");
    int locNoise = glGetUniformLocation(prog, "uNoiseAmount");
    int locSeed = glGetUniformLocation(prog, "uSeed");

    int totalFrames = Math.max(1, (int)Math.round(durSeconds * fps));
    int frame = 0;
    long startNs = System.nanoTime();
    while (!glfwWindowShouldClose(win)) {
      // Perfect loop phase: k/(upper-1)
      float phase = totalFrames > 1 ? (float)frame / (float)(totalFrames - 1) : 0f;

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locPhase, phase);
      glUniform1i(locComp, components);
      glUniform1i(locBands, isoBands);
      glUniform1f(locLT, lineThickness);
      glUniform1f(locNoise, noiseAmount);
      glUniform1f(locSeed, (float)(seed % 1000003L)); // keep in range

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      // advance frame at target fps
      frame++;
      if (frame >= totalFrames) frame = 0; // loop endlessly

      if (!vsync) {
        // simple busy-wait to approximate fps (optional)
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

  // Optional: bridge to Enumeration<Mat> (offscreen render + glReadPixels)
  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  int components, int isoBands, float lineThickness,
                                                  float noiseAmount, long seed) {
    // Create hidden window/context
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/neon_isoclines.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locComp = glGetUniformLocation(prog, "uComponents");
    int locBands = glGetUniformLocation(prog, "uIsoBands");
    int locLT = glGetUniformLocation(prog, "uLineThickness");
    int locNoise = glGetUniformLocation(prog, "uNoiseAmount");
    int locSeed = glGetUniformLocation(prog, "uSeed");

    final int total = Math.max(1, (int)Math.round(durSeconds * fps));
    return new Enumeration<Mat>() {
      int k = 0;
      public boolean hasMoreElements() { return k < total; }
      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
        float phase = total > 1 ? (float)k / (float)(total - 1) : 0f;

        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(prog);
        glUniform2f(locRes, width, height);
        glUniform1f(locPhase, phase);
        glUniform1i(locComp, components);
        glUniform1i(locBands, isoBands);
        glUniform1f(locLT, lineThickness);
        glUniform1f(locNoise, noiseAmount);
        glUniform1f(locSeed, (float)(seed % 1000003L));
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

  // Quick demo main
  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double dur = 8.0;
    runWindow(width, height, fps, dur,
      64, 8, 0.25f, 2.5f, System.nanoTime(), true);
  }
}
