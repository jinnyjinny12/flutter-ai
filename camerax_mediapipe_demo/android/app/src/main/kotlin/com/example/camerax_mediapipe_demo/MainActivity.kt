package com.example.camerax_mediapipe_demo

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import androidx.camera.lifecycle.ProcessCameraProvider
import android.util.Log
import androidx.core.content.ContextCompat

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.camerax_mediapipe_demo/camera"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method == "startCamera") {
                    startCamera()
                    result.success("Camera started")
                } else {
                    result.notImplemented()
                }
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                Log.d("MainActivity", "Camera initialized successfully")
                // 추가적인 CameraX 설정 및 MediaPipe 통합 코드 작성
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to initialize camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
