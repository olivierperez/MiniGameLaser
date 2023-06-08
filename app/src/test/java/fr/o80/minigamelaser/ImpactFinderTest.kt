package fr.o80.minigamelaser

import androidx.compose.ui.geometry.Offset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ImpactFinderTest {

    private val impactFinder = ImpactFinder()

    @Test
    fun shouldFindImpactTopBorder() {
        val (xImpact, yImpact) = impactFinder.findImpact(
            width = 500f,
            height = 1000f,
            borderSize = 10f,
            source = Offset(250f, 1000f),
            direction = Offset(250f, 900f)
        )
        assertEquals(250f, xImpact)
        assertEquals(10f, yImpact)
    }
}
