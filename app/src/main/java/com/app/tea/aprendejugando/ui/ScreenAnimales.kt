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

class ScreenAnimales : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimalesScreen()
        }
    }
}

@Composable
fun AnimalesScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB7F9D8)) // 🎨 Fondo verde agua
    ) {
        val isTablet = this.maxWidth > 600.dp

        // Variables adaptativas
        val padding = if (isTablet) 40.dp else 24.dp
        val titleFontSize = if (isTablet) 36.sp else 30.sp
        val lineHeight = if (isTablet) 44.sp else 36.sp
        val imageHeight = if (isTablet) 280.dp else 240.dp
        val spacingLarge = if (isTablet) 32.dp else 24.dp
        val spacingSmall = if (isTablet) 20.dp else 16.dp
        val buttonFontSize = if (isTablet) 18.sp else 16.sp
        val buttonWidth = if (isTablet) 280.dp else 220.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🟡 Título
            Text(
                text = "¡Conoce a los animalitos!",
                color = Color(0xFF67644F),
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            // 🖼 Imagen ilustrativa
            Image(
                painter = painterResource(id = R.drawable.animales),
                contentDescription = "Ilustración de animal",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            // 📷 Botón con imagen - Ver animales
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        val intent = Intent(context, AnimalActivity::class.java)
                        context.startActivity(intent)
                    }
                    .height(56.dp)
                    .width(buttonWidth),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Botón RA Planeta",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "\uD83D\uDC38 Ver Animales",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = buttonFontSize,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(spacingLarge))

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
