#version 330 core
// Stroboscopic six-color shader
// 5 Hz at 30 FPS => 6 frames per full brightness cycle.
// Dominant solid colors per frame with a subtle mathematical pattern overlay.

out vec4 FragColor;

uniform vec2  uResolution;  // viewport resolution (pixels)
uniform float uPhase;       // [0,1) normalized phase of the 6-frame cycle OR continuous
uniform float uSeed;        // seed for subtle pattern variance
uniform int   uCycleIndex;  // increments each completed 6-frame cycle to rotate colors
uniform int   uShapeCycleLength; // number of shape steps per cycle (default 6)
uniform int   uPackedCounts;     // optional nibble-packed sequence of disk counts (up to 8 steps)
uniform int   uSeqMode;          // 0: linear 1..N, 1: powers of 2, 2: powers of 3

// Small hash helpers (not performance critical for fullscreen @ 30fps)
float hash11(float n) { return fract(sin(n) * 43758.5453123); }
float hash21(vec2 p) { return fract(sin(dot(p, vec2(41.97, 289.13))) * 43758.5453123); }

// Simple gamma-aware mix (approx): apply pow after scalar mix if needed
vec3 gammaCorrect(vec3 c) { return pow(c, vec3(1.0/2.2)); }

void main() {
  // Normalized coordinates, keeping square aspect in math space
  vec2 res = uResolution;
  float minDim = min(res.x, res.y);
  vec2 p = (2.0 * gl_FragCoord.xy - res) / minDim; // roughly [-aspect, aspect]

  // Global slow spin + swirl/twirl: rotate entire coordinate system with a gentle
  // time-based angle and a radius-modulated perturbation for organic motion.
  // This occurs before per-tile mapping so the whole tessellation rotates.
  float r0 = length(p);
  if (r0 > 0.0) { // avoid atan undefined at origin
    float angleBase = atan(p.y, p.x);
    float globalTurnsPerCycle = 0.05; // one full rotation every 20 cycles
    // Continuous cycle time will be added later (tCycle), so use phase first and adjust after computing tCycle.
    // We'll recompute final angle after tCycle is defined.
    // Temporarily store base components; finalize after defining tCycle.
  }

  // Cycle math: map phase to shape frame index; fallback length=6.
  float cyc = fract(uPhase + 1e-6);
  int scl = (uShapeCycleLength > 0) ? uShapeCycleLength : 6;
  float framesF = cyc * float(scl);        // 0 .. < scl
  int frame = int(floor(framesF));         // 0 .. scl-1

  // Determine disk count sequence for this frame.
  const int MAX_DISKS = 24; // upper bound for loops
  int disks;
  if (uPackedCounts != 0) {
    int nib = (uPackedCounts >> (frame * 4)) & 0xF; // 4 bits per entry
    disks = max(1, nib);
  } else if (uSeqMode == 1) {
    // powers of 2
    disks = int(round(pow(2.0, float(frame))));
  } else if (uSeqMode == 2) {
    // powers of 3
    disks = int(round(pow(3.0, float(frame))));
  } else {
    // linear 1..scl
    disks = frame + 1;
  }
  disks = min(disks, MAX_DISKS);
  float tCycle = float(uCycleIndex) + cyc; // continuous cycle time for motion

  // Finalize global spin & swirl now that tCycle is available.
  float globalTurnsPerCycle = 0.05; // repeat: one rotation every 20 cycles
  float globalAngle = 6.28318530718 * globalTurnsPerCycle * tCycle;
  // Swirl strength: radius-proportional perturbation, mildly time-varying.
  float swirlStrength = 0.6; // controls twirl intensity
  float swirlAngleOffset = swirlStrength * r0 * 0.8 * sin(2.0 * tCycle + r0 * 3.0 + uSeed * 0.002);
  float finalAngleBase = (r0 > 0.0) ? atan(p.y, p.x) : 0.0;
  float finalAngle = finalAngleBase + globalAngle + swirlAngleOffset;
  if (r0 > 0.0) {
    vec2 pRot = r0 * vec2(cos(finalAngle), sin(finalAngle));
    p = pRot;
  }

  // Palette: R, Y, G, C, B, M (high-saturation cycle)
  const vec3 palette[6] = vec3[](
    vec3(1.0, 0.0, 0.0),  // Red
    vec3(1.0, 1.0, 0.0),  // Yellow
    vec3(0.0, 1.0, 0.0),  // Green
    vec3(0.0, 1.0, 1.0),  // Cyan
    vec3(0.0, 0.0, 1.0),  // Blue
    vec3(1.0, 0.0, 1.0)   // Magenta
  );
  // Rotate colors each cycle to satisfy "colors could change after each cycle".
  int cycleShift = uCycleIndex % 6;
  vec3 baseColor = palette[(frame + cycleShift) % 6];

  // Constant high brightness (remove strobe luminance variation).
  float brightness = 0.90;

  // Geometry: union of N equidistant disks. Arrange on a ring that spins and breathes.
  float bound = 0.95;           // expanded base bound closer to screen edge
  float seed = uSeed * 0.001;   // small influence if we want slight variation later

  // Base ring radius and animated variants (spin + radial breathing)
  float Nf = float(disks);
  float centerR0 = (disks == 1) ? 0.0 : 0.60 * bound;
  // Radial in/out once per cycle with small amplitude
  float radAmp = 0.30 * bound;  // larger breathing to help reach edges
  float centerRvar = (disks == 1) ? 0.0 : clamp(centerR0 + radAmp * sin(6.28318530718 * (1.0 * tCycle) + seed * 3.7), 0.0, bound - 0.05);

  // Disk radius determined by area heuristic and spacing on the current ring radius
  float drArea  = bound / max(1.0, sqrt(Nf));
  float drSpace;
  if (disks == 1) {
    drSpace = bound;
  } else {
    float sinSeg = sin(3.141592653589793 / Nf);
    drSpace = 0.9 * centerRvar * sinSeg; // limit overlap based on spacing
  }
  float dr = min(bound - centerRvar, min(drArea, drSpace));
  dr = max(0.05, dr);
  // Single disk: add a gentle breathing of its own radius
  if (disks == 1) {
    dr *= 0.90 + 0.10 * (0.5 + 0.5 * sin(6.28318530718 * tCycle + seed * 5.3));
  }

  // Edge reach scaling: occasionally scale up so outermost disk rim touches screen edge.
  // We define current extent (center radius + disk radius). If less than 1.0, we can scale up.
  float curExtent = centerRvar + dr;             // how far motif currently extends (approx)
  float edgeTarget = 1.00;                       // screen edge in normalized space
  float reachPhase = 0.5 + 0.5 * sin(6.28318530718 * (0.33 * tCycle) + seed * 1.7); // slower than luminance
  float scaleUp = edgeTarget / max(0.0001, curExtent);
  // Only scale when needed and modulated by reachPhase so it animates expanding/contracting.
  if (scaleUp > 1.0) {
    float s = mix(1.0, min(scaleUp, 1.25), reachPhase); // clamp to avoid excessive overshoot
    centerRvar *= s;
    dr        *= s;
  }

  // Signed distance to union of disks (AA via fwidth)
  // Replace hard edged disks with soft radial Gaussian disks.
  // We compute a union via: union = 1 - product(1 - g_i) for each disk contribution.
  // Gaussian parameters: larger sigma => bigger, softer shapes.
  // Previously: sigma ≈ 0.36*dr for ~0.02 at r=dr. Increase to make blobs larger.
  float sigma = dr * 0.55;
  float inv2Sig2 = 1.0 / (2.0 * sigma * sigma + 1e-9);
  float oneMinusUnion = 1.0;
  
  // Tessellate: repeat the motif across a fixed square lattice in normalized coords.
  // Choose tile pitch = 2.0 so a motif with extent ~1 reaches tile borders.
  float tilePitch = 2.0;
  vec2 q = mod(p + 0.5 * tilePitch, tilePitch) - 0.5 * tilePitch; // local coords centered at tile

  if (disks == 1) {
    float rc = length(q);
    float g = exp(- (rc * rc) * inv2Sig2);
    oneMinusUnion *= (1.0 - g);
  } else {
    float spinTurnsPerCycle = 0.5; // 180 degrees per cycle of brightness
    float dTheta = 6.28318530718 * spinTurnsPerCycle * tCycle;
    for (int i = 0; i < MAX_DISKS; ++i) {
      if (i >= disks) break;
      float baseAng = (6.28318530718 / Nf) * float(i);
      float ang = baseAng + dTheta;
      vec2 c = vec2(cos(ang), sin(ang)) * centerRvar;
      float rc = length(q - c);
      float g = exp(- (rc * rc) * inv2Sig2);
      oneMinusUnion *= (1.0 - g);
    }
  }
  float unionG = 1.0 - oneMinusUnion; // 0..1 smooth soft union of Gaussians

  // Slight normalization boost so multi-disk unions remain bright but clamp to 1.
  if (disks > 1) unionG = min(1.0, unionG * (1.0 + 0.20 * float(disks - 1)));

  // Black background and more vivid colors: boost saturation of the base color
  // using a simple luminance-preserving stretch toward the pure hue.
  float satBoost = 1.5;
  float luma = dot(baseColor, vec3(0.299, 0.587, 0.114));
  vec3 gray = vec3(luma);
  vec3 vividColor = clamp(gray + (baseColor - gray) * satBoost, 0.0, 1.0);

  // Make the light more colorful by slightly boosting the Gaussian union with brightness.
  float unionBoost = 1.15 + 0.20 * brightness;
  float uG = min(1.0, unionG * unionBoost);

  // White-centered gradient: fade from white at the center(s) to the vivid color toward edges.
  // Use unionG (pre-boost spatial mask) to drive whiteness so overlaps are bright white too.
  float whiteFac = pow(unionG, 1.2); // >1 sharpens white core slightly
  vec3 shaded = mix(vividColor, vec3(1.0), whiteFac);
  vec3 color = shaded * uG * brightness;

  // Ensure we never leak residual light in blackout frames (epsilon clamp)
  if (brightness < 0.015) color = vec3(0.0);

  // Gamma correct for display (optional; comment out if already handled in pipeline)
  color = gammaCorrect(color);

  FragColor = vec4(color, 1.0);
}
