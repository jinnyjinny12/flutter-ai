package com.example.ml_flutter_app

import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LifecycleOwner
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.ml_flutter_app/camera"
    private lateinit var interpreter: Interpreter

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startCamera" -> {
                    startCamera()
                    result.success("Camera started")
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val imageAnalysis = ImageAnalysis.Builder().build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
                processImage(image)
                image.close()
            }

            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(image: ImageProxy) {
        if (!::interpreter.isInitialized) {
            val model = assets.open("model.tflite").use { inputStream ->
                val modelBytes = inputStream.readBytes()
                ByteBuffer.wrap(modelBytes)
            }
            interpreter = Interpreter(model)
        }

        val byteBuffer = convertImageToByteBuffer(image)
        val output = Array(1) { FloatArray(1) }
        interpreter.run(byteBuffer, output)
        println("Inference Result: ${output[0][0]}")
    }

    private fun convertImageToByteBuffer(image: ImageProxy): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(224 * 224 * 3)
        return buffer
    }
}
