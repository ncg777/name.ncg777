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

  // ---------- Interactive demo ----------

  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double duration = 10.0;
    // iterations, scale, rotation, glowIntensity, primR, primG, primB, secR, secG, secB
    final float[] params = { 4f, 0.8f, 0.2f, 2.0f, 0.2f, 0.6f, 1.0f, 1.0f, 0.3f, 0.6f };

    long win = GLUtils.createWindow(width, height, "Koch Snowflake - Interactive", true);
    GLUtils.makeContextCurrent(win, true);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 0.5f : 0.1f;
      switch (key) {
        case GLFW_KEY_1 -> params[0] = Math.min(8, params[0] + 1);        // iterations +
        case GLFW_KEY_2 -> params[0] = Math.max(1, params[0] - 1);        // iterations -
        case GLFW_KEY_3 -> params[1] = Math.min(3f, params[1] + step);    // scale +
        case GLFW_KEY_4 -> params[1] = Math.max(0.1f, params[1] - step);  // scale -
        case GLFW_KEY_5 -> params[2] += step;                             // rotation +
        case GLFW_KEY_6 -> params[2] -= step;                             // rotation -
        case GLFW_KEY_7 -> params[3] += step;                             // glowIntensity +
        case GLFW_KEY_8 -> params[3] = Math.max(0f, params[3] - step);    // glowIntensity -
        case GLFW_KEY_Q -> params[4] = Math.min(1f, params[4] + 0.05f);   // primR +
        case GLFW_KEY_A -> params[4] = Math.max(0f, params[4] - 0.05f);   // primR -
        case GLFW_KEY_W -> params[5] = Math.min(1f, params[5] + 0.05f);   // primG +
        case GLFW_KEY_S -> params[5] = Math.max(0f, params[5] - 0.05f);   // primG -
        case GLFW_KEY_E -> params[6] = Math.min(1f, params[6] + 0.05f);   // primB +
        case GLFW_KEY_D -> params[6] = Math.max(0f, params[6] - 0.05f);   // primB -
        case GLFW_KEY_R -> params[7] = Math.min(1f, params[7] + 0.05f);   // secR +
        case GLFW_KEY_F -> params[7] = Math.max(0f, params[7] - 0.05f);   // secR -
        case GLFW_KEY_T -> params[8] = Math.min(1f, params[8] + 0.05f);   // secG +
        case GLFW_KEY_G -> params[8] = Math.max(0f, params[8] - 0.05f);   // secG -
        case GLFW_KEY_Y -> params[9] = Math.min(1f, params[9] + 0.05f);   // secB +
        case GLFW_KEY_H -> params[9] = Math.max(0f, params[9] - 0.05f);   // secB -
      }
    });

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

    double startTime = System.nanoTime() / 1e9;
    while (!glfwWindowShouldClose(win)) {
      double elapsed = (System.nanoTime() / 1e9 - startTime);
      float t = (float)(elapsed % duration);
      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);
      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locTime, t);
      glUniform1i(locIterations, (int)params[0]);
      glUniform1f(locScale, params[1]);
      glUniform1f(locRotation, params[2]);
      glUniform1f(locGlowIntensity, params[3]);
      glUniform3f(locColorPrimary, params[4], params[5], params[6]);
      glUniform3f(locColorSecondary, params[7], params[8], params[9]);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glfwSwapBuffers(win);
      glfwPollEvents();
    }
    glDeleteVertexArrays(vao);
    glDeleteProgram(prog);
    GLUtils.destroyWindowAndTerminate(win);
  }
}
