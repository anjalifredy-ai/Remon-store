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

            function hideLogoText() {
                // common header/nav text elements near the Play logo
                var selectors = ['header', 'nav', 'h1', 'div[role="banner"]'];
                selectors.forEach(function(sel) {
                    document.querySelectorAll(sel).forEach(function(el) {
                        var text = el.textContent || '';
                        if (text.indexOf('Google Play') !== -1) {
                            el.querySelectorAll('span, div, p, a').forEach(function(child) {
                                if (child.children.length === 0 &&
                                    child.textContent.trim().match(/^Google Play$/i)) {
                                    child.style.visibility = 'hidden';
                                    child.style.position = 'relative';
                                    var overlay = document.createElement('span');
                                    overlay.textContent = 'RikkY Store';
                                    overlay.style.position = 'absolute';
                                    overlay.style.left = '0';
                                    overlay.style.top = '0';
                                    overlay.style.visibility = 'visible';
                                    overlay.style.color = '#FF6D00';
                                    overlay.style.fontWeight = 'bold';
                                    overlay.style.whiteSpace = 'nowrap';
                                    child.parentNode.appendChild(overlay);
                                }
                            });
                        }
                    });
                });
            }

            try {
                replaceText(document.body);
                hideLogoText();

                var observer = new MutationObserver(function(mutations) {
                    mutations.forEach(function(m) {
                        m.addedNodes.forEach(function(n) {
                            replaceText(n);
                        });
                    });
                    hideLogoText();
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
                    false
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
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
