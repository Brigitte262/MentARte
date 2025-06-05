package com.app.tea.aprendejugando.ui

import android.os.Bundle
import android.content.Intent // ✅ Necesario para cambiar de pantalla
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext // ✅ Para obtener el contexto actual
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.app.tea.aprendejugando.R
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

class ScreeMatematica : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatematicaScreen()
        }
    }
}

@Composable
fun MatematicaScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2FFD2)) // Fondo principal
    ) {
        val isTablet = this.maxWidth > 600.dp
        val padding = if (isTablet) 40.dp else 24.dp
        val titleFontSize = if (isTablet) 36.sp else 30.sp
        val lineHeight = if (isTablet) 44.sp else 35.sp
        val imageHeight = if (isTablet) 280.dp else 220.dp
        val spacingLarge = if (isTablet) 32.dp else 24.dp
        val spacingSmall = if (isTablet) 20.dp else 16.dp
        val buttonFontSize = if (isTablet) 18.sp else 16.sp
        val buttonWidth = if (isTablet) 280.dp else 220.dp
        val catSize = if (isTablet) 140.dp else 100.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "¡Explora el mundo\nde la Matemática!",
                fontSize = titleFontSize,
                lineHeight = lineHeight,
                color = Color(0xFF67644F),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacingSmall))

            Image(
                painter = painterResource(id = R.drawable.ic_matematica),
                contentDescription = "Imagen curso matemática",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacingLarge))

            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp), clip = false)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        val intent = Intent(context, ScreenContenidoMatematica::class.java)
                        context.startActivity(intent)
                    }
                    .height(48.dp)
                    .width(buttonWidth)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Iniciar Actividad",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "Iniciar Actividad",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = buttonFontSize,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(spacingSmall))

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

        // Gato decorativo ajustado
        Image(
            painter = painterResource(id = R.drawable.cat_mate),
            contentDescription = "Decoración gato matemático",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(catSize)
        )
    }
}

