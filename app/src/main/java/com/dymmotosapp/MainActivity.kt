package com.dymmotosapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var sendButton: Button
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        sendButton = findViewById(R.id.sendButton)

        handleIntent(intent)

        sendButton.setOnClickListener {
            imageUri?.let {
                runOCR(it)
            } ?: Toast.makeText(this, "No hay imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {

            imageUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }

            imageUri?.let {
                imageView.setImageURI(it)
            }
        }
    }

    private fun runOCR(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText: Text ->

                    val textoOCR = visionText.text

                    val ids = EntregaParser.extraerIds(textoOCR)
                    val viajes = EntregaParser.parseBloques(textoOCR, ids)

                    viajes.forEach {
                        Log.d("VIAJE", it.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error leyendo texto", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show()
        }
    }
}