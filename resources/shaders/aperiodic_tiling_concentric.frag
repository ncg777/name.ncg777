#version 330 core
// Aperiodic tiling (Penrose-like, pentagrid method) with concentric shapes per tile
// Real-time, single-pass fragment shader.
//
// Idea: use 5 families of parallel lines (de Bruijn pentagrid). The region between
// two nearest lines from two families forms a rhomb (Penrose rhomb tiling). We pick
// the two closest line families per pixel to identify the local tile and build a
// tile-local (u,v) coordinate, then render concentric rings or polygons inside it.
//
// This is an analytic approximation suitable for shaders; it's a classic technique inspired
// by Inigo Quilez's work on Penrose tilings.

out vec4 FragColor;

uniform vec2  uResolution;     // viewport size in pixels
uniform float uTime;           // time in seconds
uniform float uScale;          // tiling density scale (suggest 2..8)
uniform int   uRings;          // number of concentric rings inside each tile
uniform float uThickness;      // ring thickness (0..0.5 in tile uv)
uniform float uSeed;           // randomization seed for phase offsets and color
uniform int   uShape;          // 0: circular rings, 1: diamond rings, 2: square-ish rings

// Helpers
float hash11(float x) { return fract(sin(x) * 43758.5453123); }
float hash21(vec2 p) { return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123); }
vec3 gammaCorrect(vec3 c) { return pow(c, vec3(1.0/2.2)); }

// Palette util: boost saturation while keeping luminance
vec3 vivid(vec3 c, float k) {
  float l = dot(c, vec3(0.299, 0.587, 0.114));
  vec3 g = vec3(l);
  return clamp(g + (c - g) * k, 0.0, 1.0);
}

// 2D value noise + fbm for organic warps
float vh(vec2 p) { return fract(sin(dot(p, vec2(27.619, 57.583))) * 43758.5453); }
float vnoise(vec2 p) {
  vec2 i = floor(p);
  vec2 f = fract(p);
  float a = vh(i);
  float b = vh(i + vec2(1.0, 0.0));
  float c = vh(i + vec2(0.0, 1.0));
  float d = vh(i + vec2(1.0, 1.0));
  vec2 u = f * f * (3.0 - 2.0 * f);
  return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}
float fbm(vec2 p) {
  float v = 0.0;
  float a = 0.5;
  mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
  for (int i = 0; i < 5; ++i) {
    v += a * vnoise(p);
    p = m * p + 3.17;
    a *= 0.5;
  }
  return v;
}
mat2 rot2(float a) { float c = cos(a), s = sin(a); return mat2(c, -s, s, c); }

