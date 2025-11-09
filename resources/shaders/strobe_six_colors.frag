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

// Decode disk count for a given index based on packed sequence or mode.
// Moved outside main() because GLSL 330 does not allow function definitions inside main.
const int MAX_DISKS = 24; // upper bound for loops (shared with main logic)
int decodeCount(int idx, int shapeCycleLength, int packedCounts, int seqMode) {
  int cnt;
  if (packedCounts != 0) {
    int nib = (packedCounts >> (idx * 4)) & 0xF;
    cnt = max(1, nib);
  } else if (seqMode == 1) {
    cnt = int(round(pow(2.0, float(idx))));
  } else if (seqMode == 2) {
    cnt = int(round(pow(3.0, float(idx))));
  } else {
    cnt = idx + 1; // linear
  }
  return min(cnt, MAX_DISKS);
}

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

  // Determine disk count sequence for this frame and the next, then smoothly morph between them.

  float stepFrac = framesF - float(frame); // [0,1) fractional progress within this shape step
  int nextFrame = (frame + 1) % scl;

  int countA = decodeCount(frame, scl, uPackedCounts, uSeqMode);
  int countB = decodeCount(nextFrame, scl, uPackedCounts, uSeqMode);
  int maxCount = (countA > countB) ? countA : countB;
  float countMix = mix(float(countA), float(countB), stepFrac);
  // Use an "effective" continuous count for size heuristics; discrete counts appear/fade with weights.
  float effectiveCount = countMix;
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
  vec3 colorA = palette[(frame + cycleShift) % 6];
  vec3 colorB = palette[(nextFrame + cycleShift) % 6];

  // Constant high brightness (remove strobe luminance variation).
  float brightness = 0.90;

  // Geometry: re-layout as a "domino" rectangle (approx 2:1 aspect, two square halves) instead of a ring.
  // We keep soft Gaussian disks and morph logic. Disks are distributed in two vertical columns (left/right halves).
  // For visual familiarity with domino pips we stack rows per half; when counts change we fade rows in/out.
  float bound = 0.90;           // overall extent inside the tile
  float seed = uSeed * 0.001;
  float Nf = max(1.0, float(maxCount));

  // Domino dimensions (centered): width ≈ 2 * height.
  float domH = bound;           // height of domino area
  float domW = 2.0 * domH;      // width of domino area
  // Ensure we stay inside tile pitch (tilePitch=2) by scaling down if needed.
  float scaleDom = min(1.0, 0.95 / max(domH * 0.5, domW * 0.5));
  domH *= scaleDom; domW *= scaleDom;

  // Split counts between left and right halves (left gets the extra when odd)
  int countLeft  = (maxCount + 1) / 2;
  int countRight = maxCount / 2;
  // Row counts for current morph endpoints (to compute spacing fairly for both A & B counts)
  int rowsLeft  = countLeft;
  int rowsRight = countRight;

  // Derive disk radius from vertical spacing & horizontal half-width.
  float halfW = domW * 0.5; // half the domino width = width of one square half
  float halfSide = halfW;   // half's side length (since halves are square by construction)
  float margin = 0.06 * halfSide; // internal padding
  float vSpaceLeft  = (rowsLeft  > 0) ? (halfSide - 2.0 * margin) / float(rowsLeft)  : (halfSide - 2.0 * margin);
  float vSpaceRight = (rowsRight > 0) ? (halfSide - 2.0 * margin) / float(rowsRight) : (halfSide - 2.0 * margin);
  float dr = min( (vSpaceLeft  * 0.5), (vSpaceRight * 0.5) );
  dr = min(dr, (halfSide * 0.5 - margin) * 0.9); // also respect horizontal room
  dr = max(0.04, dr);
  // Gentle breathing for a single disk case.
  if (maxCount == 1) {
    dr *= 0.90 + 0.10 * (0.5 + 0.5 * sin(6.28318530718 * tCycle + seed * 5.3));
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

  // Build union with per-disk weights to morph between countA and countB, and accumulate per-disk colors.
  // No angular spin for domino layout; retain a subtle micro-jitter using seed & time for organic feel.
  float jitterPhase = sin(2.0 * tCycle + seed * 13.17);
  vec3 sumC = vec3(0.0);
  float sumG = 0.0;
  for (int i = 0; i < MAX_DISKS; ++i) {
    if (i >= maxCount) break;
    // Weight rules:
    // If disk index < min(countA,countB) -> fully present.
    // If increasing (countB>countA): indices in [countA, countB) fade in with stepFrac.
    // If decreasing (countB<countA): indices in [countB, countA) fade out with (1-stepFrac).
    int minAB = (countA < countB) ? countA : countB;
    int maxAB = (countA > countB) ? countA : countB;
    float w;
    if (i < minAB) {
      w = 1.0;
    } else if (countB > countA) {
      // fade in new disks
      if (i >= countA && i < countB) w = smoothstep(0.0, 1.0, stepFrac); else w = (i < countB ? 1.0 : 0.0);
    } else if (countB < countA) {
      // fade out disks being removed
      if (i >= countB && i < countA) w = 1.0 - smoothstep(0.0, 1.0, stepFrac); else w = (i < countB ? 1.0 : 0.0);
    } else {
      w = 1.0; // unchanged count
    }
    if (w <= 0.0001) continue;
    // Per-disk color: persistent disks transition A->B; new disks use B; disappearing disks use A.
    vec3 ci;
    if (i < minAB) {
      ci = mix(colorA, colorB, stepFrac);
    } else if (countB > countA && i >= countA && i < countB) {
      ci = colorB;
    } else if (countB < countA && i >= countB && i < countA) {
      ci = colorA;
    } else {
      ci = mix(colorA, colorB, stepFrac);
    }
    // Compute domino position.
    bool isLeft = (i < countLeft);
    int row = isLeft ? i : (i - countLeft);
    int rows = isLeft ? rowsLeft : rowsRight;
    // y in [-halfSide/2, halfSide/2]
    float y = 0.0;
    if (rows > 0) {
      float fy = (float(row) + 0.5) / float(rows); // (0,1]
      y = (fy - 0.5) * (halfSide - 2.0 * margin);
    }
    float xHalfCenter = (isLeft ? -0.5 : 0.5) * domW * 0.5; // centers at -domW/4 and +domW/4
    float x = xHalfCenter + 0.0; // could add per-row x jitter later
    // Mild per-disk jitter to reduce perfect grid artifacts (scaled by disk weight w later implicitly via alpha)
    float j = (hash11(float(i) + uSeed) - 0.5) * 0.15 * dr;
    x += j * 0.7;
    y += j * 0.5 + 0.02 * dr * jitterPhase;
    vec2 c = vec2(x, y);
    float rc = length(q - c);
    float g = exp(- (rc * rc) * inv2Sig2) * w;
    oneMinusUnion *= (1.0 - g);
    sumC += ci * g;
    sumG += g;
  }
  float unionG = 1.0 - oneMinusUnion; // 0..1 smooth soft union of Gaussians

  // Slight normalization boost so multi-disk unions remain bright but clamp to 1.
  // Use average of counts during morph for normalization scaling.
  float avgCount = 0.5 * (float(countA) + float(countB));
  if (avgCount > 1.1) unionG = min(1.0, unionG * (1.0 + 0.20 * (avgCount - 1.0)));

  // Black background and more vivid colors: boost saturation of the blended per-disk color
  // using a simple luminance-preserving stretch toward the pure hues.
  float satBoost = 1.5;
  vec3 baseBlend = (sumG > 1e-5) ? (sumC / sumG) : mix(colorA, colorB, stepFrac);
  float luma = dot(baseBlend, vec3(0.299, 0.587, 0.114));
  vec3 gray = vec3(luma);
  vec3 vividColor = clamp(gray + (baseBlend - gray) * satBoost, 0.0, 1.0);

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
