package name.ncg777.computing.graphics.gl;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import org.opencv.core.Mat;

/**
 * PolarRoseGL: GPU port of the f20250708 animation.
 * Draws a polar rose with modulated transformation, rotational symmetry, and phase animation.
 * Exposes all parameters from the original transformation function for tweaking.
 */
public class PolarRoseGL {

  public static void runWindow(int width, int height, int fps, double durSeconds,
                               int symmetry, int subdivisions, float scale,
                               float sinAmp, float baseFreq, float modAmp,
                               float modFreq, float modDiv, float thetaScale,
                               float lineWidth, float hueCycles,
                               long seed, boolean vsync) {
    long win = GLUtils.createWindow(width, height, "Polar Rose (GL)", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/polar_rose.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locSym   = glGetUniformLocation(prog, "uSymmetry");
    int locSubd  = glGetUniformLocation(prog, "uSubdivisions");
    int locScale = glGetUniformLocation(prog, "uScale");
    int locSinAmp    = glGetUniformLocation(prog, "uSinAmp");
    int locBaseFreq  = glGetUniformLocation(prog, "uBaseFreq");
    int locModAmp    = glGetUniformLocation(prog, "uModAmp");
    int locModFreq   = glGetUniformLocation(prog, "uModFreq");
    int locModDiv    = glGetUniformLocation(prog, "uModDiv");
    int locThetaScale= glGetUniformLocation(prog, "uThetaScale");
    int locLineWidth = glGetUniformLocation(prog, "uLineWidth");
    int locHueCycles = glGetUniformLocation(prog, "uHueCycles");
    int locSeed  = glGetUniformLocation(prog, "uSeed");

    // Enable blending for antialiased lines over black
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    int totalFrames = Math.max(1, (int)Math.round(durSeconds * fps));
    int frame = 0;
    long startNs = System.nanoTime();
    while (!glfwWindowShouldClose(win)) {
      float phase = totalFrames > 1 ? (float)frame / (float)(totalFrames - 1) : 0f;

      glViewport(0, 0, width, height);
      glClearColor(0f, 0f, 0f, 1f);
      glClear(GL_COLOR_BUFFER_BIT);

      glUseProgram(prog);
      glUniform2f(locRes, width, height);
      glUniform1f(locPhase, phase);
      glUniform1i(locSym, symmetry);
      glUniform1i(locSubd, subdivisions);
      glUniform1f(locScale, scale);
      glUniform1f(locSinAmp, sinAmp);
      glUniform1f(locBaseFreq, baseFreq);
      glUniform1f(locModAmp, modAmp);
      glUniform1f(locModFreq, modFreq);
      glUniform1f(locModDiv, modDiv);
      glUniform1f(locThetaScale, thetaScale);
      glUniform1f(locLineWidth, lineWidth);
      glUniform1f(locHueCycles, hueCycles);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      frame++;
      if (frame >= totalFrames) frame = 0;

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

  // Offscreen enumeration for video export
  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  int symmetry, int subdivisions, float scale,
                                                  float sinAmp, float baseFreq, float modAmp,
                                                  float modFreq, float modDiv, float thetaScale,
                                                  float lineWidth, float hueCycles, long seed) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/polar_rose.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locSym   = glGetUniformLocation(prog, "uSymmetry");
    int locSubd  = glGetUniformLocation(prog, "uSubdivisions");
    int locScale = glGetUniformLocation(prog, "uScale");
    int locSinAmp    = glGetUniformLocation(prog, "uSinAmp");
    int locBaseFreq  = glGetUniformLocation(prog, "uBaseFreq");
    int locModAmp    = glGetUniformLocation(prog, "uModAmp");
    int locModFreq   = glGetUniformLocation(prog, "uModFreq");
    int locModDiv    = glGetUniformLocation(prog, "uModDiv");
    int locThetaScale= glGetUniformLocation(prog, "uThetaScale");
    int locLineWidth = glGetUniformLocation(prog, "uLineWidth");
    int locHueCycles = glGetUniformLocation(prog, "uHueCycles");
    int locSeed  = glGetUniformLocation(prog, "uSeed");

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
        glUniform1i(locSym, symmetry);
        glUniform1i(locSubd, subdivisions);
        glUniform1f(locScale, scale);
        glUniform1f(locSinAmp, sinAmp);
        glUniform1f(locBaseFreq, baseFreq);
        glUniform1f(locModAmp, modAmp);
        glUniform1f(locModFreq, modFreq);
        glUniform1f(locModDiv, modDiv);
        glUniform1f(locThetaScale, thetaScale);
        glUniform1f(locLineWidth, lineWidth);
        glUniform1f(locHueCycles, hueCycles);
        glUniform1f(locSeed, (float)(seed % 1_000_003L));
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
      7,      // symmetry (n)
      64,    // subdivisions
      0.9f,    // scale
      0.5f,   // sinAmp
      3.0f,    // baseFreq
      0.5f,    // modAmp
      512.0f, // modFreq
      5.0f,    // modDiv
      0.95f,   // thetaScale
      1.0f,    // lineWidth (pixels)
      2.0f,    // hueCycles
      System.nanoTime(), true);
  }
}
