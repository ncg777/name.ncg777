#version 330 core
out vec4 fragColor;
// Use built-in fragment coordinates in pixels

uniform vec2 iResolution;   // viewport size (pixels)
uniform float iTime;        // time in seconds

// === PARAMETERS ===
uniform float uLoopDuration;  // seconds per full loop
uniform float uSpeed;         // travel speed multiplier
uniform float uTwist;         // twisting intensity
uniform float uNoiseScale;    // noise scale factor
uniform float uNoiseAmp;      // noise amplitude
uniform float uColorCycle;    // hue shift speed
uniform float uFogDensity;    // fog falloff factor
uniform vec3  uBaseColor;     // base hue of tunnel

// Constants
const float TAU = 6.28318530718; // 2π

// === HASHED VALUE NOISE + FBM ===
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

float fbm(vec2 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 5; i++) {
        sum += amp * noise(p);
        p *= 2.0;
        amp *= 0.5;
    }
    return sum;
}

// === PERIODIC TIME FUNCTION FOR SEAMLESS LOOP ===
float loopTime(float t, float duration) {
    float phase = mod(t, duration) / duration; // 0 → 1
    return phase * TAU; // map to 0 → 2π
}

// === MAIN ===
void main() {
    // --- Normalized coordinates ---
    vec2 uv = (gl_FragCoord.xy - 0.5 * iResolution.xy) / iResolution.y;
    float aspect = iResolution.x / iResolution.y;

    // --- Time that loops perfectly ---
    float t = loopTime(iTime * uSpeed, uLoopDuration); // 0..2π
    float phase = t / TAU; // 0..1

    // --- Polar coordinates ---
    float r = length(uv);
    float a = atan(uv.y, uv.x);

    // --- Add twisting down the tunnel ---
    a += uTwist * r;

    // --- Brownian noise distortion (time-periodic) ---
    // Use periodic angular coordinates to avoid seam at -π/π
    vec2 dir = vec2(cos(a), sin(a));
    // Periodic time offset on a circle so values at t=0 and t=2π match
    vec2 tOff = vec2(cos(t), sin(t)) * (0.6 * uNoiseScale);
    vec2 np = dir * (0.75 * uNoiseScale) + tOff + r * (2.0 * uNoiseScale);
    float n = fbm(np);
    r += uNoiseAmp * n;
    a += uNoiseAmp * 0.5 * n;

    // --- Tunnel pattern (looping stripes) ---
    // Ensure stripe phase depends on a 0..1 quantity that loops seamlessly
    float stripePhase = fract(phase + r * 0.5);
    float stripe = smoothstep(0.3, 0.5, sin(a * 8.0 + stripePhase * TAU));

    // --- Dynamic color based on angle, noise, and time ---
    vec3 baseHue = uBaseColor;
    vec3 dynamicHue = 0.5 + 0.5 * cos(vec3(0.0, 0.6, 1.2) + a * 2.0 + n * 2.0 + uColorCycle * t);
    vec3 col = mix(baseHue, dynamicHue, 0.7);

    // --- Stripes as white bands instead of dark bands ---
    float stripeMask = stripe;
    col = mix(col, vec3(1.0), 0.6 * stripeMask);

    // --- Screen-space refraction via FBM gradient ---
    float fogBase = exp(-r * uFogDensity);
    float glowBase = pow(fogBase, 2.0);
    float e = 0.003 * max(0.5, uNoiseScale);
    vec2 grad;
    grad.x = fbm(np + vec2(e, 0.0)) - fbm(np - vec2(e, 0.0));
    grad.y = fbm(np + vec2(0.0, e)) - fbm(np - vec2(0.0, e));
    vec2 normal2D = normalize(grad + vec2(1e-6));
    float refractStrength = 0.03; // tweakable
    vec2 uvR = uv + normal2D * refractStrength * (0.3 + 0.7 * glowBase);

    // Re-evaluate pattern at refracted coordinates
    float rR = length(uvR);
    float aR = atan(uvR.y, uvR.x);
    aR += uTwist * rR;
    vec2 dirR = vec2(cos(aR), sin(aR));
    vec2 npR = dirR * (0.75 * uNoiseScale) + tOff + rR * (2.0 * uNoiseScale);
    float nR = fbm(npR);
    float stripePhaseR = fract(phase + rR * 0.5);
    float stripeR = smoothstep(0.3, 0.5, sin(aR * 8.0 + stripePhaseR * TAU));
    vec3 dynamicHueR = 0.5 + 0.5 * cos(vec3(0.0, 0.6, 1.2) + aR * 2.0 + nR * 2.0 + uColorCycle * t);
    vec3 colR = mix(baseHue, dynamicHueR, 0.7);
    colR = mix(colR, vec3(1.0), 0.6 * stripeR);

    // Blend refracted sample
    col = mix(col, colR, 0.6);

    // --- Enhanced fog/glow and contrast ---
    float fog = fogBase;
    float glow = glowBase;   // tighter, brighter center glow

    // Increase near contrast (bright near, darker far)
    col *= mix(0.6, 1.6, fog);
    // Add emissive glow that blooms towards the center
    col += glow * 0.35 * (0.6 * baseHue + 0.4 * dynamicHue);

    col = clamp(col, 0.0, 1.0);
    fragColor = vec4(col, 1.0);
}
