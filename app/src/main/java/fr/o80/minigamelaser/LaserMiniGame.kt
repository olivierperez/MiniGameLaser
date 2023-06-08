package fr.o80.minigamelaser

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.o80.minigamelaser.ui.theme.MiniGameLaserTheme

@Composable
fun LaserMiniGame(
    modifier: Modifier = Modifier,
    bordersSize: Dp = 4.dp,
    originSize: Dp = 16.dp,
    originColor: Color = Color(0xFF00FFFF),
    originBorder: Dp = 2.dp,
    originBorderColor: Color = Color(0xFF00A0A0),
    laserSize: Dp = 3.dp,
    laserColor: Color = Color.Red,
    leftMirrors: List<Mirror>,
    rightMirrors: List<Mirror>,
    topMirrors: List<Mirror>,
    mirrorsColor: Color = Color.Cyan
) {
    val impactFinder = remember { ImpactFinder() }
    val originSizePx = with(LocalDensity.current) { originSize.toPx() }
    val originBorderSizePx = with(LocalDensity.current) { originBorder.toPx() }
    val laserSizePx = with(LocalDensity.current) { laserSize.toPx() }
    val bordersSizePx = with(LocalDensity.current) { bordersSize.toPx() }

    var touchedPoint by remember { mutableStateOf(Offset.Unspecified) }
    var touching by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { position ->
                        touchedPoint = position
                        touching = true
                    },
                    onDragEnd = {
                        touching = false
                    },
                    onDragCancel = {
                        touching = false
                    },
                    onDrag = { pointerInputChange, _ ->
                        touchedPoint = pointerInputChange.position
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { position ->
                        touchedPoint = position
                    }
                )
            }
    ) {
        val laser = Offset(size.width / 2f, size.height - bordersSizePx)
        if (touchedPoint != Offset.Unspecified && touchedPoint.y < size.height) {
            drawLaser(
                impactFinder,
                bordersSizePx,
                laser,
                touchedPoint,
                laserColor,
                laserSizePx
            )
        }

        drawLaserOrigin(
            laser,
            originSizePx,
            originColor,
            originBorderSizePx,
            originBorderColor
        )
        drawBorders(
            bordersSizePx,
            Color.Black
        )
        drawMirrors(
            leftMirrors,
            rightMirrors,
            topMirrors,
            mirrorsColor,
            bordersSizePx
        )
    }
}

private fun DrawScope.drawLaser(
    impactFinder: ImpactFinder,
    bordersSizePx: Float,
    laser: Offset,
    touchedPoint: Offset,
    laserColor: Color,
    laserSizePx: Float
) {
    val impacts = impactFinder.findImpacts(
        width = size.width,
        height = size.height,
        borderSize = bordersSizePx,
        laser = laser,
        direction = touchedPoint
    )

    impacts.any { it == Offset.Unspecified } && return

    val path = Path().apply {
        moveTo(laser.x, laser.y)
        impacts.forEach { impact ->
            lineTo(impact.x, impact.y)
        }
    }
    drawPath(
        color = laserColor,
        style = Stroke(width = laserSizePx),
        path = path
    )
}

fun DrawScope.drawMirrors(
    leftMirrors: List<Mirror>,
    rightMirrors: List<Mirror>,
    topMirrors: List<Mirror>,
    mirrorsColor: Color,
    bordersSizePx: Float
) {
    leftMirrors.forEach { mirror ->
        drawRect(
            color = mirrorsColor,
            topLeft = Offset(
                bordersSizePx * 0.1f,
                size.height * (mirror.positionInPercent / 100f - mirror.sizeInPercent / 100f / 2f)
            ),
            size = Size(
                bordersSizePx * 0.9f,
                size.height * mirror.sizeInPercent / 100f
            )
        )
    }
    rightMirrors.forEach { mirror ->
        drawRect(
            color = mirrorsColor,
            topLeft = Offset(
                size.width - bordersSizePx,
                size.height * (mirror.positionInPercent / 100f - mirror.sizeInPercent / 100f / 2f)
            ),
            size = Size(
                bordersSizePx * 0.9f,
                size.height * mirror.sizeInPercent / 100f
            )
        )
    }
    topMirrors.forEach { mirror ->
        drawRect(
            color = mirrorsColor,
            topLeft = Offset(
                size.width * (mirror.positionInPercent / 100f - mirror.sizeInPercent / 100f / 2f),
                bordersSizePx * 0.1f
            ),
            size = Size(
                size.width * mirror.sizeInPercent / 100f,
                bordersSizePx * 0.9f
            )
        )
    }
}

fun DrawScope.drawBorders(
    sizePx: Float,
    color: Color
) {
    drawLine(
        color,
        strokeWidth = sizePx * 2,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f)
    )
    drawLine(
        color,
        strokeWidth = sizePx * 2,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height)
    )
    drawLine(
        color,
        strokeWidth = sizePx * 2,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height)
    )
    drawLine(
        color,
        strokeWidth = sizePx * 2,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height)
    )
}

private fun DrawScope.drawLaserOrigin(
    laser: Offset,
    originSizePx: Float,
    originColor: Color,
    originBorderSizePx: Float,
    originBorderColor: Color
) {
    drawCircle(
        originBorderColor,
        center = laser,
        radius = originSizePx + originBorderSizePx
    )
    drawCircle(
        originColor,
        center = laser,
        radius = originSizePx
    )
}

@Preview
@Composable
fun LaserMiniGamePreview() {
    MiniGameLaserTheme {
        LaserMiniGame(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            leftMirrors = listOf(
                Mirror(25, 5),
                Mirror(50, 5),
                Mirror(75, 5),
            ),
            rightMirrors = listOf(
                Mirror(25, 15),
                Mirror(50, 10),
                Mirror(75, 2),
            ),
            topMirrors = listOf(
                Mirror(25, 10),
            )
        )
    }
}
