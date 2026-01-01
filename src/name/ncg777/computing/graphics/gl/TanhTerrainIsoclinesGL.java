package name.ncg777.computing.graphics.gl;

import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

import org.opencv.core.Mat;

/**
 * Tanh Terrain Isoclines (GL): nonlinear, tanh-shaped contour terrain.
 */
public class TanhTerrainIsoclinesGL {

  public static void runWindow(int width, int height, int fps, double durSeconds,
                               float scale, int octaves, float lacunarity, float gain,
                               int isoBands, float lineThickness,
                               float bubbleAmp, float bubbleFreq, float bubbleDetail,
                               long seed, boolean vsync) {
    long win = GLUtils.createWindow(width, height, "Tanh Terrain Isoclines (GL)", true);
    GLUtils.makeContextCurrent(win, vsync);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/terrain_tanh_isoclines.frag");

    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locScale = glGetUniformLocation(prog, "uScale");
    int locOct   = glGetUniformLocation(prog, "uOctaves");
    int locLac   = glGetUniformLocation(prog, "uLacunarity");
    int locGain  = glGetUniformLocation(prog, "uGain");
    int locBands = glGetUniformLocation(prog, "uIsoBands");
    int locLT    = glGetUniformLocation(prog, "uLineThickness");
    int locSeed  = glGetUniformLocation(prog, "uSeed");
    int locBAmp  = glGetUniformLocation(prog, "uBubbleAmp");
    int locBFreq = glGetUniformLocation(prog, "uBubbleFreq");
    int locBDet  = glGetUniformLocation(prog, "uBubbleDetail");

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
      glUniform1f(locScale, scale);
      glUniform1i(locOct, Math.max(1, octaves));
      glUniform1f(locLac, lacunarity);
      glUniform1f(locGain, gain);
      glUniform1i(locBands, isoBands);
      glUniform1f(locLT, lineThickness);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));
      glUniform1f(locBAmp, bubbleAmp);
      glUniform1f(locBFreq, bubbleFreq);
      glUniform1f(locBDet, bubbleDetail);

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(win);
      glfwPollEvents();

      frame++;
      if (frame >= totalFrames) frame = 0; // loop seamlessly

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

  // Optional offscreen enumeration for OpenCV video pipelines
  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  float scale, int octaves, float lacunarity, float gain,
                                                  int isoBands, float lineThickness,
                                                  float bubbleAmp, float bubbleFreq, float bubbleDetail,
                                                  long seed) {
    long win = GLUtils.createWindow(width, height, "", false);
    GLUtils.makeContextCurrent(win);

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/terrain_tanh_isoclines.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locScale = glGetUniformLocation(prog, "uScale");
    int locOct   = glGetUniformLocation(prog, "uOctaves");
    int locLac   = glGetUniformLocation(prog, "uLacunarity");
    int locGain  = glGetUniformLocation(prog, "uGain");
    int locBands = glGetUniformLocation(prog, "uIsoBands");
    int locLT    = glGetUniformLocation(prog, "uLineThickness");
    int locSeed  = glGetUniformLocation(prog, "uSeed");
    int locBAmp  = glGetUniformLocation(prog, "uBubbleAmp");
    int locBFreq = glGetUniformLocation(prog, "uBubbleFreq");
    int locBDet  = glGetUniformLocation(prog, "uBubbleDetail");

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
        glUniform1f(locScale, scale);
        glUniform1i(locOct, Math.max(1, octaves));
        glUniform1f(locLac, lacunarity);
        glUniform1f(locGain, gain);
        glUniform1i(locBands, isoBands);
        glUniform1f(locLT, lineThickness);
        glUniform1f(locSeed, (float)(seed % 1_000_003L));
        glUniform1f(locBAmp, bubbleAmp);
        glUniform1f(locBFreq, bubbleFreq);
        glUniform1f(locBDet, bubbleDetail);
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
    // scale, octaves, lacunarity, gain, isoBands, lineThickness, bubbleAmp, bubbleFreq, bubbleDetail
    final float[] params = { 2.1f, 4f, 1.4f, 0.5f, 16f, 0.2f, 0.26f, 2.0f, 1.2f };
    final long seed = System.nanoTime();

    long win = GLUtils.createWindow(width, height, "Tanh Terrain Isoclines (GL) - Interactive", true);
    GLUtils.makeContextCurrent(win, true);

    glfwSetKeyCallback(win, (window, key, scancode, action, mods) -> {
      if (action != GLFW_PRESS && action != GLFW_REPEAT) return;
      float step = (mods & GLFW_MOD_SHIFT) != 0 ? 0.5f : 0.1f;
      switch (key) {
        case GLFW_KEY_1 -> params[0] = Math.max(0.1f, params[0] + step);  // scale +
        case GLFW_KEY_2 -> params[0] = Math.max(0.1f, params[0] - step);  // scale -
        case GLFW_KEY_3 -> params[1] = Math.min(12, params[1] + 1);       // octaves +
        case GLFW_KEY_4 -> params[1] = Math.max(1, params[1] - 1);        // octaves -
        case GLFW_KEY_5 -> params[2] += 0.05f;                            // lacunarity +
        case GLFW_KEY_6 -> params[2] = Math.max(1.01f, params[2] - 0.05f);// lacunarity -
        case GLFW_KEY_7 -> params[3] = Math.min(0.99f, params[3] + 0.02f);// gain +
        case GLFW_KEY_8 -> params[3] = Math.max(0.01f, params[3] - 0.02f);// gain -
        case GLFW_KEY_Q -> params[4] = Math.max(1, params[4] + 4);        // isoBands +
        case GLFW_KEY_A -> params[4] = Math.max(1, params[4] - 4);        // isoBands -
        case GLFW_KEY_W -> params[5] = Math.min(1f, params[5] + 0.02f);   // lineThickness +
        case GLFW_KEY_S -> params[5] = Math.max(0.02f, params[5] - 0.02f);// lineThickness -
        case GLFW_KEY_E -> params[6] += 0.02f;                            // bubbleAmp +
        case GLFW_KEY_D -> params[6] = Math.max(0f, params[6] - 0.02f);   // bubbleAmp -
        case GLFW_KEY_R -> params[7] += 0.25f;                            // bubbleFreq +
        case GLFW_KEY_F -> params[7] = Math.max(0f, params[7] - 0.25f);   // bubbleFreq -
        case GLFW_KEY_T -> params[8] += 0.1f;                             // bubbleDetail +
        case GLFW_KEY_G -> params[8] = Math.max(0.1f, params[8] - 0.1f);  // bubbleDetail -
      }
    });

    int prog = GLUtils.programFromResources(
      "resources/shaders/fullscreen.vert",
      "resources/shaders/terrain_tanh_isoclines.frag");
    int vao = GLUtils.createFullscreenTriangleVAO();

    int locRes   = glGetUniformLocation(prog, "uResolution");
    int locPhase = glGetUniformLocation(prog, "uPhase");
    int locScale = glGetUniformLocation(prog, "uScale");
    int locOct   = glGetUniformLocation(prog, "uOctaves");
    int locLac   = glGetUniformLocation(prog, "uLacunarity");
    int locGain  = glGetUniformLocation(prog, "uGain");
    int locBands = glGetUniformLocation(prog, "uIsoBands");
    int locLT    = glGetUniformLocation(prog, "uLineThickness");
    int locSeed  = glGetUniformLocation(prog, "uSeed");
    int locBAmp  = glGetUniformLocation(prog, "uBubbleAmp");
    int locBFreq = glGetUniformLocation(prog, "uBubbleFreq");
    int locBDet  = glGetUniformLocation(prog, "uBubbleDetail");

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
      glUniform1f(locScale, params[0]);
      glUniform1i(locOct, Math.max(1, (int)params[1]));
      glUniform1f(locLac, params[2]);
      glUniform1f(locGain, params[3]);
      glUniform1i(locBands, (int)params[4]);
      glUniform1f(locLT, params[5]);
      glUniform1f(locSeed, (float)(seed % 1_000_003L));
      glUniform1f(locBAmp, params[6]);
      glUniform1f(locBFreq, params[7]);
      glUniform1f(locBDet, params[8]);
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
