package com.app.tea.aprendejugando.ui.componentes

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tea.aprendejugando.R

@Composable
fun contenidoCard(
    nombre: String,
    icono: Int,
    context: Context,
    destino: Class<*>?,
    isTablet: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(if (isTablet) 160.dp else 130.dp)
            .height(if (isTablet) 170.dp else 140.dp)
            .shadow(8.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .background(Color(0xFFFFF6E6), shape = RoundedCornerShape(20.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                val intent = Intent(context, destino)
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(if (isTablet) 100.dp else 80.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nombre,
                color = Color.Black,
                fontSize = if (isTablet) 16.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
