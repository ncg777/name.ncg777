package name.ncg777.computing.graphics.gl;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.opencv.core.Mat;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class QuasiSnowGL {

  // ---------- Public runner (windowed) ----------

  public static void runWindow(int width, int height, int fps, boolean vsync,
                               int iterations, float scale, float rotation,
                               float glowIntensity, float[] colorPrimary, float[] colorSecondary) {

    GLFWErrorCallback.createPrint(System.err).set();
    long win = GLUtils.createWindow(width, height, "QuasiSnowGL", true);
    GLUtils.makeContextCurrent(win, vsync);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    // Program
    int compProg = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/quasi_snowflake.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    // Uniforms
    glUseProgram(compProg);
    int cRes = glGetUniformLocation(compProg, "uResolution");
    int cTime = glGetUniformLocation(compProg, "uTime");
    int cIter = glGetUniformLocation(compProg, "uIterations");
    int cScale = glGetUniformLocation(compProg, "uScale");
    int cRot = glGetUniformLocation(compProg, "uRotation");
    int cGlow = glGetUniformLocation(compProg, "uGlowIntensity");
    int cColP = glGetUniformLocation(compProg, "uColorPrimary");
    int cColS = glGetUniformLocation(compProg, "uColorSecondary");
    glUniform2f(cRes, width, height);
    glUniform1i(cIter, iterations);
    glUniform1f(cScale, scale);
    glUniform1f(cRot, rotation);
    glUniform1f(cGlow, glowIntensity);
    glUniform3f(cColP, colorPrimary[0], colorPrimary[1], colorPrimary[2]);
    glUniform3f(cColS, colorSecondary[0], colorSecondary[1], colorSecondary[2]);

    double start = System.nanoTime() * 1e-9;
    double frameTime = 1.0 / Math.max(1, fps);

    while (!glfwWindowShouldClose(win)) {
      double now = System.nanoTime() * 1e-9;
      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(compProg);
      glUniform1f(cTime, (float)(now - start));
      glUniform1f(cRot, rotation + (float)(now - start) * 0.25f);
      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      double end = System.nanoTime() * 1e-9;
      double sleep = frameTime - (end - now);
      if (sleep > 0) {
        try { Thread.sleep((long)(sleep * 1000)); } catch (InterruptedException ignored) {}
      }
    }

    glDeleteVertexArrays(vao);
    glDeleteProgram(compProg);
    GLUtils.destroyWindowAndTerminate(win);
  }

  // ---------- Offscreen enumeration ----------

  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  int iterations, float scale, float rotation,
                                                  float glowIntensity, float[] colorPrimary, float[] colorSecondary) {

    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    int compProg = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/quasi_snowflake.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    glUseProgram(compProg);
    int cRes = glGetUniformLocation(compProg, "uResolution");
    int cTime = glGetUniformLocation(compProg, "uTime");
    int cIter = glGetUniformLocation(compProg, "uIterations");
    int cScale = glGetUniformLocation(compProg, "uScale");
    int cRot = glGetUniformLocation(compProg, "uRotation");
    int cGlow = glGetUniformLocation(compProg, "uGlowIntensity");
    int cColP = glGetUniformLocation(compProg, "uColorPrimary");
    int cColS = glGetUniformLocation(compProg, "uColorSecondary");
    glUniform2f(cRes, width, height);
    glUniform1i(cIter, iterations);
    glUniform1f(cScale, scale);
    glUniform1f(cRot, rotation);
    glUniform1f(cGlow, glowIntensity);
    glUniform3f(cColP, colorPrimary[0], colorPrimary[1], colorPrimary[2]);
    glUniform3f(cColS, colorSecondary[0], colorSecondary[1], colorSecondary[2]);

    final int total = Math.max(1, (int)Math.round(durSeconds * fps));
    final double start = System.nanoTime() * 1e-9;

    return new Enumeration<Mat>() {
      int frame = 0;

      @Override public boolean hasMoreElements() { return frame < total; }

      @Override public Mat nextElement() {
        if (!hasMoreElements()) throw new java.util.NoSuchElementException();

        double now = System.nanoTime() * 1e-9;
        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(compProg);
        glUniform1f(cTime, (float)(now - start));
        glUniform1f(cRot, rotation + (float)(now - start) * 0.25f);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        Mat mat = GLUtils.readFramebufferToMatRGBA(width, height);

        frame++;
        if (!hasMoreElements()) {
          glDeleteVertexArrays(vao);
          glDeleteProgram(compProg);
          GLUtils.destroyWindowAndTerminate(win);
        }
        return mat;
      }
    };
  }

  // ---------- Interactive Demo ----------
  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    // iterations, scale, rotation, glowIntensity, primR, primG, primB, secR, secG, secB
    final float[] params = { 6f, 1.1f, 0.0f, 0.9f, 0.2f, 0.8f, 1.0f, 0.02f, 0.03f, 0.05f };

    GLFWErrorCallback.createPrint(System.err).set();
    long win = GLUtils.createWindow(width, height, "QuasiSnowGL - Interactive", true);
    GLUtils.makeContextCurrent(win, true);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 0.5f : 0.1f;
      switch (key) {
        case GLFW_KEY_1 -> params[0] = Math.min(12, params[0] + 1);       // iterations +
        case GLFW_KEY_2 -> params[0] = Math.max(1, params[0] - 1);        // iterations -
        case GLFW_KEY_3 -> params[1] += step;                             // scale +
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
        case GLFW_KEY_R -> params[7] = Math.min(1f, params[7] + 0.02f);   // secR +
        case GLFW_KEY_F -> params[7] = Math.max(0f, params[7] - 0.02f);   // secR -
        case GLFW_KEY_T -> params[8] = Math.min(1f, params[8] + 0.02f);   // secG +
        case GLFW_KEY_G -> params[8] = Math.max(0f, params[8] - 0.02f);   // secG -
        case GLFW_KEY_Y -> params[9] = Math.min(1f, params[9] + 0.02f);   // secB +
        case GLFW_KEY_H -> params[9] = Math.max(0f, params[9] - 0.02f);   // secB -
      }
    });

    int compProg = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/quasi_snowflake.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    glUseProgram(compProg);
    int cRes = glGetUniformLocation(compProg, "uResolution");
    int cTime = glGetUniformLocation(compProg, "uTime");
    int cIter = glGetUniformLocation(compProg, "uIterations");
    int cScale = glGetUniformLocation(compProg, "uScale");
    int cRot = glGetUniformLocation(compProg, "uRotation");
    int cGlow = glGetUniformLocation(compProg, "uGlowIntensity");
    int cColP = glGetUniformLocation(compProg, "uColorPrimary");
    int cColS = glGetUniformLocation(compProg, "uColorSecondary");
    glUniform2f(cRes, width, height);

    double start = System.nanoTime() * 1e-9;
    while (!glfwWindowShouldClose(win)) {
      double now = System.nanoTime() * 1e-9;
      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);
      glUseProgram(compProg);
      glUniform1f(cTime, (float)(now - start));
      glUniform1i(cIter, (int)params[0]);
      glUniform1f(cScale, params[1]);
      glUniform1f(cRot, params[2] + (float)(now - start) * 0.25f);
      glUniform1f(cGlow, params[3]);
      glUniform3f(cColP, params[4], params[5], params[6]);
      glUniform3f(cColS, params[7], params[8], params[9]);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glfwSwapBuffers(win);
      glfwPollEvents();
    }
    glDeleteVertexArrays(vao);
    glDeleteProgram(compProg);
    GLUtils.destroyWindowAndTerminate(win);
  }
}
