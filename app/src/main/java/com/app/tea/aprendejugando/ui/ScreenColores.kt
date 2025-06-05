package com.app.tea.aprendejugando.ui
import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tea.aprendejugando.R
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale

class ScreenColores : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColoresScreen()
        }
    }
}

@Composable
fun ColoresScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6D3)) // 🎨 Fondo beige
    ) {
        val isTablet = this@BoxWithConstraints.maxWidth > 600.dp
        val padding = if (isTablet) 50.dp else 24.dp
        val spacing = if (isTablet) 40.dp else 24.dp
        val titleFontSize = if (isTablet) 32.sp else 24.sp
        val lineHeight = if (isTablet) 40.sp else 36.sp
        val imageHeight = if (isTablet) 320.dp else 240.dp
        val buttonFontSize = if (isTablet) 16.sp else 14.sp
        val buttonWidth = if (isTablet) 0.45f else 0.65f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟡 Título
            Text(
                text = "¡Jugamos con los colores!",
                color = Color(0xFF67644F),
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            // 🖼 Imagen ilustrativa
            Image(
                painter = painterResource(id = R.drawable.colores),
                contentDescription = "Ilustración de colores",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacing))

            // 📷 Botón RA
            Box(
                modifier = Modifier
                    .height(70.dp)
                    .clickable {
                        val intent = Intent(context, ColorActivity::class.java)
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Botón RA Colores",
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "\uD83C\uDF08 Aprende los colores",
                    color = Color.White,
                    fontSize = buttonFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacing))

            // 🔙 Botón regresar
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        (context as? ComponentActivity)?.finish()
                    }
                    .height(56.dp)
                    .fillMaxWidth(buttonWidth)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "Regresar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
