package com.app.tea.aprendejugando.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.app.tea.aprendejugando.R
import androidx.activity.OnBackPressedCallback


class RegistroEmergenteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registro_emergente)

        // ✅ Bloquear botón atrás sin usar código deprecado
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hacemos nada: bloqueamos manualmente el cierre
            }
        })

        // ✅ Cerrar automáticamente luego de 2.5 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2500)
    }
}