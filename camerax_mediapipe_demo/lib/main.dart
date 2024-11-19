import 'package:flutter/material.dart';
import 'controller/camera_controller.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CameraX MediaPipe Demo',
      home: CameraPage(),
    );
  }
}

class CameraPage extends StatelessWidget {
  final CameraController _cameraController = CameraController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('CameraX MediaPipe Demo'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
            await _cameraController.startCamera();
          },
          child: Text('Start Camera'),
        ),
      ),
    );
  }
}
