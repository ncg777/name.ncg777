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

  // Interactive demo main with keyboard controls
  public static void main(String[] args) {
    int width = 1280, height = 720, fps = 60;
    double dur = 8.0;
    // symmetry, subdivisions, scale, sinAmp, baseFreq, modAmp, modFreq, modDiv, thetaScale, lineWidth, hueCycles
    final float[] params = { 7f, 64f, 0.9f, 0.5f, 3.0f, 0.5f, 512.0f, 5.0f, 0.95f, 1.0f, 2.0f };
    final long seed = System.nanoTime();

    long win = GLUtils.createWindow(width, height, "Polar Rose (GL) - Interactive", true);
    GLUtils.makeContextCurrent(win, true);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 10f : 1f;
      switch (key) {
        case GLFW_KEY_1 -> params[0] = Math.max(1, params[0] + 1);        // symmetry +
        case GLFW_KEY_2 -> params[0] = Math.max(1, params[0] - 1);        // symmetry -
        case GLFW_KEY_3 -> params[1] = Math.max(8, params[1] + step * 8); // subdivisions +
        case GLFW_KEY_4 -> params[1] = Math.max(8, params[1] - step * 8); // subdivisions -
        case GLFW_KEY_5 -> params[2] = Math.min(2f, params[2] + 0.05f);   // scale +
        case GLFW_KEY_6 -> params[2] = Math.max(0.1f, params[2] - 0.05f); // scale -
        case GLFW_KEY_Q -> params[3] += 0.05f;                            // sinAmp +
        case GLFW_KEY_A -> params[3] = Math.max(0f, params[3] - 0.05f);   // sinAmp -
        case GLFW_KEY_W -> params[4] += 0.5f;                             // baseFreq +
        case GLFW_KEY_S -> params[4] = Math.max(0.5f, params[4] - 0.5f);  // baseFreq -
        case GLFW_KEY_E -> params[5] += 0.1f;                             // modAmp +
        case GLFW_KEY_D -> params[5] = Math.max(0f, params[5] - 0.1f);    // modAmp -
        case GLFW_KEY_R -> params[6] *= 1.5f;                             // modFreq *
        case GLFW_KEY_F -> params[6] = Math.max(1f, params[6] / 1.5f);    // modFreq /
        case GLFW_KEY_T -> params[7] += 0.5f;                             // modDiv +
        case GLFW_KEY_G -> params[7] = Math.max(0.5f, params[7] - 0.5f);  // modDiv -
        case GLFW_KEY_Y -> params[8] = Math.min(2f, params[8] + 0.05f);   // thetaScale +
        case GLFW_KEY_H -> params[8] = Math.max(0.1f, params[8] - 0.05f); // thetaScale -
        case GLFW_KEY_U -> params[9] += 0.25f;                            // lineWidth +
        case GLFW_KEY_J -> params[9] = Math.max(0.25f, params[9] - 0.25f);// lineWidth -
        case GLFW_KEY_I -> params[10] += 0.5f;                            // hueCycles +
        case GLFW_KEY_K -> params[10] = Math.max(0.5f, params[10] - 0.5f);// hueCycles -
      }
    });

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
      glUniform1i(locSym, (int)params[0]);
      glUniform1i(locSubd, (int)params[1]);
      glUniform1f(locScale, params[2]);
      glUniform1f(locSinAmp, params[3]);
      glUniform1f(locBaseFreq, params[4]);
      glUniform1f(locModAmp, params[5]);
      glUniform1f(locModFreq, params[6]);
      glUniform1f(locModDiv, params[7]);
      glUniform1f(locThetaScale, params[8]);
      glUniform1f(locLineWidth, params[9]);
      glUniform1f(locHueCycles, params[10]);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));
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
