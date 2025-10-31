#version 330 core
out vec4 FragColor;

// Koch Snowflake Distance Field Shader
// This implements the Koch snowflake fractal using a distance field approach.
// The Koch snowflake is traditionally defined using an L-system:
//   Axiom: F--F--F
//   Rule: F -> F+F--F+F
// However, instead of using string rewriting, this shader computes the
// distance field directly through recursive geometric subdivision:
// Each line segment is divided into 4 segments forming a triangular bump.
// This approach is more efficient for real-time rendering.

uniform vec2  uResolution;     // viewport size (pixels)
uniform float uTime;           // animation time in seconds
uniform int   uIterations;     // Koch snowflake iterations (e.g., 3-6)
uniform float uScale;          // scale factor for the snowflake
uniform float uRotation;       // rotation speed multiplier
uniform float uGlowIntensity;  // glow intensity
uniform vec3  uColorPrimary;   // primary color
uniform vec3  uColorSecondary; // secondary color

// Distance from point to line segment
float sdSegment(vec2 p, vec2 a, vec2 b) {
    vec2 pa = p - a;
    vec2 ba = b - a;
    float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return length(pa - ba * h);
}

// Rotate point around origin
vec2 rotate(vec2 p, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
}

// Koch curve subdivision for a single segment
// Returns minimum distance to the Koch curve at given iteration level
float kochSegmentDistance(vec2 p, vec2 a, vec2 b, int iterations) {
    if (iterations <= 0) {
        return sdSegment(p, a, b);
    }
    
    vec2 ab = b - a;
    float len = length(ab);
    vec2 dir = ab / len;
    vec2 perp = vec2(-dir.y, dir.x);
    
    // Divide segment into thirds
    vec2 p1 = a + ab / 3.0;
    vec2 p3 = a + ab * 2.0 / 3.0;
    
    // Create the peak point (equilateral triangle)
    vec2 p2 = (p1 + p3) / 2.0 + perp * len * sqrt(3.0) / 6.0;
    
    // Recursively compute distance for all four subsegments
    float d1 = kochSegmentDistance(p, a, p1, iterations - 1);
    float d2 = kochSegmentDistance(p, p1, p2, iterations - 1);
    float d3 = kochSegmentDistance(p, p2, p3, iterations - 1);
    float d4 = kochSegmentDistance(p, p3, b, iterations - 1);
    
    return min(min(d1, d2), min(d3, d4));
}

// Koch snowflake distance field (equilateral triangle base)
float kochSnowflakeDistance(vec2 p, float size, int iterations) {
    // Define equilateral triangle vertices
    float h = size * sqrt(3.0) / 2.0;
    vec2 v1 = vec2(0.0, h * 2.0 / 3.0);
    vec2 v2 = vec2(-size / 2.0, -h / 3.0);
    vec2 v3 = vec2(size / 2.0, -h / 3.0);
    
    // Compute distance to each of the three sides
    float d1 = kochSegmentDistance(p, v1, v2, iterations);
    float d2 = kochSegmentDistance(p, v2, v3, iterations);
    float d3 = kochSegmentDistance(p, v3, v1, iterations);
    
    return min(min(d1, d2), d3);
}

void main() {
    // Normalized coordinates centered at origin
    vec2 uv = (gl_FragCoord.xy - 0.5 * uResolution.xy) / min(uResolution.x, uResolution.y);
    
    // Apply rotation based on time
    float angle = uTime * uRotation;
    uv = rotate(uv, angle);
    
    // Compute distance to Koch snowflake
    float dist = kochSnowflakeDistance(uv, uScale, uIterations);
    
    // Create line visualization with glow
    const float lineWidth = 0.002;
    const float lineOuterMult = 1.5;
    const float lineInnerMult = 0.5;
    const float distanceScale = 50.0;
    const float timeScale = 2.0;
    const float glowMix = 0.4;
    const float edgeGlowMult = 0.3;
    
    // Core line
    float line = smoothstep(lineWidth * lineOuterMult, lineWidth * lineInnerMult, dist);
    
    // Glow effect
    float glow = exp(-dist * distanceScale * uGlowIntensity);
    
    // Color mixing based on distance and time
    float colorMix = sin(dist * distanceScale - uTime * timeScale) * 0.5 + 0.5;
    vec3 color = mix(uColorPrimary, uColorSecondary, colorMix);
    
    // Combine effects
    vec3 finalColor = color * (line + glow * glowMix);
    
    // Add extra glow at the edges
    const vec3 edgeGlowColor = vec3(0.2, 0.3, 0.5);
    finalColor += edgeGlowColor * glow * uGlowIntensity * edgeGlowMult;
    
    FragColor = vec4(finalColor, 1.0);
}
