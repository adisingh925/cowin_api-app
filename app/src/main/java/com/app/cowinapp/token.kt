package com.app.cowinapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.widget.TextView

class token : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)

        val textview = findViewById<TextView>(R.id.textview)

        val token = intent.getStringExtra("message_key").toString()

        textview.text = token
    }
}