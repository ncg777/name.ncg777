package name.ncg777.computing.graphics.gl;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;
import org.lwjgl.BufferUtils;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.opencv.core.Mat;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Enumeration;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDeleteTextures;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glPixelStorei;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL11C.glTexSubImage2D;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glUniform1f;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniform2f;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30C.GL_R32F;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL33C.*;

public class FFTDiskGL {


    public static final class BPMDetection {

      private BPMDetection() {}

      public static float detectBPM(String audioFilePath)
              throws IOException, UnsupportedAudioFileException {

                    File f = new File(audioFilePath);
                    if (!f.exists() || !f.isFile()) {
                        return 0f;
                    }

                    // Bigger buffers are typically more stable for onset/tempo.
                    int bufferSize = 2048;
                    int overlap = 1024;

                    AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(f, bufferSize, overlap);
                    float sr = dispatcher.getFormat() != null ? dispatcher.getFormat().getSampleRate() : 44100f;

                    // Primary onset detector (spectral)
                    List<Double> onsetTimes = new ArrayList<>();
                    ComplexOnsetDetector detector = new ComplexOnsetDetector(bufferSize, (int)Math.round(sr));
                    detector.setHandler((time, salience) -> onsetTimes.add(time));
                    dispatcher.addAudioProcessor(detector);

                    // Fallback onset detector (simple energy-based peak picking)
                    List<Double> energyOnsetTimes = new ArrayList<>();
                    dispatcher.addAudioProcessor(new AudioProcessor() {
                        private final int windowFrames = 43; // ~1s window depending on hop size
                        private final Deque<Double> hist = new ArrayDeque<>(windowFrames);
                        private double prevEnergy = 0.0;
                        private double lastOnsetT = -1.0;

                        @Override
                        public boolean process(AudioEvent audioEvent) {
                            float[] buf = audioEvent.getFloatBuffer();
                            if (buf == null || buf.length == 0) return true;

                            double sumSq = 0.0;
                            for (float v : buf) {
                                sumSq += (double)v * (double)v;
                            }
                            double rms = Math.sqrt(sumSq / (double)buf.length);
                            // log-compress improves robustness across mixes
                            double e = Math.log1p(rms * 1000.0);

                            if (hist.size() == windowFrames) {
                                hist.removeFirst();
                            }
                            hist.addLast(e);

                            double mean = 0.0;
                            for (double x : hist) mean += x;
                            mean /= (double)hist.size();

                            double var = 0.0;
                            for (double x : hist) {
                                double d = x - mean;
                                var += d * d;
                            }
                            var /= (double)hist.size();
                            double std = Math.sqrt(var);

                            double threshold = mean + 1.5 * std;
                            double t = audioEvent.getTimeStamp();
                            boolean isPeak = e > threshold && prevEnergy <= threshold;
                            boolean spaced = lastOnsetT < 0 || (t - lastOnsetT) >= 0.10; // avoid double triggers

                            if (isPeak && spaced) {
                                energyOnsetTimes.add(t);
                                lastOnsetT = t;
                            }
                            prevEnergy = e;
                            return true;
                        }

                        @Override
                        public void processingFinished() {
                            // no-op
                        }
                    });

                    dispatcher.run();

                    List<Double> times = onsetTimes.size() >= 2 ? onsetTimes : energyOnsetTimes;
                    if (times.size() < 2) {
                        return 0f;
                    }

                    // Compute inter-onset intervals (IOI)
                    List<Double> intervals = new ArrayList<>();
                    for (int i = 1; i < times.size(); i++) {
                        double dt = times.get(i) - times.get(i - 1);
                        if (dt >= 0.18 && dt <= 2.0) { // ignore too-fast/too-slow gaps
                            intervals.add(dt);
                        }
                    }
                    if (intervals.size() < 2) {
                        return 0f;
                    }

                    // Median interval is robust to outliers
                    intervals.sort(Double::compare);
                    double medianInterval = intervals.get(intervals.size() / 2);
                    if (medianInterval <= 0.0) {
                        return 0f;
                    }

                    double bpm = 60.0 / medianInterval;
                    // Fold into a musically plausible range (avoid half/double tempo)
                    while (bpm > 200.0) bpm /= 2.0;
                    while (bpm < 60.0) bpm *= 2.0;

                    return (float)bpm;
      }
  }


  
        private long window;
        private float rotationAngle = 0f; // radians for shader
        private int numBars = 64;
        private int fps = 60;

