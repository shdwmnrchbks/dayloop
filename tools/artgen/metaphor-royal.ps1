# ROADMAP-v3 Phase 15 — Metaphor "Royal" decor art generator.
#
# Draws the pack's three decor PNGs from scratch (guide-derived vocabulary:
# ink, ivory parchment, antique gold, sage map tints per
# docs/references/metaphor-ui.md §2 — no game assets). Deterministic (seeded).
#
# Usage:  pwsh tools/artgen/metaphor-royal.ps1
# Output: content/packs/metaphor/art/decor-{header,panel,divider}.png
#
# Crop-safety contract (SkinDecor draws art aspect-preserving, center-cropped):
# critical geometry sits within the central 84% of each axis so a
# center-crop on any plausible surface keeps it.

Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)  # repo root
$art  = Join-Path $root 'content\packs\metaphor\art'
New-Item -ItemType Directory -Force $art | Out-Null

# Palette (docs/references/metaphor-ui.md §2)
$goldBright = 190, 165, 42    # #BEA52A
$goldMid    = 201, 162, 75    # #C9A24B
$goldBrass  = 158, 129, 95    # #9E815F
$ink        = 20, 20, 24      # #141418
$ivory      = 240, 239, 228   # #F0EFE4
$sage       = 144, 168, 144   # #90A890
$bronze     = 126, 96, 60     # #7E603C

function New-Canvas([int]$w, [int]$h) {
    $bmp = New-Object System.Drawing.Bitmap($w, $h)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.Clear([System.Drawing.Color]::Transparent)
    return @{ Bmp = $bmp; G = $g; W = $w; H = $h }
}

function Save-Canvas($c, [string]$name) {
    $path = Join-Path $art $name
    $c.Bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $c.G.Dispose(); $c.Bmp.Dispose()
    Write-Host ("wrote {0} ({1} bytes)" -f $path, (Get-Item $path).Length)
}

function Pen-Of($rgb, [int]$a, [float]$w) {
    New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb($a, $rgb[0], $rgb[1], $rgb[2]), $w)
}

function Brush-Of($rgb, [int]$a) {
    New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb($a, $rgb[0], $rgb[1], $rgb[2]))
}

# ---------------------------------------------------------------------------
# decor-header.png — top-bar band: faint ink wash, gold speckle, a bottom
# double gold rule, and filigree curls in the upper corners. Sits on both
# light and dark bars (transparent base; the Surface fill shows through).
# ---------------------------------------------------------------------------
$c = New-Canvas 1600 256
$g = $c.G

# Faint warm ink wash (top slightly heavier), far below text contrast.
$wash = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0, 0)), (New-Object System.Drawing.Point(0, 256)),
    [System.Drawing.Color]::FromArgb(46, $ink[0], $ink[1], $ink[2]),
    [System.Drawing.Color]::FromArgb(20, $ink[0], $ink[1], $ink[2]))
$g.FillRectangle($wash, 0, 0, 1600, 256)

# Sparse gold speckle — ornament is hairlines and corners, never a fill.
$rng = New-Object System.Random(0x2143)
1..110 | ForEach-Object {
    $x = $rng.NextDouble() * 1600
    $y = $rng.NextDouble() * 256
    $r = 0.8 + $rng.NextDouble() * 1.4
    $a = [int](36 + $rng.NextDouble() * 54)
    $g.FillEllipse((Brush-Of $goldMid $a), [float]($x - $r), [float]($y - $r), [float](2 * $r), [float](2 * $r))
}

# Corner filigree curls (top-left, top-right): nested quarter arcs.
1..2 | ForEach-Object {
    $side = $_
    foreach ($i in 0..2) {
        $r = 30 + $i * 16
        $pen = Pen-Of $goldMid ([int](120 - $i * 26)) 1.6
        if ($side -eq 1) {
            $g.DrawArc($pen, [float](10 + $i * 6), [float](10 + $i * 6), [float]$r, [float]$r, 180, 90)
        } else {
            $g.DrawArc($pen, [float](1600 - 10 - $i * 6 - $r), [float](10 + $i * 6), [float]$r, [float]$r, 270, 90)
        }
        $pen.Dispose()
    }
}

