import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Pose Detection',
      home: PoseDetectionScreen(),
    );
  }
}

class PoseDetectionScreen extends StatefulWidget {
  @override
  _PoseDetectionScreenState createState() => _PoseDetectionScreenState();
}

class _PoseDetectionScreenState extends State<PoseDetectionScreen> {
  static const platform = MethodChannel('com.example.new_ml_flutter_app/pose');

  String _status = "Pose detection not started";

  Future<void> _startPoseDetection() async {
    try {
      final String result = await platform.invokeMethod("startPoseDetection");
      setState(() {
        _status = result;
      });
    } on PlatformException catch (e) {
      setState(() {
        _status = "Failed to start pose detection: ${e.message}";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Pose Detection'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_status),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _startPoseDetection,
              child: Text('Start Pose Detection'),
            ),
          ],
        ),
      ),
    );
  }
}
