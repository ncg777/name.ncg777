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

  // ---------- Demo ----------
  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    boolean vsync = true;

    int iterations = 6;
    float scale = 1.1f;
    float rotation = 0.0f;
    float glowIntensity = 0.9f;
    float[] colorPrimary   = new float[]{0.2f, 0.8f, 1.0f};
    float[] colorSecondary = new float[]{0.02f, 0.03f, 0.05f};

    runWindow(width, height, fps, vsync,
      iterations, scale, rotation, glowIntensity,
      colorPrimary, colorSecondary);
  }
}
