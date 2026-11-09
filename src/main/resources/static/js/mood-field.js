/* Mood Field – vanilla JS port (no React needed).
   Usage: <div id="mood-field" data-mood="intense"></div>
   Optional data attributes: data-background, data-intense-color, data-calm-color,
   data-mood (intense|calm), data-speed, data-density, data-damping,
   data-warp, data-turbulence, data-contrast, data-reach, data-hover, data-swirl */
(function () {
    "use strict";

    var VERT = "attribute vec2 aPos; void main(){ gl_Position = vec4(aPos, 0.0, 1.0); }";

    var FRAG = [
        "precision highp float;",
        "uniform vec2 iResolution; uniform vec2 iMouse; uniform float iTime; uniform float uEmotion;",
        "uniform vec3 uIntense; uniform vec3 uCalm; uniform vec3 uBg;",
        "uniform float uScale; uniform float uWarp; uniform float uRidge; uniform float uContrast;",
        "uniform float uReach; uniform float uHover; uniform float uSwirl; uniform float uPointer;",
        "const mat2 R2 = mat2(0.80, 0.60, -0.60, 0.80);",
        "float hash21(vec2 p){ return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123); }",
        "float vnoise(vec2 p){",
        "  vec2 i = floor(p); vec2 f = fract(p);",
        "  vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);",
        "  float a = hash21(i); float b = hash21(i + vec2(1.0, 0.0));",
        "  float c = hash21(i + vec2(0.0, 1.0)); float d = hash21(i + vec2(1.0, 1.0));",
        "  return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);",
        "}",
        "float fbm(vec2 p){",
        "  float sum = 0.0; float amp = 0.5; float norm = 0.0;",
        "  for (int i = 0; i < 4; i++){",
        "    float v = vnoise(p);",
        "    v = mix(v, abs(v * 2.0 - 1.0), uRidge);",
        "    sum += amp * v; norm += amp;",
        "    p = R2 * p * 2.03 + 7.13; amp *= 0.5;",
        "  }",
        "  return sum / max(norm, 1e-4);",
        "}",
        "void main(){",
        "  float h = max(iResolution.y, 1.0);",
        "  vec2 sp = (gl_FragCoord.xy - 0.5 * iResolution) / h;",
        "  vec2 mp = (iMouse - 0.5 * iResolution) / h;",
        "  float reach = max(uReach / h, 1e-4);",
        "  vec2 d = sp - mp;",
        "  float r = length(d);",
        "  float infl = uPointer * uHover * exp(-(r * r) / (reach * reach));",
        "  float ang = infl * uSwirl;",
        "  float cs = cos(ang); float sn = sin(ang);",
        "  sp = mp + mat2(cs, -sn, sn, cs) * d;",
        "  sp += d * (infl * 0.30) / max(r, 1e-3);",
        "  float tt = iTime * mix(1.55, 0.55, uEmotion);",
        "  vec2 p = sp * uScale;",
        "  vec2 q = vec2(fbm(p + vec2(0.0, tt * 0.16)), fbm(p + vec2(5.2, 1.3) - vec2(tt * 0.12, 0.0)));",
        "  float f = fbm(p + uWarp * q + vec2(tt * 0.05, -tt * 0.04));",
        "  float v = pow(clamp(f, 0.0, 1.0), uContrast);",
        "  v = clamp(v + infl * 0.22, 0.0, 1.0);",
        "  vec3 mood = mix(uIntense, uCalm, uEmotion);",
        "  vec3 col = mix(uBg, mood, smoothstep(0.12, 0.92, v));",
        "  col += mood * pow(v, 5.0) * 0.9;",
        "  gl_FragColor = vec4(clamp(col, 0.0, 1.0), 1.0);",
        "}"
    ].join("\n");

    // Colour parsing via a 2D canvas so any CSS colour works
    var probe = document.createElement("canvas").getContext("2d");
    function toRgb(input, fallback) {
        if (!input || !probe) return fallback;
        probe.fillStyle = "#000000";
        probe.fillStyle = input;
        var c = String(probe.fillStyle);
        if (c === "#000000") {
            probe.fillStyle = "#ffffff";
            probe.fillStyle = input;
            c = String(probe.fillStyle);
            if (c === "#ffffff") return fallback;
        }
        if (c[0] === "#") {
            var n = parseInt(c.slice(1, 7), 16);
            return [((n >> 16) & 255) / 255, ((n >> 8) & 255) / 255, (n & 255) / 255];
        }
        var m = c.match(/-?[\d.]+/g);
        if (m && m.length >= 3) {
            return [m[0] / 255, m[1] / 255, m[2] / 255].map(function (x) { return Math.min(1, Math.max(0, x)); });
        }
        return fallback;
    }

    function num(el, key, def) {
        var v = parseFloat(el.dataset[key]);
        return isNaN(v) ? def : v;
    }

    function init(host) {
        var ds = host.dataset;
        var background = ds.background || "#05060a";
        var cfg = {
            bg: toRgb(background, [0.02, 0.024, 0.04]),
            intense: toRgb(ds.intenseColor || "#FF00B3", [1, 0, 0.7]),
            calm: toRgb(ds.calmColor || "#2f6bff", [0.18, 0.42, 1]),
            target: ds.mood === "calm" ? 1 : 0,
            speed: Math.max(0, num(host, "speed", 50)) / 50,
            scale: (2.2 * Math.max(1, num(host, "density", 100))) / 100,
            warp: (1.2 * Math.max(0, num(host, "warp", 100))) / 100,
            turbulence: Math.max(0, num(host, "turbulence", 100)) / 100,
            contrast: 0.5 + (2.5 * Math.max(0, num(host, "contrast", 100))) / 200,
            reach: Math.max(1, num(host, "reach", 260)),
            hover: Math.max(0, num(host, "hover", 100)) / 100,
            swirl: (Math.max(-90, Math.min(90, num(host, "swirl", 90))) * Math.PI) / 180
        };
        var damping = 0.0006 * Math.max(1, Math.min(100, num(host, "damping", 50)));
        var reduced = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (reduced) cfg.speed = 0;

        host.style.background = background;

        var canvas = document.createElement("canvas");
        canvas.style.cssText = "position:absolute;inset:0;width:100%;height:100%;display:block;";
        host.appendChild(canvas);

        var gl = canvas.getContext("webgl", { alpha: false, antialias: false, depth: false, stencil: false });
        if (!gl) return; // falls back to the solid background colour

        function compile(type, src) {
            var sh = gl.createShader(type);
            gl.shaderSource(sh, src);
            gl.compileShader(sh);
            if (!gl.getShaderParameter(sh, gl.COMPILE_STATUS)) console.error(gl.getShaderInfoLog(sh));
            return sh;
        }
        var prog = gl.createProgram();
        gl.attachShader(prog, compile(gl.VERTEX_SHADER, VERT));
        gl.attachShader(prog, compile(gl.FRAGMENT_SHADER, FRAG));
        gl.linkProgram(prog);
        gl.useProgram(prog);

        gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, 3, -1, -1, 3]), gl.STATIC_DRAW);
        var aPos = gl.getAttribLocation(prog, "aPos");
        gl.enableVertexAttribArray(aPos);
        gl.vertexAttribPointer(aPos, 2, gl.FLOAT, false, 0, 0);

        var names = ["iResolution", "iMouse", "iTime", "uEmotion", "uIntense", "uCalm", "uBg", "uScale",
            "uWarp", "uRidge", "uContrast", "uReach", "uHover", "uSwirl", "uPointer"];
        var u = {};
        names.forEach(function (n) { u[n] = gl.getUniformLocation(prog, n); });

        var bw = 0, bh = 0;
        function resize() {
            var dpr = Math.min(window.devicePixelRatio || 1, 1.5);
            var w = Math.max(1, Math.round(host.clientWidth * dpr));
            var h = Math.max(1, Math.round(host.clientHeight * dpr));
            if (w === bw && h === bh) return;
            bw = w; bh = h;
            canvas.width = w; canvas.height = h;
            gl.viewport(0, 0, w, h);
        }
        resize();
        if (window.ResizeObserver) new ResizeObserver(resize).observe(host);

        // Listen on the hero section so the cursor still reaches the effect through the content layer
        var zone = host.parentElement || host;
        var mouse = { x: 0.5, y: 0.5, tx: 0.5, ty: 0.5, presence: 0, want: 0 };
        zone.addEventListener("pointermove", function (e) {
            var r = host.getBoundingClientRect();
            mouse.tx = (e.clientX - r.left) / Math.max(r.width, 1);
            mouse.ty = (e.clientY - r.top) / Math.max(r.height, 1);
            mouse.want = 1;
        });
        zone.addEventListener("pointerleave", function () { mouse.want = 0; });
        zone.addEventListener("pointercancel", function () { mouse.want = 0; });

        var emotion = cfg.target, clock = 0, last = -1;
        function frame(now) {
            requestAnimationFrame(frame);
            var dt = last < 0 ? 0 : Math.min(0.1, Math.max(0, (now - last) / 1000));
            last = now;
            resize();

            emotion += (cfg.target - emotion) * (1 - Math.pow(1 - damping, dt * 60));
            clock += dt * cfg.speed;

            var mk = 1 - Math.pow(1 - 0.18, dt * 60);
            mouse.x += (mouse.tx - mouse.x) * mk;
            mouse.y += (mouse.ty - mouse.y) * mk;
            mouse.presence += (mouse.want - mouse.presence) * mk;

            gl.uniform2f(u.iResolution, bw, bh);
            gl.uniform2f(u.iMouse, mouse.x * bw, (1 - mouse.y) * bh);
            gl.uniform1f(u.iTime, clock);
            gl.uniform1f(u.uEmotion, emotion);
            gl.uniform3fv(u.uIntense, cfg.intense);
            gl.uniform3fv(u.uCalm, cfg.calm);
            gl.uniform3fv(u.uBg, cfg.bg);
            gl.uniform1f(u.uScale, cfg.scale);
            gl.uniform1f(u.uWarp, cfg.warp * (1 - 0.3 * emotion));
            gl.uniform1f(u.uRidge, Math.min(1, cfg.turbulence * (0.9 - 0.6 * emotion)));
            gl.uniform1f(u.uContrast, cfg.contrast);
            gl.uniform1f(u.uReach, cfg.reach * (bh / Math.max(host.clientHeight, 1)));
            gl.uniform1f(u.uHover, cfg.hover);
            gl.uniform1f(u.uSwirl, cfg.swirl);
            gl.uniform1f(u.uPointer, mouse.presence);
            gl.drawArrays(gl.TRIANGLES, 0, 3);
        }
        requestAnimationFrame(frame);
    }

    function boot() {
        var hosts = document.querySelectorAll("[data-mood-field], #mood-field");
        for (var i = 0; i < hosts.length; i++) init(hosts[i]);
    }
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", boot);
    else boot();
})();