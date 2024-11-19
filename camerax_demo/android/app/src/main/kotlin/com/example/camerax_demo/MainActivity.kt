package com.example.camerax_demo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugin.common.StandardMessageCodec

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.camerax_demo/camera"
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "startCamera") {
                if (checkCameraPermission()) {
                    result.success("Camera started")
                } else {
                    requestCameraPermission()
                    result.error("PERMISSION_DENIED", "Camera permission denied", null)
                }
            } else {
                result.notImplemented()
            }
        }
        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory("camera_preview", CameraPreviewFactory())
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "카메라 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class CameraPreviewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, id: Int, args: Any?): PlatformView {
        return CameraPreview(context)
    }
}

class CameraPreview(context: Context) : PlatformView, LifecycleOwner {
    private val frameLayout: FrameLayout = FrameLayout(context)
    private val previewView: androidx.camera.view.PreviewView = androidx.camera.view.PreviewView(context)
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        frameLayout.addView(previewView)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        if (checkCameraPermission(context)) {
            startCamera(context)
        } else {
            Log.e("CameraPreview", "카메라 권한이 없습니다.")
        }
    }

    private fun checkCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(context: Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            } catch (e: Exception) {
                Log.e("CameraPreview", "카메라 초기화 실패: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun getView(): View = frameLayout

    override fun dispose() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}