        // Match Animations.fftDiskAnimation: logBands x timeCols history matrix.
        private int logBands = 64;
        private int timeCols = 960;
        private int writeCol = 0;
        private int matrixTex = 0;
        private FloatBuffer columnBuffer;
        
        // Pre-computed FFT frames (like Animations.fftDiskAnimation)
        private List<double[]> fftFrames = new ArrayList<>();
        private double[][] matrixData;
        private double fftFramesPerVideoFrame = 1.0;

        // Audio playback and timing
        private double audioDurationSeconds = 0.0;
        private double eightBarsSeconds = 8.0; // duration of 8 bars in seconds (computed from BPM)
        private int totalFrames = 1;
        private Clip audioClip = null;

        private int windowWidth = 1280;
        private int windowHeight = 768;

        private int prog;
        private int vao;
        private int locRes;
        private int locRotation;
        private int locTimeCols;
        private int locLogBands;
        private int locWriteCol;
        private int locMatrixTex;
        private FloatBuffer fftBuffer;

        private String audioFilePath = "example.wav";

        // Offscreen enumeration for OpenCV pipelines (no audio playback; frames follow audio progression)
        public static Enumeration<Mat> asMatEnumeration(int width, int height, double fps, String filename)
                throws IOException, UnsupportedAudioFileException {
            if (filename == null || filename.isBlank()) {
                throw new IllegalArgumentException("filename is required");
            }
            if (fps <= 0) {
                throw new IllegalArgumentException("fps must be > 0");
            }

            // === Load audio into mono samples ===
            File audioFile = new File(filename);
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = stream.getFormat();
            int sampleRate = (int) format.getSampleRate();
            int channels = format.getChannels();
            int bytesPerSample = format.getSampleSizeInBits() / 8;
            boolean isBigEndian = format.isBigEndian();

            byte[] audioBytes = stream.readAllBytes();
            stream.close();
            java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(audioBytes);
            bb.order(isBigEndian ? java.nio.ByteOrder.BIG_ENDIAN : java.nio.ByteOrder.LITTLE_ENDIAN);

            int totalSamples = audioBytes.length / Math.max(1, (bytesPerSample * Math.max(1, channels)));
            double[] samples = new double[totalSamples];
            for (int i = 0; i < totalSamples; i++) {
                if (bytesPerSample == 2) {
                    short sample = bb.getShort(i * bytesPerSample * channels);
                    samples[i] = sample / 32768.0;
                } else {
                    samples[i] = 0;
                }
            }
            final double audioDurationSeconds = (double) totalSamples / Math.max(1.0, (double) sampleRate);

            // === Precompute FFT frames ===
            int windowSize = 2048;
            int hopSize = windowSize / 2;
            int fftBins = windowSize / 2;
            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

            double[] hann = new double[windowSize];
            for (int i = 0; i < windowSize; i++) {
                hann[i] = 0.5 * (1.0 - Math.cos((2.0 * Math.PI * i) / (windowSize - 1)));
            }

            List<double[]> fftFrames = new ArrayList<>();
            for (int i = 0; i + windowSize <= samples.length; i += hopSize) {
                double[] window = java.util.Arrays.copyOfRange(samples, i, i + windowSize);
                for (int j = 0; j < windowSize; j++) {
                    window[j] *= hann[j];
                }
                Complex[] spectrum = fft.transform(window, TransformType.FORWARD);
                double[] magnitudes = new double[fftBins];
                for (int j = 0; j < fftBins; j++) {
                    magnitudes[j] = spectrum[j].abs() / windowSize;
                }
                fftFrames.add(magnitudes);
            }
            if (fftFrames.isEmpty()) {
                // Avoid division by zero downstream; still render black frames.
                fftFrames.add(new double[fftBins]);
            }

            float bpm = BPMDetection.detectBPM(filename);
            if (bpm <= 0) bpm = 120f;
            double secondsPerBeat = 60.0 / bpm;
            double eightBarsSeconds = 32.0 * secondsPerBeat;
            if (eightBarsSeconds < 8.0) eightBarsSeconds = 8.0;
            if (eightBarsSeconds > 32.0) eightBarsSeconds = 32.0;

            final int logBands = 128;
            final int timeCols = (int) Math.ceil(eightBarsSeconds * fps);
            final double[][] matrixData = new double[logBands][Math.max(1, timeCols)];

            // === Offscreen GL setup ===
            long win = GLUtils.createWindow(width, height, "", false);
            GLUtils.makeContextCurrent(win);

            int prog = GLUtils.programFromResources(
                    "resources/shaders/fullscreen.vert",
                    "resources/shaders/fft_disk.frag");
            int vao = GLUtils.createFullscreenTriangleVAO();

            int locRes = glGetUniformLocation(prog, "uResolution");
            int locRotation = glGetUniformLocation(prog, "uRotation");
            int locTimeCols = glGetUniformLocation(prog, "uTimeCols");
            int locLogBands = glGetUniformLocation(prog, "uLogBands");
            int locWriteCol = glGetUniformLocation(prog, "uWriteCol");
            int locMatrixTex = glGetUniformLocation(prog, "uMatrixTex");

            int matrixTex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, matrixTex);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            FloatBuffer initData = BufferUtils.createFloatBuffer(Math.max(1, timeCols) * logBands);
            for (int i = 0; i < Math.max(1, timeCols) * logBands; i++) {
                initData.put(0f);
            }
            initData.flip();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, Math.max(1, timeCols), logBands, 0, GL_RED, GL_FLOAT, initData);

            final int totalFrames = Math.max(1, (int) Math.round(audioDurationSeconds * fps));
            final FloatBuffer texData = BufferUtils.createFloatBuffer(Math.max(1, timeCols) * logBands);

            return new Enumeration<Mat>() {
                int k = 0;

                @Override
                public boolean hasMoreElements() {
                    return k < totalFrames;
                }

                @Override
                public Mat nextElement() {
                    if (!hasMoreElements()) throw new java.util.NoSuchElementException();

                    double progress = totalFrames > 1 ? (double) k / (double) (totalFrames - 1) : 0.0;
                    progress = Math.max(0.0, Math.min(progress, 1.0));

                    int fftIndex = (int) (progress * (fftFrames.size() - 1));
                    fftIndex = Math.max(0, Math.min(fftIndex, fftFrames.size() - 1));
                    double[] fftFrame = fftFrames.get(fftIndex);

                    // Shift matrix left, add new column at right
                    for (int band = 0; band < logBands; band++) {
                        for (int col = 0; col < Math.max(1, timeCols) - 1; col++) {
                            matrixData[band][col] = matrixData[band][col + 1];
                        }

                        int low = (int) Math.pow(fftBins, (double) band / logBands);
                        int high = (int) Math.pow(fftBins, (double) (band + 1) / logBands);
                        low = Math.max(0, Math.min(low, fftFrame.length - 1));
                        high = Math.max(low + 1, Math.min(high, fftFrame.length));

                        double avg = 0.0;
                        for (int j = low; j < high; j++) avg += fftFrame[j];
                        avg /= (high - low);
                        matrixData[band][Math.max(1, timeCols) - 1] = avg;
                    }

                    glViewport(0, 0, width, height);
                    glClearColor(0f, 0f, 0f, 1f);
                    glClear(GL_COLOR_BUFFER_BIT);

                    glUseProgram(prog);
                    glUniform2f(locRes, width, height);
                    glUniform1f(locRotation, 0.0f);
                    glUniform1i(locTimeCols, Math.max(1, timeCols));
                    glUniform1i(locLogBands, logBands);
                    glUniform1i(locWriteCol, Math.max(1, timeCols) - 1);

                    texData.clear();
                    for (int band = 0; band < logBands; band++) {
                        for (int col = 0; col < Math.max(1, timeCols); col++) {
                            texData.put((float) matrixData[band][col]);
                        }
                    }
                    texData.flip();

                    glActiveTexture(GL_TEXTURE0);
                    glBindTexture(GL_TEXTURE_2D, matrixTex);
                    glUniform1i(locMatrixTex, 0);
                    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, Math.max(1, timeCols), logBands, GL_RED, GL_FLOAT, texData);

                    glBindVertexArray(vao);
                    glDrawArrays(GL_TRIANGLES, 0, 3);

                    Mat mat = GLUtils.readFramebufferToMatRGBA(width, height);

                    k++;
                    if (!hasMoreElements()) {
                        glDeleteTextures(matrixTex);
                        glDeleteVertexArrays(vao);
                        glDeleteProgram(prog);
                        GLUtils.destroyWindowAndTerminate(win);
                    }
                    return mat;
                }
            };
        }

        /**
         * Animation-style entrypoint consistent with other GL demos.
         */
        public static void runWindow(int width, int height, String filename)
                throws IOException, UnsupportedAudioFileException {
            FFTDiskGL app = new FFTDiskGL();
            if (filename != null && !filename.isBlank()) {
                app.audioFilePath = filename;
            }

            // 1. Load and pre-process audio (exactly like Animations.fftDiskAnimation)
            File audioFile = new File(app.audioFilePath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = stream.getFormat();
            int sampleRate = (int) format.getSampleRate();
            int channels = format.getChannels();
            int bytesPerSample = format.getSampleSizeInBits() / 8;
            boolean isBigEndian = format.isBigEndian();
            
            byte[] audioBytes = stream.readAllBytes();
            stream.close();
            java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(audioBytes);
            bb.order(isBigEndian ? java.nio.ByteOrder.BIG_ENDIAN : java.nio.ByteOrder.LITTLE_ENDIAN);
            
            int totalSamples = audioBytes.length / (bytesPerSample * channels);
            double[] samples = new double[totalSamples];
            for (int i = 0; i < totalSamples; i++) {
                if (bytesPerSample == 2) {
                    short sample = bb.getShort(i * bytesPerSample * channels);
                    samples[i] = sample / 32768.0;
                } else {
                    samples[i] = 0;
                }
            }
            
            app.audioDurationSeconds = (double) totalSamples / (double) sampleRate;
            System.out.println("Audio duration: " + app.audioDurationSeconds + " seconds");
            
            // 2. Compute FFT frames (like Animations.fftDiskAnimation, but higher resolution)
            // Larger FFT window gives finer frequency detail (helps pads/chords look cleaner).
            int windowSize = 2048;
            int hopSize = windowSize / 2;
            int fftBins = windowSize / 2;
            FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

            // Hann window reduces spectral leakage (cleaner “bands” for tonal/ambient material)
            double[] hann = new double[windowSize];
            for (int i = 0; i < windowSize; i++) {
                hann[i] = 0.5 * (1.0 - Math.cos((2.0 * Math.PI * i) / (windowSize - 1)));
            }
            
            for (int i = 0; i + windowSize <= samples.length; i += hopSize) {
                double[] window = java.util.Arrays.copyOfRange(samples, i, i + windowSize);
                for (int j = 0; j < windowSize; j++) {
                    window[j] *= hann[j];
                }
                Complex[] spectrum = fft.transform(window, TransformType.FORWARD);
                double[] magnitudes = new double[fftBins];
                for (int j = 0; j < fftBins; j++) {
                    // Normalize so typical values land near [0,1] like the original palette expects.
                    magnitudes[j] = spectrum[j].abs() / windowSize;
                }
                app.fftFrames.add(magnitudes);
            }
            System.out.println("Computed " + app.fftFrames.size() + " FFT frames");
            
            // 3. Detect BPM and setup matrix dimensions
            float bpm = BPMDetection.detectBPM(app.audioFilePath);
            if (bpm <= 0) bpm = 120f; // fallback
            System.out.println("Detected BPM: " + bpm);
            
            // Calculate 8 bars duration: 8 bars * 4 beats/bar = 32 beats
            double secondsPerBeat = 60.0 / bpm;
            app.eightBarsSeconds = 32.0 * secondsPerBeat;
            
            // Sanity check - 8 bars should be at least 8 seconds (240 BPM max)
            // and at most 32 seconds (60 BPM min)
            if (app.eightBarsSeconds < 8.0) {
                System.out.println("WARNING: eightBarsSeconds=" + app.eightBarsSeconds + " is too short, clamping to 8.0");
                app.eightBarsSeconds = 8.0;
            }
            if (app.eightBarsSeconds > 32.0) {
                System.out.println("WARNING: eightBarsSeconds=" + app.eightBarsSeconds + " is too long, clamping to 32.0");
                app.eightBarsSeconds = 32.0;
            }
            System.out.println("8 bars = " + app.eightBarsSeconds + " seconds (BPM=" + bpm + ")");
            
            // timeCols = number of VIDEO frames in 8 bars (like original)
            // This means the disk shows exactly 8 bars of history.
            app.timeCols = (int) Math.ceil(app.eightBarsSeconds * app.fps);
            // Increase radial resolution (frequency bands).
            app.logBands = 128;
            app.matrixData = new double[app.logBands][app.timeCols];
            
            System.out.println("Matrix: " + app.timeCols + " cols x " + app.logBands + " bands");
            
            // Total video frames and FFT timing
            app.totalFrames = Math.max(1, (int) Math.ceil(app.audioDurationSeconds * app.fps));
            double fftFramesPerSecond = (double) sampleRate / hopSize;
            app.fftFramesPerVideoFrame = fftFramesPerSecond / app.fps;
            
            // 4. Setup LWJGL
            app.initGL(width, height);

            // 5. Start audio playback
            app.startAudioPlayback(app.audioFilePath);

            // 6. Rendering loop
            app.loop();

            app.cleanup();
        }

        public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
            int width = 1280;
            int height = 720;
            String filename = "example.wav";

            if (args != null) {
                if (args.length >= 1) {
                    try {
                        width = Integer.parseInt(args[0]);
                    } catch (NumberFormatException ignored) {}
                }
                if (args.length >= 2) {
                    try {
                        height = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                if (args.length >= 3) {
                    filename = args[2];
                }
            }

            runWindow(width, height, filename);
        }

        private void initGL(int width, int height) {
            this.windowWidth = width;
            this.windowHeight = height;

            window = GLUtils.createWindow(width, height, "Realtime FFT Disk", true);
            GLUtils.makeContextCurrent(window, true);

            prog = GLUtils.programFromResources(
                    "resources/shaders/fullscreen.vert",
                    "resources/shaders/fft_disk.frag");
            vao = GLUtils.createFullscreenTriangleVAO();

            locRes = glGetUniformLocation(prog, "uResolution");
            locRotation = glGetUniformLocation(prog, "uRotation");
                locTimeCols = glGetUniformLocation(prog, "uTimeCols");
                locLogBands = glGetUniformLocation(prog, "uLogBands");
                locWriteCol = glGetUniformLocation(prog, "uWriteCol");
                locMatrixTex = glGetUniformLocation(prog, "uMatrixTex");

                // Data upload buffers (must be direct for LWJGL).
                // Size to current logBands to avoid truncation if we change resolution.
                fftBuffer = BufferUtils.createFloatBuffer(logBands);
                columnBuffer = BufferUtils.createFloatBuffer(logBands);

                // Create the history texture (timeCols x logBands) as R32F.
                matrixTex = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, matrixTex);
                // Linear filtering approximates GraphicsFunctions.matrixDisk(..., interpolate=true)
                // and removes the harsh “spokes” caused by nearest-neighbor sampling.
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                // Angular dimension should wrap (like wrapIndex in the original).
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

                // Initialize texture to zeros (matrix starts empty, fills as audio plays)
                FloatBuffer initData = BufferUtils.createFloatBuffer(timeCols * logBands);
                for (int i = 0; i < timeCols * logBands; i++) {
                    initData.put(0f);
                }
                initData.flip();
                glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, timeCols, logBands, 0, GL_RED, GL_FLOAT, initData);
                
                System.out.println("Texture created: " + timeCols + " x " + logBands + ", handle=" + matrixTex);

            glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
                if (w <= 0 || h <= 0) return;
                windowWidth = w;
                windowHeight = h;
            });

            glDisable(GL_DEPTH_TEST);
            glClearColor(0, 0, 0, 1);
        }

    private void startAudioPlayback(String path) {
        try {
            File audioFile = new File(path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(ais);
            audioClip.start();
            System.out.println("Audio playback started, clip length: " + audioClip.getMicrosecondLength() / 1_000_000.0 + " seconds");
        } catch (Exception e) {
            System.err.println("Could not play audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isAudioPlaying() {
        return audioClip != null && audioClip.isRunning();
    }
    
    private double getAudioPositionSeconds() {
        if (audioClip == null) return 0.0;
        return audioClip.getMicrosecondPosition() / 1_000_000.0;
    }

    private void loop() {
        // Wait a moment for audio to start
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        
        int fftBins = fftFrames.isEmpty() ? 512 : fftFrames.get(0).length;
        
        System.out.println("Starting render loop: " + fftFrames.size() + " FFT frames, " +
            audioDurationSeconds + " seconds, timeCols=" + timeCols + 
            ", eightBarsSeconds=" + eightBarsSeconds);
        
        // Frame pacing at target fps
        long frameIntervalNs = 1_000_000_000L / fps;
        long lastFrameTime = System.nanoTime();
        long debugLastPrint = System.currentTimeMillis();
        
        // Run while audio is playing
        while(!glfwWindowShouldClose(window) && isAudioPlaying()) {
            // Frame pacing - wait until it's time for next frame
            long now = System.nanoTime();
            long elapsed = now - lastFrameTime;
            if (elapsed < frameIntervalNs) {
                long sleepMs = (frameIntervalNs - elapsed) / 1_000_000;
                if (sleepMs > 1) {
                    try { Thread.sleep(sleepMs); } catch (InterruptedException ignored) {}
                }
                continue;
            }
            lastFrameTime = now;
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // Get actual audio position
            double audioPos = getAudioPositionSeconds();
            double progress = audioPos / Math.max(0.001, audioDurationSeconds);
            progress = Math.max(0, Math.min(progress, 1.0));
            
            // Debug: print timing info every second
            long nowMs = System.currentTimeMillis();
            if (nowMs - debugLastPrint > 1000) {
                double rotations = audioPos / eightBarsSeconds;
                System.out.println("audioPos=" + String.format("%.2f", audioPos) + 
                    "s, rotations=" + String.format("%.3f", rotations) +
                    ", angle=" + String.format("%.1f", Math.toDegrees(rotationAngle)) + "°");
                debugLastPrint = nowMs;
            }
            
            // Which FFT frame corresponds to current audio position?
            int fftIndex = (int)(progress * (fftFrames.size() - 1));
            fftIndex = Math.max(0, Math.min(fftIndex, fftFrames.size() - 1));
            double[] fftFrame = fftFrames.get(fftIndex);
            
            // Add exactly ONE column per video frame (like original)
            // Shift matrix left, add new column at right
            for (int band = 0; band < logBands; band++) {
                for (int col = 0; col < timeCols - 1; col++) {
                    matrixData[band][col] = matrixData[band][col + 1];
                }
                
                int low = (int) Math.pow(fftBins, (double) band / logBands);
                int high = (int) Math.pow(fftBins, (double) (band + 1) / logBands);
                low = Math.max(0, Math.min(low, fftFrame.length - 1));
                high = Math.max(low + 1, Math.min(high, fftFrame.length));
                
                double avg = 0.0;
                for (int j = low; j < high; j++) avg += fftFrame[j];
                avg /= (high - low);
                matrixData[band][timeCols - 1] = avg;
            }

            glViewport(0, 0, windowWidth, windowHeight);
            glUseProgram(prog);
            glUniform2f(locRes, (float)windowWidth, (float)windowHeight);
            
            // IMPORTANT: The matrix shift (one column per video frame) already produces
            // exactly one full angular cycle over `timeCols` frames (i.e., 8 bars).
            // Applying an extra uRotation here double-counts rotation and makes it look too fast.
            rotationAngle = 0.0f;
            glUniform1f(locRotation, rotationAngle);

            glUniform1i(locTimeCols, timeCols);
            glUniform1i(locLogBands, logBands);

            // writeCol not used in new shader, but set for compatibility
            writeCol = timeCols - 1;
            glUniform1i(locWriteCol, writeCol);

            // Upload entire matrix to texture
            FloatBuffer texData = BufferUtils.createFloatBuffer(timeCols * logBands);
            // glTex(Sub)Image2D expects rows (Y) laid out contiguously:
            // for each band (row), all columns (X) in sequence.
            for (int band = 0; band < logBands; band++) {
                for (int col = 0; col < timeCols; col++) {
                    texData.put((float) matrixData[band][col]);
                }
            }
            texData.flip();
            
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, matrixTex);
            glUniform1i(locMatrixTex, 0);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, timeCols, logBands, GL_RED, GL_FLOAT, texData);

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup(){
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
        }
        if (matrixTex != 0) glDeleteTextures(matrixTex);
        if (vao != 0) glDeleteVertexArrays(vao);
        if (prog != 0) glDeleteProgram(prog);
        GLUtils.destroyWindowAndTerminate(window);
    }
}

