#version 330 core

uniform vec2 uResolution;
uniform sampler2D uMatrixTex; // R32F, size: (timeCols x logBands)
uniform int uTimeCols;
uniform int uLogBands;
uniform int uWriteCol;    // not used anymore, kept for compatibility
uniform float uRotation;  // radians

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
  vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
  vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
  return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
  vec2 uv = gl_FragCoord.xy / uResolution;
  
  // Center the disk and scale so it fits in the smaller dimension
  float aspect = uResolution.x / uResolution.y;
  vec2 p;
  if (aspect > 1.0) {
    p = vec2((uv.x - 0.5) * aspect, uv.y - 0.5);
  } else {
    p = vec2(uv.x - 0.5, (uv.y - 0.5) / aspect);
  }

  float r = length(p);
  
  // Get angle WITHOUT rotation first (for data lookup)
  float a = atan(p.y, p.x);
  
  // Add rotation offset to angle - this rotates the VIEW not the data mapping
  // Positive rotation should make the disk appear to rotate counter-clockwise
  float rotatedAngle = a - uRotation;
  
  // Normalize to [0,1) for column lookup
  float t = (rotatedAngle + 3.14159265) / (2.0 * 3.14159265);
  t = fract(t); // wrap to [0,1)

  // Disk geometry
  // Use a near-max outer radius to “zoom” the disk to the viewport bounds.
  // Keep a tiny margin to avoid edge clipping/aliasing at the exact boundary.
  float inner = 0.05;
  float outer = 0.499;
  if (r < inner || r > outer) {
    fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    return;
  }

  int bands = max(uLogBands, 1);
  int cols  = max(uTimeCols, 1);

  // === Match GraphicsFunctions.matrixDisk(..., interpolate=true) ===
  // Radial: it uses adjustedDi = sqrt(di/m) * m which corresponds to sqrt(bandF).
  float bandF = (r - inner) / (outer - inner); // [0,1]
  float bandPos = sqrt(clamp(bandF, 0.0, 1.0)) * float(max(bands - 1, 1));

  // Angular: continuous column position around the disk.
  float colPos = t * float(cols); // [0,cols)

    // Sample with fractional positions; GL_LINEAR filtering provides bilinear interpolation.
    // Additionally, apply a tiny angular blur (3 taps) to suppress thin radial spokes.
    vec2 texCoord = vec2((colPos + 0.5) / float(cols), (bandPos + 0.5) / float(bands));
    float dx = 1.0 / float(cols);
    float v = 0.25 * texture(uMatrixTex, texCoord + vec2(-dx, 0.0)).r
      + 0.50 * texture(uMatrixTex, texCoord).r
      + 0.25 * texture(uMatrixTex, texCoord + vec2( dx, 0.0)).r;

  // === Make quiet content stay dark ===
  // A small floor removes background “hiss”, and gamma reduces low-level fill.
  // NOTE: FFT magnitudes are normalized in Java; keep the floor tiny and boost more.
  v = max(0.0, v - 0.0002);
  v = clamp(v * 40.0, 0.0, 1.0);
  v = pow(v, 1.35);

  // Radial color gradient: red (inner) → blue (outer)
  float hue = mix(0.0, 2.0/3.0, clamp(bandF, 0.0, 1.0));
  vec3 rgb = hsv2rgb(vec3(hue, 1.0, 1.0));
  fragColor = vec4(rgb * v, v);
}
