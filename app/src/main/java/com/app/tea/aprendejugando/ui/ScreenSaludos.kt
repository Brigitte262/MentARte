package com.app.tea.aprendejugando.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width

class ScreenSaludos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaludosScreen()
        }
    }
}

@Composable
fun SaludosScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6D3)) // 🎨 Fondo beige
    ) {
        val isTablet = this.maxWidth > 600.dp

        // Variables adaptables
        val padding = if (isTablet) 40.dp else 24.dp
        val titleFontSize = if (isTablet) 36.sp else 30.sp
        val lineHeight = if (isTablet) 44.sp else 36.sp
        val imageHeight = if (isTablet) 280.dp else 240.dp
        val spacingLarge = if (isTablet) 32.dp else 24.dp
        val spacingSmall = if (isTablet) 20.dp else 16.dp
        val buttonFontSize = if (isTablet) 16.sp else 13.sp
        val buttonHeight = if (isTablet) 70.dp else 60.dp
        val buttonWidth = if (isTablet) 280.dp else 240.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟡 Título
            Text(
                text = "¡Exploramos como Saludar con cariño!",
                color = Color(0xFF67644F),
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            // 🖼 Imagen ilustrativa
            Image(
                painter = painterResource(id = R.drawable.saludos),
                contentDescription = "Ilustración de saludos",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            // 📷 Botón RA saludos
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable {
                        val intent = Intent(context, SaludoActivity::class.java)
                        context.startActivity(intent)
                    }
                    .height(buttonHeight)
                    .width(buttonWidth),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Botón RA Saludos",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "\uD83E\uDDDA\u200D♀\uFE0F Aprende los saludos",
                    color = Color.White,
                    fontSize = buttonFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

            // 🔙 Botón Regresar
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
                    .fillMaxWidth(0.65f)
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
