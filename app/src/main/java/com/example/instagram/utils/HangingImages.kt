import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.instagram.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

@Composable
fun CurvedHangingImages() {
    val rotations = remember { List(6) { Random.nextFloat() * 10 - 10 } } // Random rotations
    val curveHeight = 18.dp // Height of the curve

    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        for (i in 0..5) {
            val xOffset = (i * 86).dp // Spacing between images
            val yOffset = getCurveOffset(i, 2, curveHeight) // Calculate vertical offset for the curve

            Image(
                painter = painterResource(id = getImageResource(i + 1)), // Replace with your image resources
                contentDescription = "Curved Hanging Image $i",
                contentScale = ContentScale.Crop, // Scale the image to fill the space
                modifier = Modifier
                    .size(width = 72.dp, height = 96.dp) // Set width and height separately
                    .offset(x = xOffset, y = yOffset)
                    .clip(RoundedCornerShape(16.dp))
                    .graphicsLayer(rotationZ = rotations[i]) // Apply random rotation
            )
        }
    }
}

// Helper function to calculate vertical offset based on a sine curve
fun getCurveOffset(index: Int, total: Int, height: Dp, curveMultiplier: Float = 2.5f): Dp {
    val normalizedIndex = index.toFloat() / (total - 1).toFloat() // Normalize index to range [0, 1]
    val heightPx = height.value * 2f // Keep the amplitude as is for the desired height
    val offset = heightPx * -cos(normalizedIndex * PI * curveMultiplier).toFloat() // Multiply for more curves
    return offset.dp // Convert back to Dp
}


// Replace this with your actual image resources
fun getImageResource(index: Int): Int {
    return when (index) {
        1 -> R.drawable.img2
        2 -> R.drawable.img3
        3 -> R.drawable.img3
        4 -> R.drawable.img2
        5 -> R.drawable.img2
        6 -> R.drawable.img3
        else -> R.drawable.img2 // Default image
    }
}

