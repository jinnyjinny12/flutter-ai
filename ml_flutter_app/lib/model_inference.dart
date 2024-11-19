import 'package:tflite_flutter/tflite_flutter.dart';

Future<String> runInference() async {
  try {
    // TensorFlow Lite Interpreter 로드
    final interpreter = await Interpreter.fromAsset('model.tflite');

    // 입력 데이터와 출력 데이터 준비
    var input = List.filled(224 * 224 * 3, 0.0).reshape([1, 224, 224, 3]);
    var output = List.filled(1 * 1001, 0.0).reshape([1, 1001]);

    // 추론 실행
    interpreter.run(input, output);

    // 결과 반환
    return output.toString();
  } catch (e) {
    return 'Error: $e';
  }
}
