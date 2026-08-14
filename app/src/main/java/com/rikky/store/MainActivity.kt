package com.rikky.store

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val brandingInjectionJS = """
        (function() {
            function replaceText(node) {
                if (node.nodeType === 3) {
                    var t = node.nodeValue;
                    if (t && (t.indexOf('Google Play') !== -1 || t.indexOf('Play Store') !== -1)) {
                        node.nodeValue = t
                            .replace(/Google Play Store/gi, 'RikkY Store')
                            .replace(/Google Play/gi, 'RikkY Store')
                            .replace(/Play Store/gi, 'RikkY Store');
                    }
                } else {
                    for (var i = 0; i < node.childNodes.length; i++) {
                        replaceText(node.childNodes[i]);
                    }
                }
            }
            try {
                replaceText(document.body);
                var observer = new MutationObserver(function(mutations) {
                    mutations.forEach(function(m) {
                        m.addedNodes.forEach(function(n) {
                            replaceText(n);
                        });
                    });
                });
                observer.observe(document.body, { childList: true, subtree: true });
                if (document.title) {
                    document.title = document.title
                        .replace(/Google Play Store/gi, 'RikkY Store')
                        .replace(/Google Play/gi, 'RikkY Store')
                        .replace(/Play Store/gi, 'RikkY Store');
                }
            } catch(e) {}
        })();
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.setSupportZoom(false)
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 13; Pixel) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                return if (url.startsWith("http://") || url.startsWith("https://")) {
                    false // let WebView load it normally
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        // no app to handle it, ignore
                    }
                    true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.evaluateJavascript(brandingInjectionJS, null)
            }
        }

        webView.loadUrl("https://play.google.com/store/apps")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
