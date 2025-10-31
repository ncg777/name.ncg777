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
    return phase * 6.28318530718; // map to 0 → 2π
}

// === MAIN ===
void main() {
    // --- Normalized coordinates ---
    vec2 uv = (gl_FragCoord.xy - 0.5 * iResolution.xy) / iResolution.y;
    float aspect = iResolution.x / iResolution.y;

    // --- Time that loops perfectly ---
    float t = loopTime(iTime * uSpeed, uLoopDuration);

    // --- Polar coordinates ---
    float r = length(uv);
    float a = atan(uv.y, uv.x);

    // --- Add twisting down the tunnel ---
    a += uTwist * r;

    // --- Brownian noise distortion ---
    // Use periodic angular coordinates to avoid seam at -π/π
    vec2 dir = vec2(cos(a), sin(a));
    vec2 np = dir * (0.75 * uNoiseScale) + vec2(0.0, t * 0.3) + r * (2.0 * uNoiseScale);
    float n = fbm(np);
    r += uNoiseAmp * n;
    a += uNoiseAmp * 0.5 * n;

    // --- Tunnel pattern (looping stripes) ---
    float z = mod(t + r * 3.0, 3.0);
    float stripe = smoothstep(0.3, 0.5, sin(a * 8.0 + z * 6.28318));

    // --- Dynamic color based on angle, noise, and time ---
    vec3 baseHue = uBaseColor;
    vec3 dynamicHue = 0.5 + 0.5 * cos(vec3(0.0, 0.6, 1.2) + a * 2.0 + n * 2.0 + uColorCycle * t);
    vec3 col = mix(baseHue, dynamicHue, 0.7);

    // --- Apply stripe pattern and enhanced fog/glow ---
    col *= mix(1.6, 0.5, stripe); // slightly stronger stripe contrast

    float fog = exp(-r * uFogDensity);
    float glow = pow(fog, 2.0);   // tighter, brighter center glow

    // Increase near contrast (bright near, darker far)
    col *= mix(0.6, 1.6, fog);
    // Add emissive glow that blooms towards the center
    col += glow * 0.35 * (0.6 * baseHue + 0.4 * dynamicHue);

    col = clamp(col, 0.0, 1.0);
    fragColor = vec4(col, 1.0);
}