void main() {
  // Normalize coordinates so the smallest screen dimension maps to [-1,1]
  vec2 res = uResolution;
  float minDim = min(res.x, res.y);
  vec2 p = (2.0 * gl_FragCoord.xy - res) / max(1.0, minDim);

  // Global bend (parabolic) and swirl to avoid straight-line look
  float bend = 0.22 * sin(0.21 * uTime + uSeed * 0.01);
  p.x += bend * (p.y * p.y);

  float r0 = length(p);
  float a0 = atan(p.y, p.x);
  float swirl = 0.55 * sin(0.7 * uTime + r0 * 2.6 + uSeed * 0.013) * exp(-0.15 * r0 * r0);
  a0 += swirl;
  p = r0 * vec2(cos(a0), sin(a0));

  // Multi-scale domain warp using fbm
  vec2 q = vec2(
    fbm(p * 2.4 + uTime * 0.20 + uSeed),
    fbm(p * 2.4 - uTime * 0.18 - uSeed)
  );
  p += 0.35 * (q - 0.5);

  // Parameters and directions for pentagrid
  float scale = (uScale > 0.0) ? uScale : 3.0;
  float t = uTime * 0.15; // slow phase drift
  float phi = 2.0 * 3.141592653589793 / 5.0;

  // Precompute 5 unit directions (grid normals)
  vec2 dir[5];
  for (int i = 0; i < 5; ++i) {
    float a = (float(i) * phi);
    dir[i] = vec2(cos(a), sin(a));
  }

  // For each family: signed fractional coordinate relative to the nearest line
  float sd[5];     // signed distance in normalized stripe space (-0.5..0.5)
  float ad[5];     // absolute distance to nearest line (0..0.5)
  int   ki[5];     // integer line index (region id)

  // Family-dependent phase offsets (repeatable randomness + gentle animation)
  for (int i = 0; i < 5; ++i) {
    float offs = hash11(float(i) + uSeed * 0.017) * 10.0 + t * (0.3 + 0.2 * hash11(7.7 * float(i) + uSeed));
    float s = dot(p, dir[i]) * scale + offs;
    // Curvilinear perturbation of the line families using the orthogonal coordinate
    vec2 tang = vec2(-dir[i].y, dir[i].x);
    float orth = dot(p, tang);
    s += 0.18 * sin(orth * scale * 1.6 + t * 2.2 + float(i) * 1.234 + uSeed * 0.13);
    float f = fract(s);
    sd[i] = f - 0.5;        // signed in [-0.5,0.5]
    ad[i] = min(f, 1.0 - f); // absolute distance to nearest integer line
    ki[i] = int(floor(s));
  }

  // Find two closest families (smallest ad)
  int i0 = 0, i1 = 1;
  if (ad[1] < ad[0]) { i0 = 1; i1 = 0; }
  for (int i = 2; i < 5; ++i) {
    if (ad[i] < ad[i0]) { i1 = i0; i0 = i; }
    else if (ad[i] < ad[i1]) { i1 = i; }
  }

  // Tile-local coordinates (u,v) from the two closest families
  // sd[] are in [-0.5,0.5], boundaries at +/- 0.5 form the rhomb edges for those directions.
  vec2 uv = vec2(sd[i0], sd[i1]);

  // Local tile twist/swirl to add internal motion
  float tileTwist = (hash11(float(ki[i0] + ki[i1])) - 0.5) * 0.8 + 0.25 * sin(uTime * 0.7 + float(ki[i0] - ki[i1]));
  uv = rot2(tileTwist * 0.3) * uv;

  // Optional: soft tile edge mask (anti-aliasing)
  float edge = 1.0;
  float aa = 2.0 / max(2.0, minDim); // screen-space AA
  edge *= smoothstep(0.5, 0.5 - aa, abs(uv.x));
  edge *= smoothstep(0.5, 0.5 - aa, abs(uv.y));

  // Concentric figure metric (with angular ripple for filigree)
  float r;
  if (uShape == 1) {
    // Diamond (L-infinity rotated): contours are max(|u|,|v|)
    r = max(abs(uv.x), abs(uv.y));
  } else if (uShape == 2) {
    // Square-ish (L1): contours are |u|+|v|
    r = (abs(uv.x) + abs(uv.y)) * 0.70710678; // scaled so corners ~0.5
  } else {
    // Circular in (u,v)
    r = length(uv);
  }

  // Normalize radius so the farthest corner is near 0.5 (for circular/diamond)
  float rN = r / 0.5;

  // Angular filigree and noise displacement toward edges
  float ang = atan(uv.y, uv.x);
  float rip = 0.08 * sin(6.0 * ang + uTime * 1.5 + float(ki[i0] + ki[i1]) * 0.37);
  float ndisp = 0.06 * (fbm(uv * 18.0 + hash21(vec2(ki[i0], ki[i1])) * 10.0 + uTime * 0.5) - 0.5);
  rN = clamp(rN + (1.0 - smoothstep(0.0, 1.0, rN)) * (rip + ndisp), 0.0, 2.0);

  // Build ring pattern
  int rings = (uRings > 0) ? uRings : 8;
  float thickness = clamp(uThickness, 0.005, 0.45);
  // Modulate thickness with noise for lively variation
  float tmod = 0.6 + 0.5 * (fbm(uv * 12.0 + uTime * 0.8 + uSeed) - 0.5);
  thickness *= clamp(tmod, 0.4, 1.4);
  float tf = fract(rN * float(rings));
  float band = 1.0 - smoothstep(0.5 - thickness, 0.5, abs(tf - 0.5));

  // A hint of grid lines (thin bright lines at tile edges)
  float grid = 0.0;
  for (int i = 0; i < 5; ++i) {
    float g = smoothstep(0.02, 0.0, ad[i]);
    grid = max(grid, g);
  }

  // Per-tile color from integer indices (aperiodic hash)
  // Combine two closest families' indices; this is stable within a tile but varies across tiles.
  int ida = ki[i0];
  int idb = ki[i1];
  float h = hash21(vec2(float(ida), float(idb)) * 0.123 + uSeed);
  // Map hash to a vivid hue and animate hue slightly with local noise
  float hShift = 0.15 * (fbm(uv * 8.0 + uTime * 0.4 + h * 10.0) - 0.5);
  vec3 base = vivid(vec3(0.6 + 0.4 * cos(6.28318 * (h + hShift + vec3(0.0, 0.33, 0.67)))), 1.7);

  // Combine: ring color inside tile, grid on top
  vec3 col = base * band * edge;
  col = mix(col, vec3(1.0), grid * 0.8);

  // Node glow at multi-family intersections
  float node = 1.0;
  for (int i = 0; i < 5; ++i) {
    node *= smoothstep(0.06, 0.0, ad[i]);
  }
  col = mix(col, vec3(1.0), pow(clamp(node, 0.0, 1.0), 1.6));

  // Subtle shading toward tile center for depth
  float centerShade = smoothstep(1.0, 0.0, rN);
  col *= mix(0.9, 1.1, centerShade);

  // Subtle vignette based on global radius
  float vign = smoothstep(1.4, 0.3, length(p));
  col *= mix(0.9, 1.1, vign);

  // Gamma correct
  col = gammaCorrect(col);

  FragColor = vec4(col, 1.0);
}
