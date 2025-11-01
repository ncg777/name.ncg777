#version 330 core
out vec4 FragColor;

uniform vec2  uResolution;
uniform float uTime;
uniform int   uIterations;     // Koch iterations (clamped to 8)
uniform float uScale;
uniform float uRotation;
uniform float uGlowIntensity;
uniform vec3  uColorPrimary;
uniform vec3  uColorSecondary;

// RD removed: we no longer sample any reaction-diffusion texture

// --- Utils ---
mat2 rot2(float a){ float c=cos(a), s=sin(a); return mat2(c,-s,s,c); }

float smin(float a, float b, float k) {
  float h = clamp(0.5 + 0.5*(b-a)/k, 0.0, 1.0);
  return mix(b, a, h) - k*h*(1.0 - h);
}

float sdSegment(vec2 p, vec2 a, vec2 b) {
  vec2 pa = p - a, ba = b - a;
  float h = clamp(dot(pa, ba)/dot(ba, ba), 0.0, 1.0);
  return length(pa - ba*h);
}

// Quasicrystal interference
float quasi(vec2 p, int waves) {
  float A = 0.0;
  // Use irrational spacing of angles for aperiodicity
  for(int i=0; i<16; ++i){
    if(i >= waves) break;
    float ang = 6.2831853 * float(i) * 0.5 * (sqrt(5.0)-1.0);
    vec2 k = vec2(cos(ang), sin(ang));
    A += cos(dot(k, p) * 3.0);
  }
  return A / float(max(1, waves));
}

// Koch segment distance (iterative inverse-IFS along a single side)
float kochSegmentIter(vec2 p, vec2 a, vec2 b, int it){
  vec2 ex = normalize(b - a);
  vec2 ey = vec2(-ex.y, ex.x);
  float L = length(b - a);

  // local coords: x along segment [0..L], y perpendicular
  vec2 v = vec2(dot(p - a, ex), dot(p - a, ey));
  vec2 w = v / L; // normalize to unit segment

  float s = 1.0; // accumulative scale (3^-n)
  for(int k=0; k<8; ++k){
    if(k >= it) break;
    w *= 3.0;
    s /= 3.0;
    if (w.x > 1.0 && w.x < 2.0) {
      // middle third -> rotate -60° and shift by -1
      w = rot2(-3.14159265/3.0) * (w - vec2(1.0, 0.0));
    } else if (w.x >= 2.0) {
      w.x -= 2.0;
    } else {
      // left third stays as-is
    }
  }

  float d = sdSegment(w, vec2(0.0), vec2(1.0, 0.0));
  return d * L * s;
}

// Build triangle and take min across three sides
float kochSnowflakeDist(vec2 p, float size, int it) {
  // Equilateral triangle centered at origin, circumscribed radius = size
  float r = size;
  vec2 v0 = r * vec2( cos(0.0),              sin(0.0));
  vec2 v1 = r * vec2( cos(2.094395102f),     sin(2.094395102f));     // 120°
  vec2 v2 = r * vec2( cos(4.188790205f),     sin(4.188790205f));     // 240°

  float d0 = kochSegmentIter(p, v0, v1, it);
  float d1 = kochSegmentIter(p, v1, v2, it);
  float d2 = kochSegmentIter(p, v2, v0, it);
  return min(d0, min(d1, d2));
}

void main() {
  // Screen to NDC-ish units preserving aspect
  vec2 uv = (gl_FragCoord.xy - 0.5*uResolution) / uResolution.y;

  // Subtle vignette background (no RD overlay)
  float r = length(uv);
  float vig = smoothstep(1.2, 0.2, r);
  vec3 bg = mix(uColorSecondary * 0.06, uColorSecondary * 0.22, vig);

  // Quasicrystal warp
  vec2 p = uv * uScale;
  p *= rot2(uRotation);
  float q1 = quasi(p * 2.8 + 0.3*vec2(cos(uTime*0.17), sin(uTime*0.21)), 9);
  float q2 = quasi(p.yx * 3.1 + 0.2*vec2(sin(uTime*0.13), cos(uTime*0.19)), 7);
  vec2 pWarp = p + 0.08 * vec2(q1, q2);

  // Koch snowflake edge distance
  int it = min(uIterations, 8);
  float d = kochSnowflakeDist(pWarp, 0.75, it);

  // Edge + glow
  float lineWidth = 0.0045;
  float edge = smoothstep(lineWidth, 0.0, d);
  float glow = exp(-14.0 * d) * uGlowIntensity;

  // Compose: snow on top of a subdued RD base
  vec3 snow = mix(uColorSecondary, uColorPrimary, edge) + glow * uColorPrimary;
  vec3 col = bg + snow;

  FragColor = vec4(col, 1.0);
}