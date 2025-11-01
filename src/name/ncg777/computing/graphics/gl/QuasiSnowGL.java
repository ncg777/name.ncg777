package name.ncg777.computing.graphics.gl;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.BufferUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class QuasiSnowGL {

  // ---------- GL utils ----------

  private static int compileShader(int type, String src) {
    int s = glCreateShader(type);
    glShaderSource(s, src);
    glCompileShader(s);
    if (glGetShaderi(s, GL_COMPILE_STATUS) == GL_FALSE) {
      String log = glGetShaderInfoLog(s);
      glDeleteShader(s);
      throw new RuntimeException("Shader compile error: " + log);
    }
    return s;
  }

  private static int linkProgram(String vsSrc, String fsSrc) {
    int v = compileShader(GL_VERTEX_SHADER, vsSrc);
    int f = compileShader(GL_FRAGMENT_SHADER, fsSrc);
    int p = glCreateProgram();
    glAttachShader(p, v);
    glAttachShader(p, f);
    glLinkProgram(p);
    if (glGetProgrami(p, GL_LINK_STATUS) == GL_FALSE) {
      String log = glGetProgramInfoLog(p);
      glDeleteShader(v);
      glDeleteShader(f);
      glDeleteProgram(p);
      throw new RuntimeException("Program link error: " + log);
    }
    glDeleteShader(v);
    glDeleteShader(f);
    return p;
  }

  private static String loadResource(String path) {
    try (InputStream is = QuasiSnowGL.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) throw new IOException("Resource not found: " + path);
      try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        return sb.toString();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load resource " + path + ": " + e.getMessage(), e);
    }
  }

  // ---------- Public runner (windowed) ----------

  public static void runWindow(int width, int height, int fps, boolean vsync,
                               int iterations, float scale, float rotation,
                               float glowIntensity, float[] colorPrimary, float[] colorSecondary) {

    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    long win = glfwCreateWindow(width, height, "QuasiSnowGL", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Window creation failed");
    glfwMakeContextCurrent(win);
    glfwSwapInterval(vsync ? 1 : 0);
    GL.createCapabilities();
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    // Program
    String vs = loadResource("resources/shaders/fullscreen.vert");
    String composeFs = loadResource("resources/shaders/quasi_snowflake.frag");
    int compProg = linkProgram(vs, composeFs);

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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
    glfwDestroyWindow(win);
    glfwTerminate();
  }

  // ---------- Offscreen enumeration ----------

  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double durSeconds,
                                                  int iterations, float scale, float rotation,
                                                  float glowIntensity, float[] colorPrimary, float[] colorSecondary) {

    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    long win = glfwCreateWindow(width, height, "", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Hidden window creation failed");
    glfwMakeContextCurrent(win);
    GL.createCapabilities();
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    String vs = loadResource("resources/shaders/fullscreen.vert");
    String composeFs = loadResource("resources/shaders/quasi_snowflake.frag");
    int compProg = linkProgram(vs, composeFs);

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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

        // Readback RGBA
        ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buf);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        int stride = width * 4;
        byte[] row = new byte[stride];
        for (int y = 0; y < height; y++) {
          int srcY = height - 1 - y;
          buf.position(srcY * stride);
          buf.get(row);
          mat.put(y, 0, row);
        }

        frame++;
        if (!hasMoreElements()) {
          glDeleteVertexArrays(vao);
          glDeleteProgram(compProg);
          glfwDestroyWindow(win);
          glfwTerminate();
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
