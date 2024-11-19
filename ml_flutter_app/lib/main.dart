import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const platform = MethodChannel('com.example.ml_flutter_app/camera');

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('CameraX with TFLite')),
        body: Center(
          child: ElevatedButton(
            onPressed: () async {
              try {
                final result = await platform.invokeMethod('startCamera');
                print(result);
              } catch (e) {
                print("Error: $e");
              }
            },
            child: const Text('Start Camera'),
          ),
        ),
      ),
    );
  }
}
