#version 330 core
out vec4 FragColor;

uniform vec2  uResolution;     // pixels
uniform float uPhase;          // [0,1] loop time
uniform int   uSymmetry;       // rotational symmetry copies
uniform int   uSubdivisions;   // curve sample points per copy
uniform float uScale;          // overall radius scale (0.0 - 1.0)

uniform float uSinAmp;         // amplitude of the outer sine
uniform float uBaseFreq;       // base frequency term
uniform float uModAmp;         // amplitude modulator
uniform float uModFreq;        // inner modulation frequency
uniform float uModDiv;         // inner modulation divisor
uniform float uThetaScale;     // scale of theta

uniform float uLineWidth;      // antialiased line width in pixels
uniform float uHueCycles;      // hue cycles per loop

uniform float uSeed;           // random seed for variation

const float PI  = 3.14159265358979323846;
const float TAU = 6.28318530717958647692;

float dot2(vec2 v) { return dot(v, v); }

// Exact SDF to quadratic Bezier (Inigo Quilez), returns vec2(distance, closest_t)
// where closest_t in [0,1] is the parameter on the Bezier nearest to pos.
vec2 sdBezier(vec2 pos, vec2 A, vec2 B, vec2 C) {
  vec2 a = B - A;
  vec2 b = A - 2.0*B + C;
  if (dot(b, b) < 1e-10) {
    // Degenerate: line fallback — compute t along segment
    vec2 pa = pos - A, ba = C - A;
    float ht = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return vec2(length(pa - ba * ht), ht);
  }
  vec2 c = a * 2.0;
  vec2 d = A - pos;
  float kk = 1.0 / dot(b, b);
  float kx = kk * dot(a, b);
  float ky = kk * (2.0*dot(a,a) + dot(d,b)) / 3.0;
  float kz = kk * dot(d, a);
  float p  = ky - kx*kx;
  float p3 = p*p*p;
  float q  = kx*(2.0*kx*kx - 3.0*ky) + kz;
  float h  = q*q + 4.0*p3;
  float res;
  float bestT;
  if (h >= 0.0) {
    h = sqrt(h);
    vec2 x = (vec2(h, -h) - q) / 2.0;
    vec2 uv = sign(x) * pow(abs(x), vec2(1.0/3.0));
    bestT = clamp(uv.x + uv.y - kx, 0.0, 1.0);
    res = dot2(d + (c + b*bestT)*bestT);
  } else {
    float z = sqrt(-p);
    float v = acos(q/(p*z*2.0)) / 3.0;
    float m = cos(v);
    float n = sin(v) * 1.732050808;
    vec3  t = clamp(vec3(m+m, -n-m, n-m)*z - kx, 0.0, 1.0);
    float d0 = dot2(d + (c + b*t.x)*t.x);
    float d1 = dot2(d + (c + b*t.y)*t.y);
    if (d0 < d1) { res = d0; bestT = t.x; }
    else         { res = d1; bestT = t.y; }
  }
  return vec2(sqrt(res), bestT);
}

// The transformation function from original code
float transformation(float t) {
  float inner = sin(t * uModFreq * PI);
  float outer = uBaseFreq + uModAmp * sin(TAU * inner / uModDiv);
  return (1.0 + uSinAmp * sin(outer * t * uThetaScale * PI)) * PI;
}

// Evaluate a point on the polar rose curve with symmetry rotation
vec2 curvePoint(float t, float cosR, float sinR) {
  float theta = transformation(t);
  float r = sin(t * TAU + uPhase * TAU) * uScale * 0.5;
  vec2 c = vec2(r * cos(theta), r * sin(theta));
  return vec2(cosR * c.x - sinR * c.y, sinR * c.x + cosR * c.y);
}

void main() {
  float minDim = min(uResolution.x, uResolution.y);
  vec2 px = (gl_FragCoord.xy - 0.5 * uResolution) / minDim;

  float dMin = 1e9;
  float closestT = 0.0;

  int N   = max(3, uSubdivisions);
  int sym = max(1, uSymmetry);

  // Bounding-box margin covers the glow radius
  float margin = uLineWidth * 3.0 / minDim;

  for (int s = 0; s < 128; ++s) {
    if (s >= sym) break;
    float rotAngle = float(s) * TAU / float(sym);
    float cosR = cos(rotAngle);
    float sinR = sin(rotAngle);

    // Closed-loop Catmull-Rom Bezier: N sample points, indices wrap mod N
    // so the curve forms a seamless loop with no dangling endpoints.
    vec2 Pprev = curvePoint(float(N - 1) / float(N), cosR, sinR);
    vec2 Pcurr = curvePoint(0.0, cosR, sinR);

    for (int i = 0; i < 8192; ++i) {
      if (i >= N) break;

      vec2 Pnext = curvePoint(float((i + 1) % N) / float(N), cosR, sinR);

      // Catmull-Rom midpoint Bezier: mid(prev,curr) → curr → mid(curr,next)
      // Gives C1-continuous joins — no sharp corners anywhere.
      vec2 A = 0.5 * (Pprev + Pcurr);
      vec2 B = Pcurr;
      vec2 C = 0.5 * (Pcurr + Pnext);

      // Bounding-box culling: skip the expensive sdBezier when far away.
      vec2 lo = min(A, min(B, C)) - margin;
      vec2 hi = max(A, max(B, C)) + margin;

      if (px.x >= lo.x && px.x <= hi.x && px.y >= lo.y && px.y <= hi.y) {
        vec2 db = sdBezier(px, A, B, C);  // .x = distance, .y = bezier param [0,1]
        if (db.x < dMin) {
          dMin = db.x;
          // Continuous curve parameter: segment i, interpolated by Bezier parameter
          closestT = (float(i) + db.y) / float(N);
        }
      }

      Pprev = Pcurr;
      Pcurr = Pnext;
    }
  }

  // --- Sharp core + soft bloom glow ---
  float lineHalf = uLineWidth * 0.5 / minDim;

  // Sharp antialiased core
  float core = 1.0 - smoothstep(lineHalf * 0.35, lineHalf, dMin);

  // Soft bloom glow extending beyond the core
  float glowR = lineHalf * 5.0;
  float glow  = exp(-dMin * dMin / (glowR * glowR * 0.18)) * 0.3;

  float alpha = max(core, glow);

  // Hue from curve parameter
  float hue = 0.5 + 0.5 * sin(closestT * TAU * uHueCycles);
  hue = fract(hue + 0.1 * sin(uPhase * TAU));

  // Glow is slightly desaturated; core is vivid
  float sat = mix(0.55, 0.9, core);
  float val = 0.99;

  // HSV → RGB
  vec3 rgb;
  {
    vec3 cv = vec3(hue, sat, val);
    vec3 q  = abs(fract(cv.xxx + vec3(0.0, 2.0/6.0, 4.0/6.0)) * 6.0 - 3.0);
    rgb = cv.z * mix(vec3(1.0), clamp(q - 1.0, 0.0, 1.0), cv.y);
  }

  FragColor = vec4(rgb * alpha, alpha);
}
