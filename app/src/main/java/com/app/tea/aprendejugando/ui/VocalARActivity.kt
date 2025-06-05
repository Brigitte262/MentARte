package com.app.tea.aprendejugando.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class VocalARActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val htmlUrl = intent.getStringExtra("url") ?: "file:///android_asset/vocal_a.html"

        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            webViewClient = WebViewClient()
            loadUrl(htmlUrl)
        }

        setContentView(webView)
    }
}



