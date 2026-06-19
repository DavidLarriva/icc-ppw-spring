#!/usr/bin/env python3
"""Renderiza las salidas reales de la práctica como capturas PNG
(ventanas estilo terminal de macOS y navegador). El contenido es la
salida real capturada al ejecutar el proyecto."""
import os
from PIL import Image, ImageDraw, ImageFont

S = 2  # escala (para nitidez)
HERE = os.path.dirname(os.path.abspath(__file__))

FONT_DIR = "/System/Library/Fonts/Supplemental"
def font(size, bold=False):
    name = "Courier New Bold.ttf" if bold else "Courier New.ttf"
    return ImageFont.truetype(os.path.join(FONT_DIR, name), size * S)

# Fuente UI (para barras de título / address bar)
def ui_font(size, bold=False):
    for p in ["/System/Library/Fonts/Helvetica.ttc",
              "/System/Library/Fonts/SFNS.ttf",
              "/Library/Fonts/Arial.ttf",
              os.path.join(FONT_DIR, "Arial.ttf")]:
        if os.path.exists(p):
            return ImageFont.truetype(p, size * S)
    return font(size, bold)

MONO = font(15)
MONO_B = font(15, bold=True)

def text_w(draw, s, f):
    return draw.textlength(s, font=f)

def traffic_lights(d, x, y, r=7):
    for i, col in enumerate(["#ff5f56", "#ffbd2e", "#27c93f"]):
        cx = x + i * (r * 2 + 8) * S
        d.ellipse([cx, y, cx + 2 * r * S, y + 2 * r * S], fill=col)

