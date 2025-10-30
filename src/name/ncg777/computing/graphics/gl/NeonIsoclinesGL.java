package name.ncg777.computing.graphics.gl;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.*;
import java.util.Enumeration;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class NeonIsoclinesGL {
  private static int compileShader(int type, String src) {
    int s = glCreateShader(type);
    glShaderSource(s, src);
    glCompileShader(s);
    if (glGetShaderi(s, GL_COMPILE_STATUS) == GL_FALSE) {
      throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(s));
    }
    return s;
  }

  private static int linkProgram(String vs, String fs) {
    int v = compileShader(GL_VERTEX_SHADER, vs);
    int f = compileShader(GL_FRAGMENT_SHADER, fs);
    int p = glCreateProgram();
    glAttachShader(p, v);
    glAttachShader(p, f);
    glLinkProgram(p);
    if (glGetProgrami(p, GL_LINK_STATUS) == GL_FALSE) {
      throw new RuntimeException("Program link error: " + glGetProgramInfoLog(p));
    }
    glDeleteShader(v);
    glDeleteShader(f);
    return p;
  }

  private static String loadResource(String path) {
    try (InputStream is = NeonIsoclinesGL.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) throw new RuntimeException("Missing resource: " + path);
      return new String(is.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void runWindow(int width, int height, int fps, double durSeconds,
                               int components, int isoBands, float lineThickness,
                               float noiseAmount, long seed, boolean vsync) {
    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    long win = glfwCreateWindow(width, height, "Neon Isoclines (GL)", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Window creation failed");
    glfwMakeContextCurrent(win);
    GL.createCapabilities();
    glfwSwapInterval(vsync ? 1 : 0);

    String vs = loadResource("resources/shaders/fullscreen.vert");
    String fs = loadResource("resources/shaders/neon_isoclines.frag");
    int prog = linkProgram(vs, fs);

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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
    glfwDestroyWindow(win);
    glfwTerminate();
  }

  // Optional: bridge to Enumeration<Mat> (offscreen render + glReadPixels)
  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  int components, int isoBands, float lineThickness,
                                                  float noiseAmount, long seed) {
    // Create hidden window/context
    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    long win = glfwCreateWindow(width, height, "", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Hidden window creation failed");
    glfwMakeContextCurrent(win);
    GL.createCapabilities();

    String vs = loadResource("resources/shaders/fullscreen.vert");
    String fs = loadResource("resources/shaders/neon_isoclines.frag");
    int prog = linkProgram(vs, fs);
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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

        // Read back RGBA (top-left origin in OpenGL is bottom-left; flip later if needed)
        ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buf);

        // Construct Mat (OpenCV expects top-left origin; flip vertically)
        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        // Flip while copying
        int stride = width * 4;
        byte[] row = new byte[stride];
        for (int y = 0; y < height; y++) {
          int srcY = height - 1 - y;
          buf.position(srcY * stride);
          buf.get(row);
          mat.put(y, 0, row);
        }

        k++;
        if (!hasMoreElements()) {
          glDeleteVertexArrays(vao);
          glDeleteProgram(prog);
          glfwDestroyWindow(win);
          glfwTerminate();
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
      16, 4, 0.10f, 0.16f, System.nanoTime(), true);
  }
}
