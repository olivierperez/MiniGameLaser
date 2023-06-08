package fr.o80.minigamelaser

import androidx.compose.ui.geometry.Offset

class ImpactFinder {

    private fun findOnX(
        x: Float,
        height: ClosedRange<Float>,
        source: Offset,
        direction: Offset
    ): Offset? {
        val yImpactOnVerticalBorder =
            source.y + ((x - source.x) * (direction.y - source.y)) / (direction.x - source.x)

        return yImpactOnVerticalBorder
            .takeIf { it in height }
            ?.let { y -> Offset(x, y) }
            ?.takeUnless { it == source }
    }

    private fun findOnY(
        y: Float,
        width: ClosedRange<Float>,
        source: Offset,
        touchedPoint: Offset
    ): Offset? {
        val xImpactOnHorizontalBorder =
            source.x + ((y - source.y) * (touchedPoint.x - source.x)) / (touchedPoint.y - source.y)

        return xImpactOnHorizontalBorder
            .takeIf { it in width }
            ?.let { x -> Offset(x, y) }
            ?.takeUnless { it == source }
    }

    fun findImpact(
        width: Float,
        height: Float,
        borderSize: Float,
        source: Offset,
        direction: Offset
    ): Offset {
        return findOnX(
            x = borderSize,
            height = borderSize..height,
            source,
            direction
        ) ?: findOnX(
            x = width - borderSize,
            height = borderSize..height,
            source,
            direction
        ) ?: findOnY(
            y = borderSize,
            width = borderSize..width - borderSize,
            source,
            direction
        ) ?: findOnY(
            y = height - borderSize,
            width = borderSize..width - borderSize,
            source,
            direction
        )
        ?: Offset.Unspecified.also { println("not found") }

    }

    fun findImpacts(
        width: Float,
        height: Float,
        borderSize: Float,
        laser: Offset,
        direction: Offset
    ): List<Offset> {
        val impacts = mutableListOf<Offset>()
        val firstImpact = findImpact(width, height, borderSize, laser, direction)
        impacts += firstImpact

        val secondImpact =
            findBounce(firstImpact, borderSize, direction, width, height)
                ?: return impacts

        impacts += secondImpact
        return impacts
    }

    private fun findBounce(
        firstImpact: Offset,
        borderSize: Float,
        direction: Offset,
        width: Float,
        height: Float
    ): Offset? = when {
        firstImpact.x == borderSize || firstImpact.x == width - borderSize -> {
            val newDirection = Offset(
                x = direction.x,
                y = firstImpact.y - (direction.y - firstImpact.y)
            )
            findImpact(width, height, borderSize, firstImpact, newDirection)
        }

        firstImpact.y == borderSize -> {
            val newDirection = Offset(
                x = firstImpact.x - (direction.x - firstImpact.x),
                y = direction.y
            )
            findImpact(width, height, borderSize, firstImpact, newDirection)
        }

        else -> null
    }

}
