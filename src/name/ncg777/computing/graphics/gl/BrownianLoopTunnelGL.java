package name.ncg777.computing.graphics.gl;

import org.opencv.core.Mat;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class BrownianLoopTunnelGL {

  // ---------- Windowed live renderer ----------

  public static void runWindow(int width, int height, int fps, double duration,
                               float speed, float twist, float noiseScale,
                               float noiseAmp, float colorCycle, float fogDensity,
                               float[] baseColor, boolean vsync) {

    long win = GLUtils.createWindow(width, height, "Brownian Loop Tunnel", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/brownian_loop_tunnel.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    // Uniform locations
    int locRes         = glGetUniformLocation(prog, "iResolution");
    int locTime        = glGetUniformLocation(prog, "iTime");
    int locLoop        = glGetUniformLocation(prog, "uLoopDuration");
    int locSpeed       = glGetUniformLocation(prog, "uSpeed");
    int locTwist       = glGetUniformLocation(prog, "uTwist");
    int locNoiseScale  = glGetUniformLocation(prog, "uNoiseScale");
    int locNoiseAmp    = glGetUniformLocation(prog, "uNoiseAmp");
    int locColorCycle  = glGetUniformLocation(prog, "uColorCycle");
    int locFogDensity  = glGetUniformLocation(prog, "uFogDensity");
    int locBaseColor   = glGetUniformLocation(prog, "uBaseColor");

    double startTime = System.nanoTime() / 1e9;
    double frameTime = 1.0 / Math.max(1, fps);

    while (!glfwWindowShouldClose(win)) {
      double elapsed = (System.nanoTime() / 1e9 - startTime);
      float t = (float)(elapsed % duration); // loop time

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locTime, t);
      glUniform1f(locLoop, (float)duration);
      glUniform1f(locSpeed, speed);
      glUniform1f(locTwist, twist);
      glUniform1f(locNoiseScale, noiseScale);
      glUniform1f(locNoiseAmp, noiseAmp);
      glUniform1f(locColorCycle, colorCycle);
      glUniform1f(locFogDensity, fogDensity);
      glUniform3f(locBaseColor, baseColor[0], baseColor[1], baseColor[2]);

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
                                                  float speed, float twist, float noiseScale,
                                                  float noiseAmp, float colorCycle, float fogDensity,
                                                  float[] baseColor) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/brownian_loop_tunnel.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes         = glGetUniformLocation(prog, "iResolution");
    int locTime        = glGetUniformLocation(prog, "iTime");
    int locLoop        = glGetUniformLocation(prog, "uLoopDuration");
    int locSpeed       = glGetUniformLocation(prog, "uSpeed");
    int locTwist       = glGetUniformLocation(prog, "uTwist");
    int locNoiseScale  = glGetUniformLocation(prog, "uNoiseScale");
    int locNoiseAmp    = glGetUniformLocation(prog, "uNoiseAmp");
    int locColorCycle  = glGetUniformLocation(prog, "uColorCycle");
    int locFogDensity  = glGetUniformLocation(prog, "uFogDensity");
    int locBaseColor   = glGetUniformLocation(prog, "uBaseColor");

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
        glUniform1f(locLoop, (float)duration);
        glUniform1f(locSpeed, speed);
        glUniform1f(locTwist, twist);
        glUniform1f(locNoiseScale, noiseScale);
        glUniform1f(locNoiseAmp, noiseAmp);
        glUniform1f(locColorCycle, colorCycle);
        glUniform1f(locFogDensity, fogDensity);
        glUniform3f(locBaseColor, baseColor[0], baseColor[1], baseColor[2]);
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
    double duration = 8.0;
    // speed, twist, noiseScale, noiseAmp, colorCycle, fogDensity, baseR, baseG, baseB
    final float[] params = { 1.0f, 4.0f, 1.9f, 0.5f, 1.0f, 2.0f, 0.2f, 0.5f, 0.9f };

    long win = GLUtils.createWindow(width, height, "Brownian Loop Tunnel - Interactive", true);
    GLUtils.makeContextCurrent(win, true);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 0.5f : 0.1f;
      switch (key) {
        case GLFW_KEY_1 -> params[0] += step;                             // speed +
        case GLFW_KEY_2 -> params[0] = Math.max(0.1f, params[0] - step);  // speed -
        case GLFW_KEY_3 -> params[1] += step;                             // twist +
        case GLFW_KEY_4 -> params[1] = Math.max(0f, params[1] - step);    // twist -
        case GLFW_KEY_5 -> params[2] += step;                             // noiseScale +
        case GLFW_KEY_6 -> params[2] = Math.max(0.1f, params[2] - step);  // noiseScale -
        case GLFW_KEY_7 -> params[3] += 0.05f;                            // noiseAmp +
        case GLFW_KEY_8 -> params[3] = Math.max(0f, params[3] - 0.05f);   // noiseAmp -
        case GLFW_KEY_Q -> params[4] += step;                             // colorCycle +
        case GLFW_KEY_A -> params[4] = Math.max(0f, params[4] - step);    // colorCycle -
        case GLFW_KEY_W -> params[5] += step;                             // fogDensity +
        case GLFW_KEY_S -> params[5] = Math.max(0.1f, params[5] - step);  // fogDensity -
        case GLFW_KEY_E -> params[6] = Math.min(1f, params[6] + 0.05f);   // baseR +
        case GLFW_KEY_D -> params[6] = Math.max(0f, params[6] - 0.05f);   // baseR -
        case GLFW_KEY_R -> params[7] = Math.min(1f, params[7] + 0.05f);   // baseG +
        case GLFW_KEY_F -> params[7] = Math.max(0f, params[7] - 0.05f);   // baseG -
        case GLFW_KEY_T -> params[8] = Math.min(1f, params[8] + 0.05f);   // baseB +
        case GLFW_KEY_G -> params[8] = Math.max(0f, params[8] - 0.05f);   // baseB -
      }
    });

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/brownian_loop_tunnel.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes         = glGetUniformLocation(prog, "iResolution");
    int locTime        = glGetUniformLocation(prog, "iTime");
    int locLoop        = glGetUniformLocation(prog, "uLoopDuration");
    int locSpeed       = glGetUniformLocation(prog, "uSpeed");
    int locTwist       = glGetUniformLocation(prog, "uTwist");
    int locNoiseScale  = glGetUniformLocation(prog, "uNoiseScale");
    int locNoiseAmp    = glGetUniformLocation(prog, "uNoiseAmp");
    int locColorCycle  = glGetUniformLocation(prog, "uColorCycle");
    int locFogDensity  = glGetUniformLocation(prog, "uFogDensity");
    int locBaseColor   = glGetUniformLocation(prog, "uBaseColor");

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
      glUniform1f(locLoop, (float)duration);
      glUniform1f(locSpeed, params[0]);
      glUniform1f(locTwist, params[1]);
      glUniform1f(locNoiseScale, params[2]);
      glUniform1f(locNoiseAmp, params[3]);
      glUniform1f(locColorCycle, params[4]);
      glUniform1f(locFogDensity, params[5]);
      glUniform3f(locBaseColor, params[6], params[7], params[8]);
      glDrawArrays(GL_TRIANGLES, 0, 3);
      glfwSwapBuffers(win);
      glfwPollEvents();
    }
    glDeleteVertexArrays(vao);
    glDeleteProgram(prog);
    GLUtils.destroyWindowAndTerminate(win);
  }
}
