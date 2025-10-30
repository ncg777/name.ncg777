#version 330 core
out vec4 FragColor;

uniform vec2  uResolution;     // pixels
uniform float uPhase;          // [0,1], use k/(upper-1) for perfect loop
uniform int   uComponents;     // e.g., 5..8
uniform int   uIsoBands;       // e.g., 36..64
uniform float uLineThickness;  // ~0.08..0.15
uniform float uNoiseAmount;    // ~0.10..0.22 (radians)
uniform float uSeed;           // seed for procedural params

// Utility: hash -> [0,1)
float hash11(float n) {
  return fract(sin(n) * 43758.5453123);
}
float hash21(vec2 p) {
  return fract(sin(dot(p, vec2(12.9898,78.233))) * 43758.5453);
}

// Make reproducible “random” from index i and seed
float randI(int i, float s, float ch) {
  return hash11(float(i) * 17.0 + s * 251.0 + ch * 0.61803);
}
int randint(int i, float s, float ch, int lo, int hiInclusive) {
  float r = randI(i, s, ch);
  return lo + int(floor(r * float(hiInclusive - lo + 1)));
}
float randUniform(int i, float s, float ch, float lo, float hi) {
  float r = randI(i, s, ch);
  return mix(lo, hi, r);
}

// HSV -> RGB (neon colors via HSB/HSV mapping)
vec3 hsv2rgb(vec3 c) {
  vec3 p = abs(fract(c.xxx + vec3(0., 2./6., 4./6.)) * 6. - 3.);
  vec3 rgb = clamp(p - 1., 0., 1.);
  return c.z * mix(vec3(1.), rgb, c.y);
}

void main() {
  // Isotropic coords: ensure equal pixel step in x,y
  float minDim = min(uResolution.x, uResolution.y);
  // Map fragCoord from [0,res] to centered coords scaled by minDim (so dx=dy=1/minDim)
  vec2 p = (2.0 * gl_FragCoord.xy - uResolution) / minDim;

  // Polar
  float rn = length(p) / sqrt(2.0);     // ~[0,1] inside the inscribed circle
  float th = atan(p.y, p.x);            // [-pi, pi]

  // Multi-component radial sine sum with phase noise
  // Match slow, smooth motion with integer cycles for seamless loop
  // Bounds as in your Java version
  int fMin = 1, fMax = 4;
  float aMin = 0.20, aMax = 0.85;
  int tCyclesMin = 0, tCyclesMax = 2;
  int nThetaMin = 1, nThetaMax = 4;
  int nTimeMin  = 1, nTimeMax  = 2;
  int nRadMin   = 0, nRadMax   = 2;

  float s = 0.0;
  float ampSum = 0.0;

  // Precompute terms
  float twoPI = 6.283185307179586;
  for (int i = 0; i < 64; ++i) {
    if (i >= uComponents) break;

    int f      = randint(i, uSeed, 11.0, fMin, fMax);
    float amp  = randUniform(i, uSeed, 13.0, aMin, aMax);
    float phi0 = randUniform(i, uSeed, 17.0, 0.0, twoPI);
    int tCyc   = randint(i, uSeed, 19.0, tCyclesMin, tCyclesMax);
    int nTh    = randint(i, uSeed, 23.0, nThetaMin, nThetaMax);
    int nTi    = randint(i, uSeed, 29.0, nTimeMin,  nTimeMax);
    int nRa    = randint(i, uSeed, 31.0, nRadMin,   nRadMax);
    float nPhi = randUniform(i, uSeed, 37.0, 0.0, twoPI);

    // Balance amplitudes to avoid over-saturation
    amp *= 1.0 / max(1.0, sqrt(float(uComponents)));
    ampSum += abs(amp);

    float tTerm = twoPI * (float(tCyc) * uPhase);
    // Center-safe angular noise & gentle temporal cycles
    float angNoise = sin(float(nTh) * th + twoPI * float(nTi) * uPhase + nPhi);
    float radNoise = sin(twoPI * (float(nRa) * rn + float(nTi + 1) * uPhase) + 0.37 * nPhi);
    float noise = uNoiseAmount * (rn * angNoise + 0.4 * radNoise);

    s += amp * sin(twoPI * (float(f) * rn) + phi0 + tTerm + noise);
  }
  float ampNorm = (ampSum > 1e-9) ? ampSum : 1.0;
  float v = s / ampNorm;

  // Stripe function (isoclines): bright near sin(pi*isoBands*v) ≈ 0
  float line = abs(sin(3.141592653589793 * float(uIsoBands) * v));
  float lt = clamp(uLineThickness, 0.01, 0.75);

  // Neon core + glow
  float core = pow(max(0.0, 1.0 - (line / lt)), 1.5);
  float glow = pow(max(0.0, 1.0 - (line / (lt * 2.8))), 2.2);
  float intensity = min(1.0, core + 0.45 * glow);

  // Soft rim fade to avoid edge artifacts
  if (rn > 0.985) {
    float t = clamp((rn - 0.985) / (1.0 - 0.985), 0.0, 1.0);
    intensity *= (1.0 - t);
  }

  // Seamless neon hue: only periodic sin/cos terms (no raw theta wrap, no linear phase drift)
  float t1 = sin(twoPI * (1.0 * uPhase));
  float t2 = cos(twoPI * (2.0 * uPhase));
  float hue = 0.24 * rn
            + 0.18 * v
            + 0.12 * sin(th)
            + 0.08 * cos(2.0 * th)
            + 0.06 * sin(3.0 * th)
            + 0.07 * t1
            + 0.05 * t2
            + 0.06 * sin(twoPI * (0.25 * rn + 1.0 * uPhase));
  hue = hue - floor(hue);

  float sat = min(1.0, 0.9 + 0.1 * intensity);
  float bri = min(1.0, 0.95 * intensity + 0.35 * glow);

  vec3 rgb = hsv2rgb(vec3(hue, sat, bri));
  // Black background automatically when intensity ~ 0
  FragColor = vec4(rgb, 1.0);
}