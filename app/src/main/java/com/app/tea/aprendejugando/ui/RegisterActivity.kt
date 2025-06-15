package com.app.tea.aprendejugando.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.ui.unit.dp
import com.app.tea.aprendejugando.ui.theme.AprendeJugandoTheme
import androidx.compose.ui.text.style.TextAlign
import com.app.tea.aprendejugando.R
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.shadow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn


// Íconos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AprendeJugandoTheme {
                val context = this@RegisterActivity
                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
                var showSuccessDialog by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    RegisterScreen(
                        name = name,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        onNameChange = { name = it },
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onRegisterClick = {
                            val trimmedName = name.trim()
                            val trimmedEmail = email.trim()
                            val trimmedPassword = password.trim()
                            val trimmedConfirm = confirmPassword.trim()

                            when {
                                trimmedName.isEmpty() || trimmedEmail.isEmpty() ||
                                        trimmedPassword.isEmpty() || trimmedConfirm.isEmpty() ->
                                    errorMessage = "Por favor, completa todos los campos 🧩"
                                trimmedName.contains(" ") || trimmedEmail.contains(" ") ||
                                        trimmedPassword.contains(" ") || trimmedConfirm.contains(" ") ->
                                    errorMessage = "No uses espacios en blanco ✨"
                                trimmedPassword != trimmedConfirm ->
                                    errorMessage = "Las contraseñas no coinciden 🛑"
                                else -> {
                                    val auth = FirebaseAuth.getInstance()
                                    val db = FirebaseFirestore.getInstance()
                                    auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val userId = auth.currentUser?.uid
                                                val usuario = hashMapOf(
                                                    "nombre" to trimmedName,
                                                    "correo" to trimmedEmail
                                                )
                                                userId?.let {
                                                    db.collection("usuarios").document(it).set(usuario)
                                                        .addOnSuccessListener {
                                                            showSuccessDialog = true
                                                            Log.d("DEBUG", "✅ Dialog activado")
                                                        }
                                                        .addOnFailureListener {
                                                            errorMessage = "No pudimos guardar tus datos 😢"
                                                        }
                                                }
                                            } else {
                                                val exception = task.exception
                                                errorMessage = if (exception is FirebaseAuthUserCollisionException) {
                                                    "Este correo ya está registrado. Inicia sesión 📧"
                                                } else {
                                                    "Error: ${exception?.message}"
                                                }
                                            }
                                        }
                                }
                            }
                        },
                        onNavigateToLogin = { finish() },
                        errorMessage = errorMessage,
                        showSuccessDialog = showSuccessDialog,
                        onDismissSuccessDialog = { showSuccessDialog = false },
                        onTestPopup = {
                            showSuccessDialog = true
                        }
                    )

                    // ✅ Aquí sí se dibuja el diálogo cuando se activa
                    if (showSuccessDialog) {
                        MagicRegisterSuccessDialog(
                            userName = name,
                            onDismiss = { showSuccessDialog = false },
                            onNavigateToLogin = {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                finish()
                            }
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun RegisterScreen(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    errorMessage: String?,
    showSuccessDialog: Boolean = false,
    onDismissSuccessDialog: () -> Unit = {},
    onTestPopup: () -> Unit = {} // ✅ nuevo parámetro
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Log.d("RegisterScreen", "showSuccessDialog: $showSuccessDialog")

    BoxWithConstraints {
        val isTablet = this.maxWidth > 600.dp
        val contentPadding = if (isTablet) 48.dp else 24.dp
        val innerPadding = if (isTablet) 32.dp else 24.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFD4FFDD), Color(0xFF4C8970))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.cat_register),
                contentDescription = "Gatito registro",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(if (isTablet) 160.dp else 120.dp)
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
                    text = "¡Crea tu cuenta mágica!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomInputField(name, onNameChange, "Nombre", Icons.Default.Person)
                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(email, onEmailChange, "Correo Electrónico", Icons.Default.Email)
                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(password, onPasswordChange, "Contraseña", Icons.Default.Lock,
                    isPassword = true, passwordVisible, { passwordVisible = !passwordVisible })
                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(confirmPassword, onConfirmPasswordChange, "Confirmar", Icons.Default.Lock,
                    isPassword = true, confirmPasswordVisible, { confirmPasswordVisible = !confirmPasswordVisible })
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTestPopup, // ✅ ahora usa esto para mostrar el diálogo
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DBB8A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
                ) {
                    Text("Registrarme", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = Color.Gray)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


@Composable
fun MagicRegisterSuccessDialog(
    userName: String,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val visible = remember { mutableStateOf(true) }

    // 🔄 Lottie animation setup
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("Hello.lottie"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.85f),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = {
                    visible.value = false
                    onNavigateToLogin()
                }) {
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    // ✨ Animación mágica
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .height(130.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "¡Registro exitoso, $userName!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Tu cuenta ha sido creada con éxito. ¡Bienvenido a la aventura! 🧸",
                    color = Color(0xFF444444),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color(0xFFFFF2CC), // más mágica con transparencia
            tonalElevation = 8.dp,
            modifier = Modifier.padding(24.dp)
        )
    }

    // ⏳ Desaparece automáticamente luego de unos segundos
    LaunchedEffect(Unit) {
        delay(4010)
        visible.value = false
        onDismiss()
        onNavigateToLogin()
    }
}















