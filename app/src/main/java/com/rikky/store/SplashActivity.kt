package com.rikky.store

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = android.view.Gravity.CENTER
        layout.setBackgroundColor(Color.parseColor("#121212"))

        val title = TextView(this)
        title.text = "RikkY Store"
        title.textSize = 32f
        title.setTextColor(Color.parseColor("#FF6D00"))
        title.gravity = android.view.Gravity.CENTER
        title.setPadding(0, 40, 0, 0)

        val subtitle = TextView(this)
        subtitle.text = "Loading..."
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.parseColor("#AAAAAA"))
        subtitle.gravity = android.view.Gravity.CENTER
        subtitle.setPadding(0, 20, 0, 0)

        layout.addView(title)
        layout.addView(subtitle)
        setContentView(layout)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}
