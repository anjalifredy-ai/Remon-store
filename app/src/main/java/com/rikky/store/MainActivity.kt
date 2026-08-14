package com.rikky.store

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    // JS that runs after every page load - replaces Play Store text/branding
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

                // watch for dynamically loaded content (Play Store is JS-heavy)
                var observer = new MutationObserver(function(mutations) {
                    mutations.forEach(function(m) {
                        m.addedNodes.forEach(function(n) {
                            replaceText(n);
                        });
                    });
                });
                observer.observe(document.body, { childList: true, subtree: true });

                // try replacing document title too
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
        webView.settings.userAgentString = webView.settings.userAgentString + " RikkYStore/1.0"

        webView.webViewClient = object : WebViewClient() {
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