# Bottom double rule (a gold underline band under the bar's title), kept
# inside the central 84% band so any center-crop keeps both lines.
foreach ($i in 0..1) {
    $y = [float](190 + $i * 10)
    $pen = Pen-Of $goldBright ([int](170 - $i * 70)) ([float](2 - $i))
    $g.DrawLine($pen, [float]30, $y, [float]1570, $y)
    $pen.Dispose()
}
# Lozenge stops on the rule, at the rule's thirds.
foreach ($x in 533, 1067) {
    $b = Brush-Of $goldBright 190
    $pts = @( (New-Object System.Drawing.PointF($x, 183)), (New-Object System.Drawing.PointF(($x + 5), 191)),
              (New-Object System.Drawing.PointF($x, 199)), (New-Object System.Drawing.PointF(($x - 5), 191)) )
    $g.FillPolygon($b, $pts)
    $b.Dispose()
}

Save-Canvas $c 'decor-header.png'

# ---------------------------------------------------------------------------
# decor-panel.png — parchment fill texture (no border: the filigree painter
# draws the double gold frame at the panel's true size over this fill).
# Low-frequency ivory blotches + sage map-tint hints + fiber specks.
# ---------------------------------------------------------------------------
$c = New-Canvas 640 448
$g = $c.G
$rng = New-Object System.Random(0x0B5E)

# Blotches: mostly ivory parchment, a few sage map tints (§2 map tones).
1..46 | ForEach-Object {
    $x = $rng.NextDouble() * 640
    $y = $rng.NextDouble() * 448
    $rx = 26 + $rng.NextDouble() * 66
    $ry = 18 + $rng.NextDouble() * 48
    $rot = $rng.NextDouble() * 180
    $tint = if ($rng.NextDouble() -lt 0.22) { $sage } else { $ivory }
    $a = if ($tint[0] -eq $sage[0]) { 5 + $rng.NextDouble() * 5 } else { 7 + $rng.NextDouble() * 9 }
    $m = New-Object System.Drawing.Drawing2D.Matrix
    $m.RotateAt([float]$rot, (New-Object System.Drawing.PointF([float]$x, [float]$y)))
    $g.Transform = $m
    $g.FillEllipse((Brush-Of $tint ([int]$a)), [float]($x - $rx), [float]($y - $ry), [float](2 * $rx), [float](2 * $ry))
    $g.ResetTransform()
}

# Fiber specks.
1..240 | ForEach-Object {
    $x = $rng.NextDouble() * 640
    $y = $rng.NextDouble() * 448
    $r = 0.5 + $rng.NextDouble() * 0.9
    $g.FillEllipse((Brush-Of $bronze ([int](8 + $rng.NextDouble() * 14))), [float]($x - $r), [float]($y - $r), [float](2 * $r), [float](2 * $r))
}

Save-Canvas $c 'decor-panel.png'

# ---------------------------------------------------------------------------
# decor-divider.png — a gold rule with a center lozenge stop (the "gold-rule"
# ornament from the reference §7). Full-width hairline; the lozenge sits in
# the central crop-safe zone. Rendered into a ~10 dp band on the Day screen.
# ---------------------------------------------------------------------------
$c = New-Canvas 720 24
$g = $c.G

# Rule with alpha fading toward both ends.
1..72 | ForEach-Object {
    $i = $_
    $x0 = ($i - 1) * 10.0
    $t = [Math]::Abs($i - 36.5) / 35.5          # 0 center → 1 ends
    $a = [int](196 - 120 * $t)
    $pen = Pen-Of $goldBright $a 1.4
    $g.DrawLine($pen, [float]$x0, [float]12, [float]($x0 + 10), [float]12)
    $pen.Dispose()
}

# Center lozenge + inner diamond dot.
$b = Brush-Of $goldBright 226
$pts = @( (New-Object System.Drawing.PointF(360, 4)), (New-Object System.Drawing.PointF(368, 12)),
          (New-Object System.Drawing.PointF(360, 20)), (New-Object System.Drawing.PointF(352, 12)) )
$g.FillPolygon($b, $pts)
$b.Dispose()
$b2 = Brush-Of $ink 200
$pts2 = @( (New-Object System.Drawing.PointF(360, 9)), (New-Object System.Drawing.PointF(363, 12)),
           (New-Object System.Drawing.PointF(360, 15)), (New-Object System.Drawing.PointF(357, 12)) )
$g.FillPolygon($b2, $pts2)
$b2.Dispose()

# Small companion diamonds at ±120 px (crop-safe).
foreach ($x in 240, 480) {
    $b3 = Brush-Of $goldMid 150
    $p = @( (New-Object System.Drawing.PointF($x, 8)), (New-Object System.Drawing.PointF(($x + 4), 12)),
            (New-Object System.Drawing.PointF($x, 16)), (New-Object System.Drawing.PointF(($x - 4), 12)) )
    $g.FillPolygon($b3, $p)
    $b3.Dispose()
}

Save-Canvas $c 'decor-divider.png'

Write-Host 'Royal decor art complete.'


