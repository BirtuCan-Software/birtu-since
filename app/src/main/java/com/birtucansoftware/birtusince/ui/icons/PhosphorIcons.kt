package com.birtucansoftware.birtusince.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

data class PhosphorIconData(
    val name: String,
    val draw: DrawScope.(tint: Color, secondaryTint: Color) -> Unit
)

@Composable
fun PhosphorIcon(
    icon: PhosphorIconData,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalBirtuColors.current.text,
    secondaryTint: Color = tint.copy(alpha = 0.22f)
) {
    Canvas(modifier = modifier.size(size)) {
        icon.draw(this, tint, secondaryTint)
    }
}

object PhosphorIcons {

    // 1. House (Home)
    val House = PhosphorIconData("House") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val houseBody = Path().apply {
            moveTo(w * 0.22f, h * 0.44f)
            lineTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.44f)
            lineTo(w * 0.78f, h * 0.86f)
            lineTo(w * 0.22f, h * 0.86f)
            close()
        }
        drawPath(houseBody, brush = SolidColor(secondaryTint), style = Fill)

        val roof = Path().apply {
            moveTo(w * 0.12f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.14f)
            lineTo(w * 0.88f, h * 0.48f)
        }
        drawPath(roof, brush = SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val walls = Path().apply {
            moveTo(w * 0.22f, h * 0.44f)
            lineTo(w * 0.22f, h * 0.86f)
            lineTo(w * 0.78f, h * 0.86f)
            lineTo(w * 0.78f, h * 0.44f)
        }
        drawPath(walls, brush = SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 2. ChartBar (Stats)
    val ChartBar = PhosphorIconData("ChartBar") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Baseline
        drawLine(SolidColor(tint), Offset(w * 0.12f, h * 0.86f), Offset(w * 0.88f, h * 0.86f), strokeW, StrokeCap.Round)

        // Bar 1
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.18f, h * 0.48f), Size(w * 0.16f, h * 0.38f), CornerRadius(w * 0.04f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.18f, h * 0.48f), Size(w * 0.16f, h * 0.38f), CornerRadius(w * 0.04f), style = Stroke(strokeW))

        // Bar 2
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.42f, h * 0.22f), Size(w * 0.16f, h * 0.64f), CornerRadius(w * 0.04f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.42f, h * 0.22f), Size(w * 0.16f, h * 0.64f), CornerRadius(w * 0.04f), style = Stroke(strokeW))

        // Bar 3
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.66f, h * 0.36f), Size(w * 0.16f, h * 0.50f), CornerRadius(w * 0.04f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.66f, h * 0.36f), Size(w * 0.16f, h * 0.50f), CornerRadius(w * 0.04f), style = Stroke(strokeW))
    }

    // 3. Plus (Add)
    val Plus = PhosphorIconData("Plus") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(2.0f)
        drawCircle(SolidColor(secondaryTint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f))
        drawLine(SolidColor(tint), Offset(w * 0.22f, h * 0.5f), Offset(w * 0.78f, h * 0.5f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), strokeW, StrokeCap.Round)
    }

    // 4. Archive (Put away)
    val Archive = PhosphorIconData("Archive") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Box body
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.18f, h * 0.36f), Size(w * 0.64f, h * 0.48f), CornerRadius(w * 0.06f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.18f, h * 0.36f), Size(w * 0.64f, h * 0.48f), CornerRadius(w * 0.06f), style = Stroke(strokeW))

        // Lid
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.12f, h * 0.18f), Size(w * 0.76f, h * 0.20f), CornerRadius(w * 0.05f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.12f, h * 0.18f), Size(w * 0.76f, h * 0.20f), CornerRadius(w * 0.05f), style = Stroke(strokeW))

        // Handle
        drawLine(SolidColor(tint), Offset(w * 0.40f, h * 0.52f), Offset(w * 0.60f, h * 0.52f), strokeW, StrokeCap.Round)
    }

    // 5. GearSix (Settings)
    val GearSix = PhosphorIconData("GearSix") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.32f, center = Offset(w / 2f, h / 2f))
        drawCircle(SolidColor(tint), radius = w * 0.32f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))
        drawCircle(SolidColor(tint), radius = w * 0.12f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))

        for (i in 0 until 6) {
            val angle = i * (Math.PI / 3)
            val startX = (w / 2f + (w * 0.32f) * Math.cos(angle)).toFloat()
            val startY = (h / 2f + (h * 0.32f) * Math.sin(angle)).toFloat()
            val endX = (w / 2f + (w * 0.44f) * Math.cos(angle)).toFloat()
            val endY = (h / 2f + (h * 0.44f) * Math.sin(angle)).toFloat()
            drawLine(SolidColor(tint), Offset(startX, startY), Offset(endX, endY), strokeWidth = strokeW * 1.3f, cap = StrokeCap.Round)
        }
    }

    // 6. ArrowCounterClockwise (Start over / Past runs)
    val ArrowCounterClockwise = PhosphorIconData("ArrowCounterClockwise") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.085f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.32f, center = Offset(w / 2f, h / 2f))

        // Counter-clockwise arc starting from top-left curving down, right, and up
        val arc = Path().apply {
            moveTo(w * 0.32f, h * 0.28f)
            cubicTo(w * 0.16f, h * 0.44f, w * 0.20f, h * 0.78f, w * 0.50f, h * 0.82f)
            cubicTo(w * 0.78f, h * 0.82f, w * 0.84f, h * 0.52f, w * 0.76f, h * 0.34f)
            cubicTo(w * 0.70f, h * 0.20f, w * 0.50f, h * 0.20f, w * 0.32f, h * 0.28f)
        }
        drawPath(arc, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Clean arrowhead pointing down-left at top arc start
        val arrow = Path().apply {
            moveTo(w * 0.44f, h * 0.14f)
            lineTo(w * 0.30f, h * 0.28f)
            lineTo(w * 0.44f, h * 0.40f)
        }
        drawPath(arrow, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 7. Pause
    val Pause = PhosphorIconData("Pause") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(2.0f)
        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))
        drawLine(SolidColor(tint), Offset(w * 0.38f, h * 0.28f), Offset(w * 0.38f, h * 0.72f), strokeW * 1.5f, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.62f, h * 0.28f), Offset(w * 0.62f, h * 0.72f), strokeW * 1.5f, StrokeCap.Round)
    }

    // 8. Play (Resume)
    val Play = PhosphorIconData("Play") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val triangle = Path().apply {
            moveTo(w * 0.32f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.50f)
            lineTo(w * 0.32f, h * 0.78f)
            close()
        }
        drawPath(triangle, SolidColor(secondaryTint), style = Fill)
        drawPath(triangle, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 9. PencilSimple (Edit)
    val PencilSimple = PhosphorIconData("PencilSimple") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val pencilBody = Path().apply {
            moveTo(w * 0.68f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.32f)
            lineTo(w * 0.36f, h * 0.78f)
            lineTo(w * 0.20f, h * 0.82f)
            lineTo(w * 0.24f, h * 0.66f)
            close()
        }
        drawPath(pencilBody, SolidColor(secondaryTint), style = Fill)
        drawPath(pencilBody, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(SolidColor(tint), Offset(w * 0.56f, h * 0.30f), Offset(w * 0.70f, h * 0.44f), strokeW, StrokeCap.Round)
    }

    // 10. Trash (Delete)
    val Trash = PhosphorIconData("Trash") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Can
        val can = Path().apply {
            moveTo(w * 0.24f, h * 0.34f)
            lineTo(w * 0.28f, h * 0.84f)
            lineTo(w * 0.72f, h * 0.84f)
            lineTo(w * 0.76f, h * 0.34f)
            close()
        }
        drawPath(can, SolidColor(secondaryTint), style = Fill)
        drawPath(can, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Rim
        drawLine(SolidColor(tint), Offset(w * 0.16f, h * 0.34f), Offset(w * 0.84f, h * 0.34f), strokeW, StrokeCap.Round)
        // Handle
        val handle = Path().apply {
            moveTo(w * 0.38f, h * 0.34f)
            lineTo(w * 0.38f, h * 0.20f)
            lineTo(w * 0.62f, h * 0.20f)
            lineTo(w * 0.62f, h * 0.34f)
        }
        drawPath(handle, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Vertical lines
        drawLine(SolidColor(tint), Offset(w * 0.42f, h * 0.46f), Offset(w * 0.42f, h * 0.72f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.58f, h * 0.46f), Offset(w * 0.58f, h * 0.72f), strokeW, StrokeCap.Round)
    }

    // 11. Lock
    val Lock = PhosphorIconData("Lock") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Shackle
        val shackle = Path().apply {
            moveTo(w * 0.32f, h * 0.46f)
            lineTo(w * 0.32f, h * 0.30f)
            cubicTo(w * 0.32f, h * 0.16f, w * 0.68f, h * 0.16f, w * 0.68f, h * 0.30f)
            lineTo(w * 0.68f, h * 0.46f)
        }
        drawPath(shackle, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Body
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.20f, h * 0.44f), Size(w * 0.60f, h * 0.44f), CornerRadius(w * 0.08f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.20f, h * 0.44f), Size(w * 0.60f, h * 0.44f), CornerRadius(w * 0.08f), style = Stroke(strokeW))

        // Keyhole
        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.62f))
        drawLine(SolidColor(tint), Offset(w * 0.5f, h * 0.64f), Offset(w * 0.5f, h * 0.74f), strokeW, StrokeCap.Round)
    }

    // 12. LockOpen
    val LockOpen = PhosphorIconData("LockOpen") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Open shackle
        val shackle = Path().apply {
            moveTo(w * 0.32f, h * 0.44f)
            lineTo(w * 0.32f, h * 0.26f)
            cubicTo(w * 0.32f, h * 0.12f, w * 0.68f, h * 0.12f, w * 0.68f, h * 0.26f)
        }
        drawPath(shackle, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Body
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.20f, h * 0.44f), Size(w * 0.60f, h * 0.44f), CornerRadius(w * 0.08f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.20f, h * 0.44f), Size(w * 0.60f, h * 0.44f), CornerRadius(w * 0.08f), style = Stroke(strokeW))
    }

    // 13. Fingerprint
    val Fingerprint = PhosphorIconData("Fingerprint") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.075f).coerceAtLeast(1.6f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))

        val arch1 = Path().apply {
            moveTo(w * 0.30f, h * 0.76f)
            cubicTo(w * 0.26f, h * 0.54f, w * 0.34f, h * 0.32f, w * 0.50f, h * 0.30f)
            cubicTo(w * 0.66f, h * 0.32f, w * 0.74f, h * 0.54f, w * 0.70f, h * 0.76f)
        }
        drawPath(arch1, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round))

        val arch2 = Path().apply {
            moveTo(w * 0.40f, h * 0.82f)
            cubicTo(w * 0.38f, h * 0.58f, w * 0.42f, h * 0.44f, w * 0.50f, h * 0.44f)
            cubicTo(w * 0.58f, h * 0.44f, w * 0.62f, h * 0.58f, w * 0.60f, h * 0.82f)
        }
        drawPath(arch2, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round))

        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.56f), Offset(w * 0.50f, h * 0.84f), strokeW, StrokeCap.Round)
    }

    // 14. Bell (Remind me)
    val Bell = PhosphorIconData("Bell") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val bellBody = Path().apply {
            moveTo(w * 0.20f, h * 0.70f)
            cubicTo(w * 0.24f, h * 0.46f, w * 0.30f, h * 0.30f, w * 0.50f, h * 0.26f)
            cubicTo(w * 0.70f, h * 0.30f, w * 0.76f, h * 0.46f, w * 0.80f, h * 0.70f)
            close()
        }
        drawPath(bellBody, SolidColor(secondaryTint), style = Fill)
        drawPath(bellBody, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(SolidColor(tint), Offset(w * 0.16f, h * 0.72f), Offset(w * 0.84f, h * 0.72f), strokeW, StrokeCap.Round)
        drawCircle(SolidColor(tint), radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.84f))
        drawCircle(SolidColor(tint), radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.18f))
    }

    // 15. CalendarBlank (Date and time)
    val CalendarBlank = PhosphorIconData("CalendarBlank") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.16f, h * 0.24f), Size(w * 0.68f, h * 0.62f), CornerRadius(w * 0.08f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.16f, h * 0.24f), Size(w * 0.68f, h * 0.62f), CornerRadius(w * 0.08f), style = Stroke(strokeW))

        drawLine(SolidColor(tint), Offset(w * 0.16f, h * 0.42f), Offset(w * 0.84f, h * 0.42f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.34f, h * 0.16f), Offset(w * 0.34f, h * 0.28f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.66f, h * 0.16f), Offset(w * 0.66f, h * 0.28f), strokeW, StrokeCap.Round)
    }

    // 16. Tag (Group)
    val Tag = PhosphorIconData("Tag") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val tagPath = Path().apply {
            moveTo(w * 0.18f, h * 0.46f)
            lineTo(w * 0.46f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.54f)
            lineTo(w * 0.54f, h * 0.82f)
            close()
        }
        drawPath(tagPath, SolidColor(secondaryTint), style = Fill)
        drawPath(tagPath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.68f, h * 0.32f))
    }

    // 17. Note
    val Note = PhosphorIconData("Note") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.20f, h * 0.18f), Size(w * 0.60f, h * 0.68f), CornerRadius(w * 0.08f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.20f, h * 0.18f), Size(w * 0.60f, h * 0.68f), CornerRadius(w * 0.08f), style = Stroke(strokeW))

        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.36f), Offset(w * 0.68f, h * 0.36f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.52f), Offset(w * 0.68f, h * 0.52f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.68f), Offset(w * 0.52f, h * 0.68f), strokeW, StrokeCap.Round)
    }

    // 18. MagnifyingGlass (Search)
    val MagnifyingGlass = PhosphorIconData("MagnifyingGlass") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.28f, center = Offset(w * 0.44f, h * 0.44f))
        drawCircle(SolidColor(tint), radius = w * 0.28f, center = Offset(w * 0.44f, h * 0.44f), style = Stroke(strokeW))
        drawLine(SolidColor(tint), Offset(w * 0.64f, h * 0.64f), Offset(w * 0.84f, h * 0.84f), strokeW * 1.3f, StrokeCap.Round)
    }

    // 19. DotsThreeVertical (More)
    val DotsThreeVertical = PhosphorIconData("DotsThreeVertical") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        drawCircle(SolidColor(tint), radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.24f))
        drawCircle(SolidColor(tint), radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.50f))
        drawCircle(SolidColor(tint), radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.76f))
    }

    // 20. CaretLeft (Back)
    val CaretLeft = PhosphorIconData("CaretLeft") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(2.0f)
        val path = Path().apply {
            moveTo(w * 0.62f, h * 0.24f)
            lineTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.62f, h * 0.76f)
        }
        drawPath(path, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 21. CaretRight (Go forward)
    val CaretRight = PhosphorIconData("CaretRight") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(2.0f)
        val path = Path().apply {
            moveTo(w * 0.38f, h * 0.24f)
            lineTo(w * 0.64f, h * 0.50f)
            lineTo(w * 0.38f, h * 0.76f)
        }
        drawPath(path, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 22. X (Close)
    val X = PhosphorIconData("X") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.085f).coerceAtLeast(1.8f)
        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))
        drawLine(SolidColor(tint), Offset(w * 0.30f, h * 0.30f), Offset(w * 0.70f, h * 0.70f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.70f, h * 0.30f), Offset(w * 0.30f, h * 0.70f), strokeW, StrokeCap.Round)
    }

    // 23. Check (Done)
    val Check = PhosphorIconData("Check") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(2.0f)
        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))
        val path = Path().apply {
            moveTo(w * 0.26f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.70f)
            lineTo(w * 0.76f, h * 0.34f)
        }
        drawPath(path, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 24. Warning (Clock warning)
    val Warning = PhosphorIconData("Warning") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val triangle = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.86f, h * 0.82f)
            lineTo(w * 0.14f, h * 0.82f)
            close()
        }
        drawPath(triangle, SolidColor(secondaryTint), style = Fill)
        drawPath(triangle, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.40f), Offset(w * 0.50f, h * 0.60f), strokeW, StrokeCap.Round)
        drawCircle(SolidColor(tint), radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.72f))
    }

    // 25. Hourglass (Empty state)
    val Hourglass = PhosphorIconData("Hourglass") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val path = Path().apply {
            moveTo(w * 0.26f, h * 0.22f)
            lineTo(w * 0.74f, h * 0.22f)
            lineTo(w * 0.54f, h * 0.50f)
            lineTo(w * 0.74f, h * 0.78f)
            lineTo(w * 0.26f, h * 0.78f)
            lineTo(w * 0.46f, h * 0.50f)
            close()
        }
        drawPath(path, SolidColor(secondaryTint), style = Fill)
        drawPath(path, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(SolidColor(tint), Offset(w * 0.20f, h * 0.22f), Offset(w * 0.80f, h * 0.22f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.20f, h * 0.78f), Offset(w * 0.80f, h * 0.78f), strokeW, StrokeCap.Round)
    }

    // 26. Timer (Default item icon)
    val Timer = PhosphorIconData("Timer") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.34f, center = Offset(w / 2f, h * 0.56f))
        drawCircle(SolidColor(tint), radius = w * 0.34f, center = Offset(w / 2f, h * 0.56f), style = Stroke(strokeW))

        // Top stem & button
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.16f), Offset(w * 0.50f, h * 0.22f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.42f, h * 0.16f), Offset(w * 0.58f, h * 0.16f), strokeW, StrokeCap.Round)

        // Hands
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.56f), Offset(w * 0.50f, h * 0.38f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.56f), Offset(w * 0.64f, h * 0.56f), strokeW, StrokeCap.Round)
    }

    // 27. DownloadSimple (Save a copy)
    val DownloadSimple = PhosphorIconData("DownloadSimple") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.20f), Offset(w * 0.50f, h * 0.62f), strokeW, StrokeCap.Round)

        val arrow = Path().apply {
            moveTo(w * 0.34f, h * 0.46f)
            lineTo(w * 0.50f, h * 0.62f)
            lineTo(w * 0.66f, h * 0.46f)
        }
        drawPath(arrow, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val tray = Path().apply {
            moveTo(w * 0.20f, h * 0.64f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.64f)
        }
        drawPath(tray, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 28. UploadSimple (Bring back a copy)
    val UploadSimple = PhosphorIconData("UploadSimple") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.62f), Offset(w * 0.50f, h * 0.22f), strokeW, StrokeCap.Round)

        val arrow = Path().apply {
            moveTo(w * 0.34f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.22f)
            lineTo(w * 0.66f, h * 0.38f)
        }
        drawPath(arrow, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val tray = Path().apply {
            moveTo(w * 0.20f, h * 0.64f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.64f)
        }
        drawPath(tray, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 29. Palette (Theme)
    val Palette = PhosphorIconData("Palette") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val palettePath = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            cubicTo(w * 0.24f, h * 0.16f, w * 0.16f, h * 0.38f, w * 0.16f, h * 0.58f)
            cubicTo(w * 0.16f, h * 0.78f, w * 0.36f, h * 0.86f, w * 0.50f, h * 0.86f)
            cubicTo(w * 0.58f, h * 0.86f, w * 0.64f, h * 0.78f, w * 0.64f, h * 0.72f)
            cubicTo(w * 0.64f, h * 0.66f, w * 0.72f, h * 0.62f, w * 0.80f, h * 0.62f)
            cubicTo(w * 0.88f, h * 0.62f, w * 0.86f, h * 0.40f, w * 0.78f, h * 0.28f)
            close()
        }
        drawPath(palettePath, SolidColor(secondaryTint), style = Fill)
        drawPath(palettePath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.34f, h * 0.38f))
        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.52f, h * 0.32f))
        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.68f, h * 0.42f))
    }

    // 30. WifiSlash (Offline promise)
    val WifiSlash = PhosphorIconData("WifiSlash") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.36f, center = Offset(w / 2f, h / 2f))

        val arc1 = Path().apply {
            moveTo(w * 0.24f, h * 0.38f)
            cubicTo(w * 0.38f, h * 0.26f, w * 0.62f, h * 0.26f, w * 0.76f, h * 0.38f)
        }
        drawPath(arc1, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round))

        drawCircle(SolidColor(tint), radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.76f))
        drawLine(SolidColor(tint), Offset(w * 0.18f, h * 0.18f), Offset(w * 0.82f, h * 0.82f), strokeW * 1.2f, StrokeCap.Round)
    }

    // 31. ShieldCheck
    val ShieldCheck = PhosphorIconData("ShieldCheck") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val shield = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.80f, h * 0.26f)
            lineTo(w * 0.80f, h * 0.56f)
            cubicTo(w * 0.80f, h * 0.76f, w * 0.54f, h * 0.86f, w * 0.50f, h * 0.88f)
            cubicTo(w * 0.46f, h * 0.86f, w * 0.20f, h * 0.76f, w * 0.20f, h * 0.56f)
            lineTo(w * 0.20f, h * 0.26f)
            close()
        }
        drawPath(shield, SolidColor(secondaryTint), style = Fill)
        drawPath(shield, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val check = Path().apply {
            moveTo(w * 0.36f, h * 0.52f)
            lineTo(w * 0.46f, h * 0.62f)
            lineTo(w * 0.66f, h * 0.42f)
        }
        drawPath(check, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 32. Eye & EyeSlash
    val Eye = PhosphorIconData("Eye") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val eyePath = Path().apply {
            moveTo(w * 0.14f, h * 0.50f)
            cubicTo(w * 0.28f, h * 0.28f, w * 0.72f, h * 0.28f, w * 0.86f, h * 0.50f)
            cubicTo(w * 0.72f, h * 0.72f, w * 0.28f, h * 0.72f, w * 0.14f, h * 0.50f)
            close()
        }
        drawPath(eyePath, SolidColor(secondaryTint), style = Fill)
        drawPath(eyePath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(SolidColor(tint), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.5f))
    }

    val EyeSlash = PhosphorIconData("EyeSlash") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)
        Eye.draw(this, tint, secondaryTint)
        drawLine(SolidColor(tint), Offset(w * 0.18f, h * 0.18f), Offset(w * 0.82f, h * 0.82f), strokeW * 1.2f, StrokeCap.Round)
    }

    // 33. ClockCounterClockwise
    val ClockCounterClockwise = ArrowCounterClockwise

    // 12 Group Icons: Timer, House, Heart, Car, Plant, Drop, Pill, Broom, Wrench, Barbell, Book, Star
    val Heart = PhosphorIconData("Heart") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val heart = Path().apply {
            moveTo(w * 0.50f, h * 0.80f)
            cubicTo(w * 0.20f, h * 0.60f, w * 0.14f, h * 0.36f, w * 0.26f, h * 0.24f)
            cubicTo(w * 0.38f, h * 0.14f, w * 0.50f, h * 0.24f, w * 0.50f, h * 0.32f)
            cubicTo(w * 0.50f, h * 0.24f, w * 0.62f, h * 0.14f, w * 0.74f, h * 0.24f)
            cubicTo(w * 0.86f, h * 0.36f, w * 0.80f, h * 0.60f, w * 0.50f, h * 0.80f)
            close()
        }
        drawPath(heart, SolidColor(secondaryTint), style = Fill)
        drawPath(heart, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    val Car = PhosphorIconData("Car") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val carBody = Path().apply {
            moveTo(w * 0.16f, h * 0.64f)
            lineTo(w * 0.22f, h * 0.42f)
            lineTo(w * 0.40f, h * 0.30f)
            lineTo(w * 0.64f, h * 0.30f)
            lineTo(w * 0.80f, h * 0.44f)
            lineTo(w * 0.86f, h * 0.64f)
            close()
        }
        drawPath(carBody, SolidColor(secondaryTint), style = Fill)
        drawPath(carBody, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(SolidColor(tint), radius = w * 0.08f, center = Offset(w * 0.32f, h * 0.70f))
        drawCircle(SolidColor(tint), radius = w * 0.08f, center = Offset(w * 0.70f, h * 0.70f))
    }

    val Plant = PhosphorIconData("Plant") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.84f), Offset(w * 0.50f, h * 0.44f), strokeW, StrokeCap.Round)

        val leaf1 = Path().apply {
            moveTo(w * 0.50f, h * 0.58f)
            cubicTo(w * 0.30f, h * 0.52f, w * 0.22f, h * 0.36f, w * 0.34f, h * 0.28f)
            cubicTo(w * 0.46f, h * 0.26f, w * 0.50f, h * 0.44f, w * 0.50f, h * 0.58f)
        }
        drawPath(leaf1, SolidColor(secondaryTint), style = Fill)
        drawPath(leaf1, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val leaf2 = Path().apply {
            moveTo(w * 0.50f, h * 0.48f)
            cubicTo(w * 0.70f, h * 0.42f, w * 0.78f, h * 0.26f, w * 0.66f, h * 0.18f)
            cubicTo(w * 0.54f, h * 0.16f, w * 0.50f, h * 0.34f, w * 0.50f, h * 0.48f)
        }
        drawPath(leaf2, SolidColor(secondaryTint), style = Fill)
        drawPath(leaf2, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    val Drop = PhosphorIconData("Drop") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val drop = Path().apply {
            moveTo(w * 0.50f, h * 0.18f)
            cubicTo(w * 0.26f, h * 0.50f, w * 0.24f, h * 0.68f, w * 0.36f, h * 0.78f)
            cubicTo(w * 0.46f, h * 0.86f, w * 0.54f, h * 0.86f, w * 0.64f, h * 0.78f)
            cubicTo(w * 0.76f, h * 0.68f, w * 0.74f, h * 0.50f, w * 0.50f, h * 0.18f)
            close()
        }
        drawPath(drop, SolidColor(secondaryTint), style = Fill)
        drawPath(drop, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    val Pill = PhosphorIconData("Pill") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val pill = Path().apply {
            moveTo(w * 0.30f, h * 0.70f)
            lineTo(w * 0.70f, h * 0.30f)
        }
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.26f, h * 0.26f), Size(w * 0.48f, h * 0.48f), CornerRadius(w * 0.24f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.26f, h * 0.26f), Size(w * 0.48f, h * 0.48f), CornerRadius(w * 0.24f), style = Stroke(strokeW))
        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.68f), Offset(w * 0.68f, h * 0.32f), strokeW, StrokeCap.Round)
    }

    val Broom = PhosphorIconData("Broom") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawLine(SolidColor(tint), Offset(w * 0.76f, h * 0.20f), Offset(w * 0.46f, h * 0.56f), strokeW, StrokeCap.Round)

        val bristles = Path().apply {
            moveTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.56f, h * 0.64f)
            lineTo(w * 0.32f, h * 0.86f)
            lineTo(w * 0.20f, h * 0.74f)
            close()
        }
        drawPath(bristles, SolidColor(secondaryTint), style = Fill)
        drawPath(bristles, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    val Wrench = PhosphorIconData("Wrench") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.68f), Offset(w * 0.62f, h * 0.38f), strokeW * 1.3f, StrokeCap.Round)
        drawCircle(SolidColor(secondaryTint), radius = w * 0.16f, center = Offset(w * 0.68f, h * 0.32f))
        drawCircle(SolidColor(tint), radius = w * 0.16f, center = Offset(w * 0.68f, h * 0.32f), style = Stroke(strokeW))
    }

    val Barbell = PhosphorIconData("Barbell") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawLine(SolidColor(tint), Offset(w * 0.20f, h * 0.50f), Offset(w * 0.80f, h * 0.50f), strokeW, StrokeCap.Round)
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.24f, h * 0.30f), Size(w * 0.08f, h * 0.40f), CornerRadius(w * 0.02f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.24f, h * 0.30f), Size(w * 0.08f, h * 0.40f), CornerRadius(w * 0.02f), style = Stroke(strokeW))
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.68f, h * 0.30f), Size(w * 0.08f, h * 0.40f), CornerRadius(w * 0.02f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.68f, h * 0.30f), Size(w * 0.08f, h * 0.40f), CornerRadius(w * 0.02f), style = Stroke(strokeW))
    }

    val Book = PhosphorIconData("Book") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val book = Path().apply {
            moveTo(w * 0.20f, h * 0.24f)
            lineTo(w * 0.50f, h * 0.30f)
            lineTo(w * 0.80f, h * 0.24f)
            lineTo(w * 0.80f, h * 0.76f)
            lineTo(w * 0.50f, h * 0.82f)
            lineTo(w * 0.20f, h * 0.76f)
            close()
        }
        drawPath(book, SolidColor(secondaryTint), style = Fill)
        drawPath(book, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.30f), Offset(w * 0.50f, h * 0.82f), strokeW, StrokeCap.Round)
    }

    val Star = PhosphorIconData("Star") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val star = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.61f, h * 0.38f)
            lineTo(w * 0.84f, h * 0.41f)
            lineTo(w * 0.67f, h * 0.58f)
            lineTo(w * 0.72f, h * 0.82f)
            lineTo(w * 0.50f, h * 0.70f)
            lineTo(w * 0.28f, h * 0.82f)
            lineTo(w * 0.33f, h * 0.58f)
            lineTo(w * 0.16f, h * 0.41f)
            lineTo(w * 0.39f, h * 0.38f)
            close()
        }
        drawPath(star, SolidColor(secondaryTint), style = Fill)
        drawPath(star, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    val Question = PhosphorIconData("Question") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f))
        drawCircle(SolidColor(tint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))

        val markPath = Path().apply {
            moveTo(w * 0.38f, h * 0.38f)
            cubicTo(w * 0.38f, h * 0.28f, w * 0.62f, h * 0.28f, w * 0.62f, h * 0.40f)
            cubicTo(w * 0.62f, h * 0.50f, w * 0.50f, h * 0.54f, w * 0.50f, h * 0.62f)
        }
        drawPath(markPath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(SolidColor(tint), radius = w * 0.045f, center = Offset(w * 0.50f, h * 0.74f))
    }

    // 34. Sun (Light mode)
    val Sun = PhosphorIconData("Sun") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.24f, center = Offset(w / 2f, h / 2f))
        drawCircle(SolidColor(tint), radius = w * 0.24f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))

        val rays = 8
        for (i in 0 until rays) {
            val angle = i * (Math.PI * 2 / rays)
            val cosA = Math.cos(angle).toFloat()
            val sinA = Math.sin(angle).toFloat()
            val startX = w / 2f + (w * 0.32f) * cosA
            val startY = h / 2f + (h * 0.32f) * sinA
            val endX = w / 2f + (w * 0.44f) * cosA
            val endY = h / 2f + (h * 0.44f) * sinA
            drawLine(SolidColor(tint), Offset(startX, startY), Offset(endX, endY), strokeW, StrokeCap.Round)
        }
    }

    // 35. Moon (Dark mode - Crescent Moon)
    val Moon = PhosphorIconData("Moon") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val moonPath = Path().apply {
            // Top horn tip
            moveTo(w * 0.68f, h * 0.12f)
            // Outer circular arc curving smoothly around left side to bottom horn tip
            cubicTo(w * 0.32f, h * 0.12f, w * 0.16f, h * 0.35f, w * 0.16f, h * 0.50f)
            cubicTo(w * 0.16f, h * 0.65f, w * 0.32f, h * 0.88f, w * 0.68f, h * 0.88f)
            // Inner crescent curve carving back up to top horn tip
            cubicTo(w * 0.44f, h * 0.76f, w * 0.36f, h * 0.64f, w * 0.36f, h * 0.50f)
            cubicTo(w * 0.36f, h * 0.36f, w * 0.44f, h * 0.24f, w * 0.68f, h * 0.12f)
            close()
        }
        drawPath(moonPath, SolidColor(secondaryTint), style = Fill)
        drawPath(moonPath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 36. Sparkle / Stars (Attractive aesthetic accent)
    val Sparkle = PhosphorIconData("Sparkle") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        val sparklePath = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            cubicTo(w * 0.50f, h * 0.36f, w * 0.64f, h * 0.50f, w * 0.86f, h * 0.50f)
            cubicTo(w * 0.64f, h * 0.50f, w * 0.50f, h * 0.64f, w * 0.50f, h * 0.86f)
            cubicTo(w * 0.50f, h * 0.64f, w * 0.36f, h * 0.50f, w * 0.14f, h * 0.50f)
            cubicTo(w * 0.36f, h * 0.50f, w * 0.50f, h * 0.36f, w * 0.50f, h * 0.14f)
            close()
        }
        drawPath(sparklePath, SolidColor(secondaryTint), style = Fill)
        drawPath(sparklePath, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 37. PlusCircle
    val PlusCircle = PhosphorIconData("PlusCircle") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.085f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f))
        drawCircle(SolidColor(tint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))
        drawLine(SolidColor(tint), Offset(w * 0.32f, h * 0.5f), Offset(w * 0.68f, h * 0.5f), strokeW, StrokeCap.Round)
        drawLine(SolidColor(tint), Offset(w * 0.5f, h * 0.32f), Offset(w * 0.5f, h * 0.68f), strokeW, StrokeCap.Round)
    }

    // 38. ArrowSquareOut (External link)
    val ArrowSquareOut = PhosphorIconData("ArrowSquareOut") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Box
        val box = Path().apply {
            moveTo(w * 0.42f, h * 0.22f)
            lineTo(w * 0.22f, h * 0.22f)
            lineTo(w * 0.22f, h * 0.78f)
            lineTo(w * 0.78f, h * 0.78f)
            lineTo(w * 0.78f, h * 0.58f)
        }
        drawPath(box, SolidColor(secondaryTint), style = Fill)
        drawPath(box, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Arrow
        drawLine(SolidColor(tint), Offset(w * 0.48f, h * 0.52f), Offset(w * 0.78f, h * 0.22f), strokeW, StrokeCap.Round)
        val arrowHead = Path().apply {
            moveTo(w * 0.58f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.42f)
        }
        drawPath(arrowHead, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // 39. Info
    val Info = PhosphorIconData("Info") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f))
        drawCircle(SolidColor(tint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f), style = Stroke(strokeW))
        drawCircle(SolidColor(tint), radius = strokeW * 0.8f, center = Offset(w / 2f, h * 0.35f))
        drawLine(SolidColor(tint), Offset(w / 2f, h * 0.46f), Offset(w / 2f, h * 0.68f), strokeW, StrokeCap.Round)
    }

    // 40. ArrowsDownUp (Sort)
    val ArrowsDownUp = PhosphorIconData("ArrowsDownUp") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.085f).coerceAtLeast(1.8f)

        drawCircle(SolidColor(secondaryTint), radius = w * 0.38f, center = Offset(w / 2f, h / 2f))

        // Left arrow pointing up
        val upArrow = Path().apply {
            moveTo(w * 0.22f, h * 0.40f)
            lineTo(w * 0.34f, h * 0.26f)
            lineTo(w * 0.46f, h * 0.40f)
        }
        drawPath(upArrow, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(SolidColor(tint), Offset(w * 0.34f, h * 0.26f), Offset(w * 0.34f, h * 0.74f), strokeW, StrokeCap.Round)

        // Right arrow pointing down
        val downArrow = Path().apply {
            moveTo(w * 0.54f, h * 0.60f)
            lineTo(w * 0.66f, h * 0.74f)
            lineTo(w * 0.78f, h * 0.60f)
        }
        drawPath(downArrow, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(SolidColor(tint), Offset(w * 0.66f, h * 0.26f), Offset(w * 0.66f, h * 0.74f), strokeW, StrokeCap.Round)
    }

    // 41. PushPin (Pin item)
    val PushPin = PhosphorIconData("PushPin") { tint, secondaryTint ->
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Top head cap
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.30f, h * 0.16f), Size(w * 0.40f, h * 0.10f), CornerRadius(w * 0.04f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.30f, h * 0.16f), Size(w * 0.40f, h * 0.10f), CornerRadius(w * 0.04f), style = Stroke(strokeW))

        // Body connecting cap to base plate
        val pinBody = Path().apply {
            moveTo(w * 0.36f, h * 0.26f)
            lineTo(w * 0.40f, h * 0.44f)
            lineTo(w * 0.60f, h * 0.44f)
            lineTo(w * 0.64f, h * 0.26f)
            close()
        }
        drawPath(pinBody, SolidColor(secondaryTint), style = Fill)
        drawPath(pinBody, SolidColor(tint), style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Base plate
        drawRoundRect(SolidColor(secondaryTint), Offset(w * 0.24f, h * 0.44f), Size(w * 0.52f, h * 0.08f), CornerRadius(w * 0.03f), style = Fill)
        drawRoundRect(SolidColor(tint), Offset(w * 0.24f, h * 0.44f), Size(w * 0.52f, h * 0.08f), CornerRadius(w * 0.03f), style = Stroke(strokeW))

        // Needle pointing down
        drawLine(SolidColor(tint), Offset(w * 0.50f, h * 0.52f), Offset(w * 0.50f, h * 0.84f), strokeW * 1.1f, StrokeCap.Round)
    }

    // 42. PushPinSlash (Unpin item)
    val PushPinSlash = PhosphorIconData("PushPinSlash") { tint, secondaryTint ->
        PushPin.draw(this, tint, secondaryTint)
        val w = size.width; val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)
        drawLine(SolidColor(tint), Offset(w * 0.20f, h * 0.20f), Offset(w * 0.80f, h * 0.80f), strokeW * 1.2f, StrokeCap.Round)
    }

    /**
     * Resolves group icon by name, defaulting to Timer
     */
    fun fromName(name: String?): PhosphorIconData = when (name?.lowercase()) {
        "house" -> House
        "heart" -> Heart
        "car" -> Car
        "plant" -> Plant
        "drop" -> Drop
        "pill" -> Pill
        "broom" -> Broom
        "wrench" -> Wrench
        "barbell" -> Barbell
        "book" -> Book
        "star" -> Star
        else -> Timer
    }

    val GroupIcons = listOf(
        "Timer" to Timer,
        "House" to House,
        "Heart" to Heart,
        "Car" to Car,
        "Plant" to Plant,
        "Drop" to Drop,
        "Pill" to Pill,
        "Broom" to Broom,
        "Wrench" to Wrench,
        "Barbell" to Barbell,
        "Book" to Book,
        "Star" to Star
    )
}
