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

  // Interactive demo main with keyboard controls
  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double dur = 8.0;
    final float[] params = { 64f, 8f, 0.25f, 2.5f }; // components, isoBands, lineThickness, noiseAmount
    final long seed = System.nanoTime();

    long win = GLUtils.createWindow(width, height, "Neon Isoclines (GL) - Interactive", true);
    GLUtils.makeContextCurrent(win, true);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 10f : 1f;
      switch (key) {
        case GLFW_KEY_Q -> params[0] = Math.max(1, params[0] + step);   // components +
        case GLFW_KEY_A -> params[0] = Math.max(1, params[0] - step);   // components -
        case GLFW_KEY_W -> params[1] = Math.max(1, params[1] + step);   // isoBands +
        case GLFW_KEY_S -> params[1] = Math.max(1, params[1] - step);   // isoBands -
        case GLFW_KEY_E -> params[2] = Math.min(1f, params[2] + 0.02f); // lineThickness +
        case GLFW_KEY_D -> params[2] = Math.max(0.01f, params[2] - 0.02f); // lineThickness -
        case GLFW_KEY_R -> params[3] = params[3] + 0.1f;                // noiseAmount +
        case GLFW_KEY_F -> params[3] = Math.max(0f, params[3] - 0.1f);  // noiseAmount -
      }
    });

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

    int totalFrames = Math.max(1, (int)Math.round(dur * fps));
    int frame = 0;
    while (!glfwWindowShouldClose(win)) {
      float phase = totalFrames > 1 ? (float)frame / (float)(totalFrames - 1) : 0f;
      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);
      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locPhase, phase);
      glUniform1i(locComp, (int)params[0]);
      glUniform1i(locBands, (int)params[1]);
      glUniform1f(locLT, params[2]);
      glUniform1f(locNoise, params[3]);
      glUniform1f(locSeed, (float)(seed % 1000003L));
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glfwSwapBuffers(win);
      glfwPollEvents();
      frame = (frame + 1) % totalFrames;
    }
    glDeleteVertexArrays(vao);
    glDeleteProgram(prog);
    GLUtils.destroyWindowAndTerminate(win);
  }
}
