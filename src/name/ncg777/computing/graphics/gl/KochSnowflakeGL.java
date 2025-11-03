package name.ncg777.computing.graphics.gl;

import org.opencv.core.Mat;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class KochSnowflakeGL {

  // ---------- Windowed live renderer ----------

  public static void runWindow(int width, int height, int fps, double duration,
                               int iterations, float scale, float rotation,
                               float glowIntensity, float[] colorPrimary,
                               float[] colorSecondary, boolean vsync) {

    long win = GLUtils.createWindow(width, height, "Koch Snowflake", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/koch_snowflake.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    // Uniform locations
    int locRes            = glGetUniformLocation(prog, "uResolution");
    int locTime           = glGetUniformLocation(prog, "uTime");
    int locIterations     = glGetUniformLocation(prog, "uIterations");
    int locScale          = glGetUniformLocation(prog, "uScale");
    int locRotation       = glGetUniformLocation(prog, "uRotation");
    int locGlowIntensity  = glGetUniformLocation(prog, "uGlowIntensity");
    int locColorPrimary   = glGetUniformLocation(prog, "uColorPrimary");
    int locColorSecondary = glGetUniformLocation(prog, "uColorSecondary");

    double startTime = System.nanoTime() / 1e9;
    double frameTime = 1.0 / Math.max(1, fps);

    while (!glfwWindowShouldClose(win)) {
      double elapsed = (System.nanoTime() / 1e9 - startTime);
      float t = (float)(elapsed % duration);

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locTime, t);
      glUniform1i(locIterations, iterations);
      glUniform1f(locScale, scale);
      glUniform1f(locRotation, rotation);
      glUniform1f(locGlowIntensity, glowIntensity);
      glUniform3f(locColorPrimary, colorPrimary[0], colorPrimary[1], colorPrimary[2]);
      glUniform3f(locColorSecondary, colorSecondary[0], colorSecondary[1], colorSecondary[2]);

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      if (!vsync) {
        long sleepNs = (long)(frameTime * 1e9);
        try { Thread.sleep(sleepNs / 1_000_000, (int)(sleepNs % 1_000_000)); }
        catch (InterruptedException ignored) {}
      }
    }

    glDeleteVertexArrays(vao);
    glDeleteProgram(prog);
    GLUtils.destroyWindowAndTerminate(win);
  }

  // ---------- Offscreen enumeration renderer ----------

  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double duration,
                                                  int iterations, float scale, float rotation,
                                                  float glowIntensity, float[] colorPrimary,
                                                  float[] colorSecondary) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/koch_snowflake.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes            = glGetUniformLocation(prog, "uResolution");
    int locTime           = glGetUniformLocation(prog, "uTime");
    int locIterations     = glGetUniformLocation(prog, "uIterations");
    int locScale          = glGetUniformLocation(prog, "uScale");
    int locRotation       = glGetUniformLocation(prog, "uRotation");
    int locGlowIntensity  = glGetUniformLocation(prog, "uGlowIntensity");
    int locColorPrimary   = glGetUniformLocation(prog, "uColorPrimary");
    int locColorSecondary = glGetUniformLocation(prog, "uColorSecondary");

    final int total = Math.max(1, (int)Math.round(duration * fps));
    final double frameTime = 1.0 / Math.max(1, fps);

    return new Enumeration<>() {
      int frame = 0;

      public boolean hasMoreElements() { return frame < total; }

      public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();
        float t = (float)((frame * frameTime) % duration);

        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(prog);
        glUniform2f(locRes, width, height);
        glUniform1f(locTime, t);
        glUniform1i(locIterations, iterations);
        glUniform1f(locScale, scale);
        glUniform1f(locRotation, rotation);
        glUniform1f(locGlowIntensity, glowIntensity);
        glUniform3f(locColorPrimary, colorPrimary[0], colorPrimary[1], colorPrimary[2]);
        glUniform3f(locColorSecondary, colorSecondary[0], colorSecondary[1], colorSecondary[2]);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        Mat mat = GLUtils.readFramebufferToMatRGBA(width, height);

        frame++;
        if (!hasMoreElements()) {
          glDeleteVertexArrays(vao);
          glDeleteProgram(prog);
          GLUtils.destroyWindowAndTerminate(win);
        }
        return mat;
      }
    };
  }

  // ---------- Quick demo ----------

  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double duration = 10.0;

    runWindow(width, height, fps, duration,
      4,                                     // iterations
      0.8f,                                  // scale
      0.2f,                                  // rotation speed
      2.0f,                                  // glow intensity
      new float[]{0.2f, 0.6f, 1.0f},         // primary color (blue)
      new float[]{1.0f, 0.3f, 0.6f},         // secondary color (pink)
      true                                   // vsync
    );
  }
}
