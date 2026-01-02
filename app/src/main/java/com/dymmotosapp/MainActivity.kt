package com.dymmotosapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)

        if (intent?.action == android.content.Intent.ACTION_SEND) {
            val imageUri: Uri? = intent.getParcelableExtra(android.content.Intent.EXTRA_STREAM)

            if (imageUri != null) {
                imageView.setImageURI(imageUri)
            }
        }
    }
}