import 'package:camera/camera.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:google_mlkit_face_detection/google_mlkit_face_detection.dart';

late List<CameraDescription> cameras;

class FaceLandmarkScreen extends StatefulWidget {
  @override
  _FaceLandmarkScreenState createState() => _FaceLandmarkScreenState();
}

class _FaceLandmarkScreenState extends State<FaceLandmarkScreen> {
  CameraController? _cameraController;
  FaceDetector? _faceDetector;
  bool _isDetectingFaces = false;
  List<Face> _faces = [];

  @override
  void initState() {
    super.initState();
    _initializeCamera();
    _initializeFaceDetector();
  }

  Future<void> _initializeCamera() async {
    cameras = await availableCameras();
    _cameraController = CameraController(cameras[0], ResolutionPreset.medium);
    await _cameraController?.initialize();
    setState(() {});

    _cameraController?.startImageStream((CameraImage image) {
      if (_isDetectingFaces) return;

      _isDetectingFaces = true;
      _processCameraImage(image).then((faces) {
        setState(() {
          _faces = faces;
        });
        _isDetectingFaces = false;
      });
    });
  }

  void _initializeFaceDetector() {
    _faceDetector = FaceDetector(
      options: FaceDetectorOptions(
        performanceMode: FaceDetectorMode.accurate,
        enableLandmarks: true,
      ),
    );
  }

  Future<List<Face>> _processCameraImage(CameraImage image) async {
    final WriteBuffer allBytes = WriteBuffer();
    for (final Plane plane in image.planes) {
      allBytes.putUint8List(plane.bytes);
    }
    final bytes = allBytes.done().buffer.asUint8List();

    final inputImage = InputImage.fromBytes(
      bytes: bytes,
      inputImageFormat: InputImageFormat.nv21,
      inputImageMetadata: InputImageMetadata(
        size: Size(image.width.toDouble(), image.height.toDouble()),
        rotation: InputImageRotation.rotation0deg, // 카메라 방향에 맞게 조정
        bytesPerRow: image.planes[0].bytesPerRow,
      ),
    );

    return await _faceDetector!.processImage(inputImage);
  }

  @override
  void dispose() {
    _cameraController?.dispose();
    _faceDetector?.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Face Detection with Landmarks'),
      ),
      body: _cameraController == null || !_cameraController!.value.isInitialized
          ? const Center(child: CircularProgressIndicator())
          : Stack(
        children: [
          CameraPreview(_cameraController!),
          CustomPaint(
            painter: FacePainter(_faces, _cameraController!.value.previewSize!),
          ),
        ],
      ),
    );
  }
}

class FacePainter extends CustomPainter {
  final List<Face> faces;
  final Size previewSize;

  FacePainter(this.faces, this.previewSize);

  @override
  void paint(Canvas canvas, Size size) {
    final double scaleX = size.width / previewSize.height;
    final double scaleY = size.height / previewSize.width;

    final paint = Paint()
      ..color = Colors.red
      ..strokeWidth = 2.0
      ..style = PaintingStyle.stroke;

    for (var face in faces) {
      // Draw bounding box
      final rect = Rect.fromLTRB(
        face.boundingBox.left * scaleX,
        face.boundingBox.top * scaleY,
        face.boundingBox.right * scaleX,
        face.boundingBox.bottom * scaleY,
      );
      canvas.drawRect(rect, paint);

      // Draw landmarks
      final landmarkPaint = Paint()
        ..color = Colors.blue
        ..strokeWidth = 4.0;

      face.landmarks?.values.forEach((landmark) {
        final Offset position = Offset(
          (landmark.x ?? 0) * scaleX,
          (landmark.y ?? 0) * scaleY,
        );
        canvas.drawCircle(position, 4.0, landmarkPaint);
      });
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
