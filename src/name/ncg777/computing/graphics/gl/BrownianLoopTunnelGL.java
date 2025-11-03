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

  // ---------- Quick demo ----------

  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double duration = 8.0;

    runWindow(width, height, fps, duration,
      1.0f,    // speed
      4.0f,    // twist
      1.9f,    // noise scale
      0.5f,    // noise amplitude
      1.0f,    // color cycle
      2.0f,    // fog density
      new float[]{0.2f, 0.5f, 0.9f}, // base color
      true     // vsync
    );
  }
}
