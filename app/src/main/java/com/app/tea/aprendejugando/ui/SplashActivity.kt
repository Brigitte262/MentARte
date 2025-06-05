package com.app.tea.aprendejugando.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.tea.aprendejugando.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 🧠 Elementos visuales
        val logo = findViewById<ImageView>(R.id.splash_logo)
        val frase = findViewById<TextView>(R.id.splash_text)

        // 🎞️ Cargar animaciones personalizadas
        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val fadeInText = AnimationUtils.loadAnimation(this, R.anim.fade_in_text)

        // 🎬 Aplicarlas
        logo.startAnimation(zoomIn)
        frase.startAnimation(fadeInText)

        // ⏳ Transición al menú principal
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}