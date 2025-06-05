package com.app.tea.aprendejugando.ui

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tea.aprendejugando.R
import com.app.tea.aprendejugando.ui.componentes.contenidoCard
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width

class ScreenContenidoHabilidades : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContenidoHabilidadScreen()
        }
    }
}

@Composable
fun ContenidoHabilidadScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5AB)) // Fondo amarillo pastel
    ) {
        val isTablet = this.maxWidth > 600.dp
        val spacing = if (isTablet) 40.dp else 25.dp
        val paddingGeneral = if (isTablet) 50.dp else 30.dp
        val titleFontSize = if (isTablet) 32.sp else 24.sp
        val catSize = if (isTablet) 150.dp else 110.dp
        val buttonFontSize = if (isTablet) 18.sp else 16.sp
        val buttonWidthFraction = if (isTablet) 0.45f else 0.65f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingGeneral)
        ) {
            Text(
                text = "¡Diviértete con Habilidades Sociales!",
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF67644F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            // Fila de tarjetas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                contenidoCard("Emociones", R.drawable.emociones, context, ScreenEmociones::class.java, isTablet)
                contenidoCard("Saludos", R.drawable.saludos, context, ScreenSaludos::class.java, isTablet)
            }

            Spacer(modifier = Modifier.height(spacing))

            contenidoCard("Rutinas", R.drawable.rutinas, context, ScreenRutinas::class.java, isTablet)

            Spacer(modifier = Modifier.height(spacing))

            // Botón regresar
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
                    .fillMaxWidth(buttonWidthFraction)
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
                    fontSize = buttonFontSize,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(spacing))

            // Decoración gato
            Image(
                painter = painterResource(id = R.drawable.gat_habi),
                contentDescription = "Decoración gato habilidades",
                modifier = Modifier
                    .align(Alignment.Start)
                    .graphicsLayer(scaleX = -1f)
                    .size(catSize)
            )
        }
    }
}
