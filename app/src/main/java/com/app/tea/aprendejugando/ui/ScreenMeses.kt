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

class ScreenMeses : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MesesSimpleScreen()
        }
    }
}

@Composable
fun MesesSimpleScreen() {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6D3)) // 🎨 Fondo beige
    ) {
        val isTablet = this.maxWidth > 600.dp
        val horizontalPadding = if (isTablet) 64.dp else 24.dp
        val titleFontSize = if (isTablet) 36.sp else 30.sp
        val lineHeight = if (isTablet) 44.sp else 36.sp
        val imageHeight = if (isTablet) 320.dp else 240.dp
        val spacing = if (isTablet) 40.dp else 32.dp
        val buttonFontSize = if (isTablet) 18.sp else 15.sp
        val buttonWidth = if (isTablet) 0.45f else 0.65f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Conoce los Meses del Año!",
                color = Color(0xFF67644F),
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                lineHeight = lineHeight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing))

            Image(
                painter = painterResource(id = R.drawable.meses),
                contentDescription = "Ilustración meses del año",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(imageHeight)
            )

            Spacer(modifier = Modifier.height(spacing))

            Box(
                modifier = Modifier
                    .height(70.dp)
                    .clickable {
                        val intent = Intent(context, ScreenOCRMeses::class.java)
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.boton),
                    contentDescription = "Botón RA meses",
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "📅 Aprende los meses",
                    color = Color.White,
                    fontSize = buttonFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacing))

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

