package fr.o80.minigamelaser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fr.o80.minigamelaser.ui.theme.MiniGameLaserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniGameLaserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
        }
    }
}
