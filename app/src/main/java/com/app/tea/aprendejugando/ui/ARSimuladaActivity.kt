package com.app.tea.aprendejugando.ui
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class ARSimuladaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webViewClient = WebViewClient()

        val vocal = intent.getStringExtra("vocal") ?: "a"
        val url = "file:///android_asset/aframe/ar_aframe.html?vocal=$vocal"
        webView.loadUrl(url)

        setContentView(webView)
    }
}
