#version 330 core
out vec4 FragColor;

uniform vec2  uResolution;     // pixels
uniform float uPhase;          // [0,1] loop time
uniform int   uSymmetry;       // rotational symmetry copies (n)
uniform int   uSubdivisions;   // how many line segments per copy
uniform float uScale;          // overall radius scale (0.0 - 1.0)

// Transformation params extracted from the original f20250708 function:
//   transformation = (1 + sinAmp*sin((baseFreq + modAmp*sin(2*PI*sin(theta*modFreq*PI)/modDiv))*theta*thetaScale*PI))*PI
uniform float uSinAmp;         // amplitude of the outer sine (default 0.25)
uniform float uBaseFreq;       // base frequency term (default 2.0)
uniform float uModAmp;         // amplitude modulator (default 1.0)
uniform float uModFreq;        // inner modulation frequency (default 1024.0)
uniform float uModDiv;         // inner modulation divisor (default 4.0)
uniform float uThetaScale;     // scale of theta (default 0.95)

uniform float uLineWidth;      // antialiased line width in pixels
uniform float uHueCycles;      // hue cycles per segment loop

uniform float uSeed;           // random seed for variation

const float PI  = 3.14159265358979323846;
const float TAU = 6.28318530717958647692;

// Signed distance to a line segment
float sdLine(vec2 p, vec2 a, vec2 b) {
  vec2 pa = p - a, ba = b - a;
  float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
  return length(pa - ba * h);
}

// The transformation function from original code
float transformation(float t) {
  float inner = sin(t * uModFreq * PI);
  float outer = uBaseFreq + uModAmp * sin(TAU * inner / uModDiv);
  return (1.0 + uSinAmp * sin(outer * t * uThetaScale * PI)) * PI;
}

void main() {
  float minDim = min(uResolution.x, uResolution.y);
  vec2 p = (gl_FragCoord.xy - 0.5 * uResolution) / minDim; // [-0.5, 0.5]

  // Compute closest distance to any drawn segment across all symmetry copies
  float dMin = 1e9;
  float closestT = 0.0; // for hue

  int subdiv = max(1, uSubdivisions);
  int sym = max(1, uSymmetry);

  for (int s = 0; s < 128; ++s) {
    if (s >= sym) break;
    float rotAngle = float(s) * TAU / float(sym);
    float cosR = cos(rotAngle);
    float sinR = sin(rotAngle);

    for (int i = 0; i < 8192; ++i) {
      if (i >= subdiv - 1) break;

      float t1 = float(i) / float(subdiv);
      float t2 = float(i + 1) / float(subdiv);

      float theta1 = transformation(t1);
      float theta2 = transformation(t2);

      // radius oscillates with phase (exactly as original)
      float r1 = sin(t1 * TAU + uPhase * TAU) * uScale * 0.5;
      float r2 = sin(t2 * TAU + uPhase * TAU) * uScale * 0.5;

      // polar -> cartesian
      vec2 c1 = vec2(r1 * cos(theta1), r1 * sin(theta1));
      vec2 c2 = vec2(r2 * cos(theta2), r2 * sin(theta2));

      // apply symmetry rotation
      c1 = vec2(cosR * c1.x - sinR * c1.y, sinR * c1.x + cosR * c1.y);
      c2 = vec2(cosR * c2.x - sinR * c2.y, sinR * c2.x + cosR * c2.y);

      float d = sdLine(p, c1, c2);
      if (d < dMin) {
        dMin = d;
        closestT = t1;
      }
    }
  }

  // Antialiased line mask
  float lineHalf = uLineWidth * 0.5 / minDim;
  float alpha = 1.0 - smoothstep(lineHalf * 0.5, lineHalf, dMin);

  // Hue from closestT and phase (matching original HSB logic)
  float hue = 0.5 + 0.5 * sin(closestT * TAU * uHueCycles);
  hue = fract(hue + 0.1 * sin(uPhase * TAU));

  float sat = 0.8 + 0.2 * cos(uPhase * TAU);
  float val = 0.99;

  // HSV to RGB
  vec3 rgb;
  {
    vec3 c = vec3(hue, sat, val);
    vec3 q = abs(fract(c.xxx + vec3(0., 2./6., 4./6.)) * 6. - 3.);
    rgb = c.z * mix(vec3(1.), clamp(q - 1., 0., 1.), c.y);
  }

  FragColor = vec4(rgb * alpha, alpha);
}
