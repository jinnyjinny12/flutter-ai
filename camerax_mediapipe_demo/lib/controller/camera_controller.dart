
import 'package:flutter/services.dart';

class CameraController {
  static const MethodChannel _channel =
  MethodChannel('com.example.camerax_mediapipe_demo/camera');

  Future<void> startCamera() async {
    try {
      final result = await _channel.invokeMethod('startCamera');
      print(result); // "Camera started"
    } catch (e) {
      print('Failed to start camera: $e');
    }
  }
}
