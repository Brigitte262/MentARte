package com.app.tea.aprendejugando.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import android.content.Intent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

class ScreenNumeros : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumerosSimpleScreen()
        }
    }
}

@Composable
fun NumerosSimpleScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFD2)) // Fondo verde
    ) {
        val isTablet = this@BoxWithConstraints.maxWidth > 600.dp
        val padding = if (isTablet) 50.dp else 24.dp
        val spacing = if (isTablet) 40.dp else 24.dp
        val titleFontSize = if (isTablet) 32.sp else 24.sp
        val lineHeight = if (isTablet) 40.sp else 32.sp
        val imageHeight = if (isTablet) 320.dp else 240.dp
        val buttonFontSize = if (isTablet) 16.sp else 13.sp
        val buttonWidth = if (isTablet) 0.45f else 0.65f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔢 Título centrado
            Text(
                text = "¡Aprende los\nNúmeros del 1 al 100!",
                color = Color(0xFF67644F),
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            // 🖼 Imagen ilustrativa
            Image(
                painter = painterResource(id = R.drawable.numeros),
                contentDescription = "Ilustración números",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacing))

            // 📷 Botón de RA
            Box(
                modifier = Modifier
                    .height(70.dp)
                    .clickable {
                        val intent = Intent(context, ScreenOCRNumeros::class.java)
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Botón Aprender Números",
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "🔢 Aprende con números",
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
