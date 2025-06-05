package com.app.tea.aprendejugando.ui

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder



class AnimalClassifier(context: Context) {

    private var interpreter: Interpreter
    private val labels: List<String>

    init {
        val model = context.assets.open("model_unquant.tflite").readBytes()
        val buffer = ByteBuffer.allocateDirect(model.size).apply {
            order(ByteOrder.nativeOrder())
            put(model)
            rewind()
        }
        interpreter = Interpreter(buffer, Interpreter.Options())


        val labelStream = context.assets.open("labels.txt")
        val reader = BufferedReader(InputStreamReader(labelStream))
        labels = reader.readLines()
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(224 * 224)
        resized.getPixels(pixels, 0, 224, 0, 0, 224, 224)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        val output = TensorBuffer.createFixedSize(
            intArrayOf(1, labels.size),
            org.tensorflow.lite.DataType.FLOAT32
        )
        interpreter.run(byteBuffer, output.buffer.rewind())

        val scores = output.floatArray
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1
        val confidence = scores[maxIndex]

        val label = if (maxIndex != -1) labels[maxIndex] else "Desconocido"
        return Pair(label, confidence)
    }


}