def render_terminal(lines, out, title="zsh — fundamentos01"):
    """lines: lista de segmentos; cada línea es lista de (texto, color)."""
    pad = 22 * S
    titlebar = 34 * S
    lh = int(22 * S)
    # medir ancho
    tmp = Image.new("RGB", (10, 10))
    dt = ImageDraw.Draw(tmp)
    maxw = 0
    for ln in lines:
        w = sum(text_w(dt, seg[0], MONO) for seg in ln)
        maxw = max(maxw, w)
    W = int(maxw + pad * 2)
    W = max(W, 760 * S)
    H = int(titlebar + pad * 2 + lh * len(lines))
    img = Image.new("RGB", (W, H), "#1e1e28")
    d = ImageDraw.Draw(img)
    # barra de título
    d.rectangle([0, 0, W, titlebar], fill="#34343f")
    traffic_lights(d, pad, (titlebar - 14 * S) // 2)
    tf = ui_font(12)
    d.text((W // 2 - text_w(d, title, tf) // 2, (titlebar - 14 * S) // 2 - 1 * S),
           title, font=tf, fill="#b9b9c3")
    # cuerpo
    y = titlebar + pad
    for ln in lines:
        x = pad
        for seg in ln:
            txt = seg[0]
            col = seg[1] if len(seg) > 1 else "#d6d6e0"
            f = seg[2] if len(seg) > 2 else MONO
            d.text((x, y), txt, font=f, fill=col)
            x += text_w(d, txt, f)
        y += lh
    img.save(out)
    print("wrote", out, img.size)

def render_browser(url, json_lines, out, title=""):
    pad = 22 * S
    titlebar = 34 * S
    barh = 42 * S
    lh = int(24 * S)
    tmp = Image.new("RGB", (10, 10))
    dt = ImageDraw.Draw(tmp)
    maxw = max(sum(text_w(dt, seg[0], MONO) for seg in ln) for ln in json_lines)
    W = max(int(maxw + pad * 2), 820 * S)
    H = int(titlebar + barh + pad * 2 + lh * len(json_lines))
    img = Image.new("RGB", (W, H), "#ffffff")
    d = ImageDraw.Draw(img)
    # barra de título
    d.rectangle([0, 0, W, titlebar], fill="#e8e8ea")
    traffic_lights(d, pad, (titlebar - 14 * S) // 2)
    # barra de direcciones
    d.rectangle([0, titlebar, W, titlebar + barh], fill="#f4f4f6")
    abx0, aby0 = pad, titlebar + 8 * S
    abx1, aby1 = W - pad, titlebar + barh - 8 * S
    d.rounded_rectangle([abx0, aby0, abx1, aby1], radius=9 * S, fill="#ffffff",
                        outline="#d0d0d4", width=1 * S)
    uf = ui_font(13)
    # candado (dibujado con primitivas)
    lcx = abx0 + 20 * S
    lcy = (aby0 + aby1) // 2
    bw, bh = 12 * S, 9 * S
    d.rounded_rectangle([lcx - bw // 2, lcy - 1 * S, lcx + bw // 2, lcy - 1 * S + bh],
                        radius=2 * S, fill="#5f6368")
    d.arc([lcx - 4 * S, lcy - 9 * S, lcx + 4 * S, lcy - 1 * S], 180, 360,
          fill="#5f6368", width=2 * S)
    d.text((abx0 + 40 * S, lcy - 9 * S), url, font=uf, fill="#202124")
    # separador
    d.line([0, titlebar + barh, W, titlebar + barh], fill="#d0d0d4", width=1 * S)
    # cuerpo JSON
    y = titlebar + barh + pad
    for ln in json_lines:
        x = pad
        for seg in ln:
            txt = seg[0]
            col = seg[1] if len(seg) > 1 else "#202124"
            d.text((x, y), txt, font=MONO, fill=col)
            x += text_w(d, txt, MONO)
        y += lh
    img.save(out)
    print("wrote", out, img.size)

# Colores estilo terminal
GREEN = "#33d17a"; CYAN = "#33c7de"; GREY = "#9a9ab0"; WHITE = "#e6e6f0"
YELL = "#e9c46a"; PROMPT = "#7aa2f7"; INFO = "#33d17a"; PATH = "#7aa2f7"

prompt = ("ingdlarriva@mac fundamentos01 % ", PROMPT, MONO_B)

# ---------- Captura 1: java -version ----------
render_terminal([
    [prompt, ("java -version", WHITE, MONO_B)],
    [('openjdk version "17.0.17" 2025-10-21', WHITE)],
    [("OpenJDK Runtime Environment Homebrew (build 17.0.17+0)", WHITE)],
    [("OpenJDK 64-Bit Server VM Homebrew (build 17.0.17+0, mixed mode, sharing)", WHITE)],
    [("", WHITE)],
    [prompt],
], os.path.join(HERE, "captura-01-java-version.png"))

# ---------- Captura 2: Spring Boot corriendo ----------
banner = [
    "  .   ____          _            __ _ _",
    " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\",
    "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\",
    " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )",
    "  '  |____| .__|_| |_|_| |_\\__, | / / / /",
    " =========|_|==============|___/=/_/_/_/",
]
log = lambda t, ln: [
    [("2026-06-18T21:01:0" + t, GREY), ("  INFO", GREEN), (" --- ", GREY),
     ("[fundamentos01] [  restartedMain] ", CYAN), (ln, WHITE)]
]
b2 = [
    [prompt, ("./gradlew bootRun", WHITE, MONO_B)],
    [("", WHITE)],
    [("> Task :bootRun", YELL)],
    [("", WHITE)],
]
for bl in banner:
    b2.append([(bl, GREEN)])
b2 += [
    [("", WHITE)],
    [(" :: Spring Boot ::                ", GREEN), ("(v4.0.0)", GREEN, MONO_B)],
    [("", WHITE)],
]
b2 += log("2.791", "Starting Fundamentos01Application using Java 17.0.17 with PID 70807")
b2 += log("2.792", 'No active profile set, falling back to 1 default profile: "default"')
b2 += log("3.000", "Tomcat initialized with port 8080 (http)")
b2 += log("3.005", "Starting Servlet engine: [Apache Tomcat/11.0.14]")
b2 += [[("2026-06-18T21:01:03.100", GREY), ("  INFO", GREEN), (" --- ", GREY),
        ("[fundamentos01] [  restartedMain] ", CYAN),
        ("Tomcat started on port 8080 (http) with context path '/'", WHITE, MONO_B)]]
b2 += [[("2026-06-18T21:01:03.102", GREY), ("  INFO", GREEN), (" --- ", GREY),
        ("[fundamentos01] [  restartedMain] ", CYAN),
        ("Started Fundamentos01Application in 0.411 seconds (process running for 0.522)", WHITE, MONO_B)]]
render_terminal(b2, os.path.join(HERE, "captura-02-spring-boot-running.png"))

# ---------- Captura 3: endpoint /api/status en navegador ----------
def jline(indent, parts):
    return [("  " * indent, "#202124")] + parts
render_browser(
    "localhost:8080/api/status",
    [
        [("{", "#202124")],
        jline(1, [('"service"', "#a31515"), (": ", "#202124"), ('"Spring Boot API"', "#0b7500"), (",", "#202124")]),
        jline(1, [('"timestamp"', "#a31515"), (": ", "#202124"), ('"2026-06-18T21:01:35.821576"', "#0b7500"), (",", "#202124")]),
        jline(1, [('"status"', "#a31515"), (": ", "#202124"), ('"running"', "#0b7500")]),
        [("}", "#202124")],
    ],
    os.path.join(HERE, "captura-03-api-status.png"))

# ---------- Captura 4: estructura del proyecto ----------
render_terminal([
    [prompt, ("ls ./src/main/java/ec/edu/ups/icc/fundamentos01/controllers/", WHITE, MONO_B)],
    [("StatusController.java", CYAN, MONO_B)],
    [("", WHITE)],
    [prompt, ("find src -type f", WHITE, MONO_B)],
    [("src/main/java/ec/edu/ups/icc/fundamentos01/Fundamentos01Application.java", WHITE)],
    [("src/main/java/ec/edu/ups/icc/fundamentos01/controllers/StatusController.java", CYAN)],
    [("src/main/resources/application.properties", WHITE)],
    [("src/test/java/ec/edu/ups/icc/fundamentos01/Fundamentos01ApplicationTests.java", WHITE)],
    [("", WHITE)],
    [prompt],
], os.path.join(HERE, "captura-04-estructura.png"))

print("OK")
