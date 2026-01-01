#version 330 core
out vec4 FragColor;

uniform vec2  uResolution;     // pixels
uniform float uPhase;          // [0,1], use k/(upper-1) for perfect loop
uniform float uScale;          // world scale (bigger = zoom out)
uniform int   uOctaves;        // FBM octaves (1..8)
uniform float uLacunarity;     // freq growth per octave (>1)
uniform float uGain;           // amp attenuation per octave (0..1)
uniform int   uIsoBands;       // contour bands (e.g., 24..96)
uniform float uLineThickness;  // ~0.05..0.35 (dimensionless)
uniform float uSeed;           // seed for procedural params
uniform float uBubbleAmp;      // amplitude of bubbling vertical displacement
uniform float uBubbleFreq;     // number of bubbling cycles per loop
uniform float uBubbleDetail;   // scales domain for bubble noise (detail)

// Constants
const float PI  = 3.14159265358979323846;
const float TAU = 6.28318530717958647692;

// --- Hash/Noise (value noise + smooth interp) ---
float hash(vec2 p) {
  return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
  vec2 i = floor(p);
  vec2 f = fract(p);
  vec2 u = f * f * (3.0 - 2.0 * f);
  return mix(
    mix(hash(i + vec2(0,0)), hash(i + vec2(1,0)), u.x),
    mix(hash(i + vec2(0,1)), hash(i + vec2(1,1)), u.x),
    u.y
  );
}

// Reproducible variations from seed
float h11(float n) { return fract(sin(n) * 43758.5453123); }
vec2  h21(float n) {
  // Map seed to a pseudo-random 2D vec in [0,1)
  return vec2(h11(n * 19.0 + 0.73), h11(n * 23.0 + 1.91));
}

// FBM that returns a normalized value in ~[0,1]
float fbm(vec2 p, int octaves, float lac, float gain) {
  float sum = 0.0;
  float amp = 0.5; // start at 0.5 to keep sum in [0,1] if noise in [0,1]
  float norm = 0.0;
  vec2  pp = p;
  for (int i = 0; i < 12; ++i) {
    if (i >= octaves) break;
    sum  += amp * noise(pp);
    norm += amp;
    pp   *= lac;
    amp  *= gain;
  }
  return (norm > 1e-6) ? sum / norm : 0.0;
}

// HSV -> RGB
vec3 hsv2rgb(vec3 c) {
  vec3 p = abs(fract(c.xxx + vec3(0., 2./6., 4./6.)) * 6. - 3.);
  vec3 rgb = clamp(p - 1., 0., 1.);
  return c.z * mix(vec3(1.), rgb, c.y);
}

void main() {
  // Isotropic coordinates
  float minDim = min(uResolution.x, uResolution.y);
  vec2  p = (gl_FragCoord.xy - 0.5 * uResolution) / minDim; // ~[-0.5,0.5]

  // Terrain domain (scale, seed shift, and perfectly looping time offset)
  vec2 seedShift = (h21(uSeed * 0.137) - 0.5) * 1024.0;   // large static offset
  vec2 timeShift = vec2(cos(TAU * uPhase), sin(TAU * uPhase)) * (0.75 * uScale);
  vec2 world = p * uScale + seedShift + timeShift;

  // Loop-safe warp and base height
  vec2 warpOff = vec2(cos(TAU * (uPhase + 0.27)), sin(TAU * (uPhase + 0.27))) * (0.33 * uScale);
  float base0 = fbm(world + warpOff, max(1, uOctaves), max(1.01, uLacunarity), clamp(uGain, 0.01, 0.99));
  float signed0 = base0 * 2.0 - 1.0;
  float tanh0 = tanh(1.35 * signed0);

  // Nonlinear warp using tanh height and a gentle swirl
  vec2 swirl = vec2(-p.y, p.x);
  vec2 warp = (0.28 * uScale) * (0.7 * swirl * tanh0
             + 0.3 * vec2(sin(world.y * 1.05 + tanh0 * 2.5), cos(world.x * 1.05 - tanh0 * 2.5)));
  vec2 world2 = world + warp;

  // Tanh-shaped terrain with extra nonlinear remap
  float base1 = fbm(world2 + warpOff * 0.6, max(1, uOctaves), max(1.01, uLacunarity), clamp(uGain, 0.01, 0.99));
  float signed1 = base1 * 2.0 - 1.0;
  float h = 0.5 + 0.5 * tanh(1.55 * signed1);
  h = clamp(h + 0.18 * sin(TAU * h + 1.3 * tanh0), 0.0, 1.0);
  float hCurve = h * h * (3.0 - 2.0 * h); // smoothstep curve
  float ridged = 1.0 - abs(2.0 * hCurve - 1.0);
  float hFinal = mix(hCurve, ridged, 0.35 + 0.15 * sin(TAU * uPhase));

  // Bubbling displacement: loop-safe temporal wave modulated by localized noise
  float bubbleDet = max(0.25, uBubbleDetail);
  vec2 bubbleTimeShift = vec2(cos(TAU * (uPhase + 0.43)), sin(TAU * (uPhase + 0.43))) * (0.55 * bubbleDet);
  float bubbleNoise = fbm(world2 * bubbleDet + bubbleTimeShift, max(1, uOctaves), max(1.01, uLacunarity), clamp(uGain, 0.01, 0.99));
  float bubbleWave = sin(TAU * (uBubbleFreq * uPhase) + bubbleNoise * PI + 1.5 * tanh0);
  float hBubbled = hFinal + uBubbleAmp * bubbleWave * (0.35 + 0.65 * bubbleNoise);
  hBubbled = clamp(hBubbled, 0.0, 1.0);

  // Screen-space gradient (for shading and hue)
  float e = 1.25 / minDim; // small isotropic step
  float hx = fbm(world2 + vec2(e, 0.0), uOctaves, uLacunarity, uGain)
           - fbm(world2 - vec2(e, 0.0), uOctaves, uLacunarity, uGain);
  float hy = fbm(world2 + vec2(0.0, e), uOctaves, uLacunarity, uGain)
           - fbm(world2 - vec2(0.0, e), uOctaves, uLacunarity, uGain);
  float slope = length(vec2(hx, hy));

  // Isoclines (contours) on height
  int   bands = max(1, uIsoBands);
  float line  = abs(sin(PI * float(bands) * hBubbled));
  float lt    = clamp(uLineThickness, 0.02, 0.75);

  // Topo-like core line + soft glow
  float core = pow(max(0.0, 1.0 - (line / lt)), 1.35);
  float glow = pow(max(0.0, 1.0 - (line / (lt * 3.0))), 2.2);
  float intensity = clamp(core + 0.5 * glow, 0.0, 1.0);

  // Mild vignette to avoid hard edges
  float r = length(p) / 0.9; // 0 at center, ~1 near edge
  float vignette = smoothstep(1.0, 0.6, r);
  intensity *= vignette;

  // Hue varies with height and slope, with a slow time spin
  float hue = fract(0.62 * hBubbled + 0.18 * slope + 0.1 * sin(TAU * uPhase));
  float sat = mix(0.65, 1.0, intensity);
  float bri = mix(0.12, 1.0, intensity);
  hue = fract(hue + 0.05 * tanh0 + 0.04 * sin(TAU * (uPhase + hBubbled)));

  vec3 rgb = hsv2rgb(vec3(hue, sat, bri));
  FragColor = vec4(rgb, 1.0);
}
