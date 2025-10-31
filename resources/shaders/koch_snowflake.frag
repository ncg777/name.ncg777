#version 330 core
out vec4 FragColor;

// Koch Snowflake Distance Field Shader (GLSL 330, no recursion)
//
// Previous version used recursive subdivision (not allowed in GLSL).
// This version replaces recursion with an iterative, space-folding
// approach that maps the point into a canonical segment using the
// triangle's 6-fold symmetry. The base distance is evaluated against a
// single segment and scaled across iterations.

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

// Distance to Koch curve along a single segment via inverse-IFS folding.
// p is in world coordinates; a,b are world segment endpoints.
float kochSegmentDistanceIter(vec2 p, vec2 a, vec2 b, int iterations) {
    // Build local frame for segment (length-normalized)
    vec2 ab = b - a;
    float len = max(length(ab), 1e-6);
    vec2 dir = ab / len;
    vec2 nrm = vec2(-dir.y, dir.x);
    // world -> local (x along segment in [0,1], y perpendicular)
    vec2 pl = vec2(dot(p - a, dir) / len, dot(p - a, nrm) / len);

    const float c60 = 0.5;
    const float s60 = 0.8660254037844386;
    // inverse rotations for the two middle rotated children
    mat2 invRotPlus  = mat2( c60,  s60, -s60,  c60); // -60 degrees
    mat2 invRotMinus = mat2( c60, -s60,  s60,  c60); // +60 degrees

    const int MAX_ITERS = 8;
    int it = min(iterations, MAX_ITERS);
    float scaleAccum = 1.0;

    for (int i = 0; i < MAX_ITERS; ++i) {
        if (i >= it) break;
        // scale up by 3 to choose the child region
        pl *= 3.0;
        float region = floor(pl.x); // 0,1,2 for [0,1),[1,2),[2,3)

        if (region == 1.0) {
            // Middle third: undo rotation from either of the two slanted segments
            vec2 c = vec2(1.5, 0.0);
            vec2 pr = pl - c;
            vec2 pr1 = invRotPlus * pr;   // child that was rotated +60 (undo -60)
            vec2 pr2 = invRotMinus * pr;  // child that was rotated -60 (undo +60)
            vec2 p1 = pr1 + c;
            vec2 p2 = pr2 + c;
            // choose the mapping that brings us closer to the parent axis (y=0)
            pl = (abs(p1.y) < abs(p2.y)) ? p1 : p2;
        }

        // fold x back to parent domain [0,1)
        pl.x -= region;
        scaleAccum *= (1.0 / 3.0);
    }

    // Distance to base segment [0,1] in local space, rescaled to world
    float dLocal = sdSegment(pl, vec2(0.0, 0.0), vec2(1.0, 0.0));
    return dLocal * len * scaleAccum;
}

// Koch snowflake distance: min over the three sides of the base triangle
float kochSnowflakeDistance(vec2 p, float size, int iterations) {
    float h = size * sqrt(3.0) / 2.0;
    vec2 v1 = vec2(0.0, h * 2.0 / 3.0);
    vec2 v2 = vec2(-size / 2.0, -h / 3.0);
    vec2 v3 = vec2( size / 2.0, -h / 3.0);

    float d1 = kochSegmentDistanceIter(p, v1, v2, iterations);
    float d2 = kochSegmentDistanceIter(p, v2, v3, iterations);
    float d3 = kochSegmentDistanceIter(p, v3, v1, iterations);
    return min(min(d1, d2), d3);
}

// Simple equilateral triangle perimeter distance (fallback/aid for visibility)
float trianglePerimeterDistance(vec2 p, float size) {
    float h = size * sqrt(3.0) / 2.0;
    vec2 v1 = vec2(0.0, h * 2.0 / 3.0);
    vec2 v2 = vec2(-size / 2.0, -h / 3.0);
    vec2 v3 = vec2( size / 2.0, -h / 3.0);
    float d1 = sdSegment(p, v1, v2);
    float d2 = sdSegment(p, v2, v3);
    float d3 = sdSegment(p, v3, v1);
    return min(min(d1, d2), d3);
}

void main() {
    // Normalized coordinates centered at origin
    vec2 uv = (gl_FragCoord.xy - 0.5 * uResolution.xy) / min(uResolution.x, uResolution.y);
    
    // Apply rotation based on time
    float angle = uTime * uRotation;
    uv = rotate(uv, angle);
    
    // Compute distance to Koch snowflake (iterative inverse-IFS)
    float distKoch = kochSnowflakeDistance(uv, uScale, uIterations);
    // Keep a faint triangle fallback to stabilize visuals at low iteration
    float distTri  = trianglePerimeterDistance(uv, uScale);
    float dist = min(distKoch, distTri * 0.75);
    
    // Create line visualization with glow
    const float lineWidth = 0.004; // moderate edge width
    const float lineOuterMult = 1.5;
    const float lineInnerMult = 0.5;
    const float distanceScale = 15.0; // lower falloff so glow is visible
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
