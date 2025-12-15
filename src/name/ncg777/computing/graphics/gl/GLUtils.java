package name.ncg777.computing.graphics.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.BufferUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Shared utilities for the GL full-screen shader demos.
 */
public final class GLUtils {
  private GLUtils() {}

  public static long createWindow(int width, int height, String title, boolean visible) {
    if (!glfwInit()) throw new IllegalStateException("GLFW init failed");
    if (!visible) glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    long win = glfwCreateWindow(width, height, visible ? title : "", NULL, NULL);
    if (win == NULL) throw new RuntimeException("Window creation failed");
    return win;
  }

  public static void makeContextCurrent(long window, boolean vsync) {
    glfwMakeContextCurrent(window);
    GL.createCapabilities();
    glfwSwapInterval(vsync ? 1 : 0);
  }

  public static void makeContextCurrent(long window) {
    glfwMakeContextCurrent(window);
    GL.createCapabilities();
  }

  public static int createFullscreenTriangleVAO() {
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);
    return vao;
  }

  public static String loadResource(String path) {
    try (InputStream is = GLUtils.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) throw new RuntimeException("Missing resource: " + path);
      return new String(is.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int compileShader(int type, String src) {
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

  public static int linkProgram(String vsSrc, String fsSrc) {
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

  public static int programFromResources(String vsResourcePath, String fsResourcePath) {
    String vs = loadResource(vsResourcePath);
    String fs = loadResource(fsResourcePath);
    return linkProgram(vs, fs);
  }

  public static Mat readFramebufferToMatRGBA(int width, int height) {
    ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
    // OpenCV convention is BGRA for CV_8UC4; reading BGRA avoids channel swaps
    // when frames are written via VideoWriter or converted with matToBufferedImage.
    glReadPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_BYTE, buf);

    Mat mat = new Mat(height, width, CvType.CV_8UC4);
    int stride = width * 4;
    byte[] row = new byte[stride];
    for (int y = 0; y < height; y++) {
      int srcY = height - 1 - y;
      buf.position(srcY * stride);
      buf.get(row);
      mat.put(y, 0, row);
    }
    return mat;
  }

  public static void destroyWindowAndTerminate(long window) {
    glfwDestroyWindow(window);
    glfwTerminate();
  }
}
