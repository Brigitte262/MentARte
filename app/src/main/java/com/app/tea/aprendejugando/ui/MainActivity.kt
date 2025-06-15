package com.app.tea.aprendejugando.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.app.tea.aprendejugando.ui.theme.AprendeJugandoTheme
import androidx.compose.ui.text.style.TextAlign
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.TextStyle

// Íconos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

// Sombra (shadow) desde Foundation
import androidx.compose.ui.draw.shadow

//base de datos
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AprendeJugandoTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                var showSuccessDialog by remember { mutableStateOf(false) }
                var nombreUsuario by remember { mutableStateOf("Usuario") }

                val context = this@MainActivity

                LoginScreen(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    onLoginClick = {
                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()

                        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
                            errorMessage = "Completa todos los campos por favor ✨"
                        } else if (trimmedEmail.contains(" ") || trimmedPassword.contains(" ")) {
                            errorMessage = "No uses espacios en blanco 🧸"
                        } else {
                            val auth = FirebaseAuth.getInstance()
                            auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid
                                        val db = FirebaseFirestore.getInstance()

                                        userId?.let {
                                            db.collection("usuarios").document(it).get()
                                                .addOnSuccessListener { document ->
                                                    nombreUsuario = document.getString("nombre") ?: "Usuario"
                                                    showSuccessDialog = true
                                                }
                                                .addOnFailureListener {
                                                    errorMessage = "No pudimos obtener tu nombre 😢"
                                                }
                                        }
                                    } else {
                                        errorMessage = "Correo o contraseña incorrectos ❌"
                                    }
                                }
                        }
                    },
                    onNavigateToRegister = {
                        val intent = Intent(context, RegisterActivity::class.java)
                        context.startActivity(intent)
                    },
                    showSuccessDialog = showSuccessDialog,
                    nombreUsuario = nombreUsuario,
                    errorMessage = errorMessage,
                    onDialogDismiss = {
                        showSuccessDialog = false
                        val intent = Intent(context, MenuCursosActivity::class.java)
                        intent.putExtra("nombreUsuario", nombreUsuario)
                        context.startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .background(Color(0xFF917C71), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        },
        trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
            {
                IconButton(onClick = { onTogglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        } else null,
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = TextStyle(color = Color.Black),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Gray,
            focusedBorderColor = Color(0xFF917C71),
            unfocusedLabelColor = Color.Gray,
            focusedLabelColor = Color(0xFF917C71),
            cursorColor = Color(0xFF917C71),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
    )
}


@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    showSuccessDialog: Boolean,
    nombreUsuario: String,
    errorMessage: String?,
    onDialogDismiss: () -> Unit
) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    BoxWithConstraints {
        val isTablet = this.maxWidth > 600.dp
        val contentPadding = if (isTablet) 48.dp else 24.dp
        val innerPadding = if (isTablet) 32.dp else 24.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFD4FFDD), Color(0xFF4C8970))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.cat_login),
                contentDescription = "Gatito",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(if (isTablet) 180.dp else 160.dp)
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(contentPadding)
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF9F0), shape = RoundedCornerShape(16.dp))
                    .padding(innerPadding)
                    .fillMaxWidth(if (isTablet) 0.6f else 1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Bienvenido a tu\nmundo mágico!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomInputField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Correo Electrónico",
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB8A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
                ) {
                    Text("Iniciar Sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes cuenta? Regístrate", color = Color.Gray)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showSuccessDialog) {
                MagicWelcomeDialog(nombreUsuario = nombreUsuario, onDismiss = onDialogDismiss)
                LaunchedEffect(showSuccessDialog) {
                    delay(2500)
                    onDialogDismiss()
                }
            }
        }
    }
}




@Composable
fun MagicWelcomeDialog(nombreUsuario: String, onDismiss: () -> Unit) {
    val visible = remember { mutableStateOf(true) }

    // ✅ Cargar la animación desde assets
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("magic_wand.lottie") // usa el nuevo nombre aquí
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.85f),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ✨ Animación mágica
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .height(120.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "¡Bienvenido/a $nombreUsuario!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Nos alegra verte de nuevo 🧸",
                    color = Color(0xFF444444),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            containerColor = Color(0xFFFFF2CC),
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(24.dp)
        )
    }

    LaunchedEffect(Unit) {
        delay(5000)
        visible.value = false
        onDismiss()
    }
}







