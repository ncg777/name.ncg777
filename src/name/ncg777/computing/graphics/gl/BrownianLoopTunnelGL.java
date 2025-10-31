package name.ncg777.computing.graphics.gl;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.*;
import java.nio.*;
import java.util.Enumeration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BrownianLoopTunnelGL {

  // ---------- Utility methods ----------

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
    try (InputStream is = BrownianLoopTunnelGL.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) throw new RuntimeException("Missing resource: " + path);
      return new String(is.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ---------- Windowed live renderer ----------

  public static void runWindow(int width, int height, int fps, double duration,
                               float speed, float twist, float noiseScale,
                               float noiseAmp, float colorCycle, float fogDensity,
                               float[] baseColor, boolean vsync) {

    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    long win = glfwCreateWindow(width, height, "Brownian Loop Tunnel", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Window creation failed");

    glfwMakeContextCurrent(win);
    GL.createCapabilities();
    glfwSwapInterval(vsync ? 1 : 0);

    String vs = loadResource("resources/shaders/fullscreen.vert");
    String fs = loadResource("resources/shaders/brownian_loop_tunnel.frag");
    int prog = linkProgram(vs, fs);

    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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
    glfwDestroyWindow(win);
    glfwTerminate();
  }

  // ---------- Offscreen enumeration renderer ----------

  public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, double duration,
                                                  float speed, float twist, float noiseScale,
                                                  float noiseAmp, float colorCycle, float fogDensity,
                                                  float[] baseColor) {
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
    String fs = loadResource("resources/shaders/brownian_loop_tunnel.frag");
    int prog = linkProgram(vs, fs);
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

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
          glDeleteProgram(prog);
          glfwDestroyWindow(win);
          glfwTerminate();
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
