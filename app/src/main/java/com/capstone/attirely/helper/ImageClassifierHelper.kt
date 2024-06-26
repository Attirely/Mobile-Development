package com.capstone.attirely.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.capstone.attirely.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassifierHelper(
    private val context: Context,
    private val listener: ClassifierListener?,
    private val colorModelFileName: String,
    private val typeModelFileName: String
) {

    private var colorInterpreter: Interpreter? = null
    private var typeInterpreter: Interpreter? = null
    private var colorInputImageWidth = 0
    private var colorInputImageHeight = 0
    private var colorInputImageChannels = 0
    private var typeInputImageWidth = 0
    private var typeInputImageHeight = 0
    private var typeInputImageChannels = 0
    private var colorModelReady = false
    private var typeModelReady = false

    init {
        Log.d(TAG, "Initializing ImageClassifierHelper")
        setupInterpreter(colorModelFileName) { interpreter ->
            colorInterpreter = interpreter
            colorModelReady = true
            checkIfBothModelsReady()
        }
        setupInterpreter(typeModelFileName) { interpreter ->
            typeInterpreter = interpreter
            typeModelReady = true
            checkIfBothModelsReady()
        }
    }

    private fun setupInterpreter(modelFileName: String, setupInterpreter: (Interpreter) -> Unit) {
        Log.d(TAG, "Setting up model: $modelFileName")
        val modelFile = loadModelFile(context, modelFileName)
        if (modelFile != null) {
            setupInterpreter(createInterpreter(modelFile, modelFileName == colorModelFileName))
        } else {
            Log.e(TAG, "Failed to load model $modelFileName")
            listener?.onFailure(context.getString(R.string.classifier_failed))
        }
    }

    private fun loadModelFile(context: Context, modelFileName: String): ByteBuffer? {
        return try {
            val assetFileDescriptor = context.assets.openFd(modelFileName)
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading model file $modelFileName", e)
            null
        }
    }

    private fun createInterpreter(modelFile: ByteBuffer, isColorModel: Boolean): Interpreter {
        Log.d(TAG, "Creating interpreter for ${if (isColorModel) "color" else "type"} model")
        val options = Interpreter.Options().apply {
            setNumThreads(numThreads)
        }
        val interpreter = Interpreter(modelFile, options)

        val inputTensor = interpreter.getInputTensor(0)
        val inputShape = inputTensor.shape()
        if (isColorModel) {
            colorInputImageHeight = inputShape[1]
            colorInputImageWidth = inputShape[2]
            colorInputImageChannels = inputShape[3]
        } else {
            typeInputImageHeight = inputShape[1]
            typeInputImageWidth = inputShape[2]
            typeInputImageChannels = inputShape[3]
        }
        Log.d(TAG, "Interpreter created with input shape: ${inputShape.contentToString()}")
        return interpreter
    }

    private fun checkIfBothModelsReady() {
        if (colorModelReady && typeModelReady) {
            Log.d(TAG, "Both models are ready")
            listener?.onModelReady()
        }
    }

    val isInterpreterReady: Boolean
        get() = colorModelReady && typeModelReady

    fun classifyImage(imageUri: Uri, callback: (colorResult: ClassificationResult?, typeResult: ClassificationResult?) -> Unit) {
        if (!isInterpreterReady) {
            Log.e(TAG, "Interpreters are not ready")
            listener?.onFailure(context.getString(R.string.classifier_failed))
            callback(null, null)
            return
        }

        val colorByteBuffer = preprocessImageForColor(context, imageUri)
        val typeByteBuffer = preprocessImageForType(context, imageUri)

        val colorResult = classifyColor(colorByteBuffer)
        val typeResult = classifyType(typeByteBuffer)

        if (colorResult != null && typeResult != null) {
            listener?.onSuccess(listOf(colorResult, typeResult))
        } else {
            listener?.onFailure(context.getString(R.string.classifier_failed))
        }
        callback(colorResult, typeResult)
    }

    private fun classifyColor(byteBuffer: ByteBuffer): ClassificationResult? {
        if (colorInterpreter == null) {
            Log.e(TAG, "Color interpreter is not ready")
            listener?.onFailure(context.getString(R.string.classifier_failed))
            return null
        }

        val outputShape = colorInterpreter!!.getOutputTensor(0).shape()
        val outputBuffer = Array(outputShape[0]) { FloatArray(outputShape[1]) }

        return try {
            Log.d(TAG, "Running color classification")
            colorInterpreter?.run(byteBuffer, outputBuffer)
            logClassificationResults(outputBuffer[0], "Color", ::mapColorClass)
            outputBuffer[0].withIndex().maxByOrNull { it.value }?.let {
                ClassificationResult(mapColorClass(it.index), it.value).also {
                    Log.d(TAG, "Color classification result: ${it.label} with confidence ${it.score}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during color classification", e)
            null
        }
    }

    private fun classifyType(byteBuffer: ByteBuffer): ClassificationResult? {
        if (typeInterpreter == null) {
            Log.e(TAG, "Type interpreter is not ready")
            return null
        }

        val outputShape = typeInterpreter!!.getOutputTensor(0).shape()
        val outputBuffer = Array(outputShape[0]) { FloatArray(outputShape[1]) }

        return try {
            Log.d(TAG, "Running type classification")
            typeInterpreter?.run(byteBuffer, outputBuffer)
            logClassificationResults(outputBuffer[0], "Type", ::mapClothClass)
            outputBuffer[0].withIndex().maxByOrNull { it.value }?.let {
                ClassificationResult(mapClothClass(it.index), it.value).also {
                    Log.d(TAG, "Type classification result: ${it.label} with confidence ${it.score}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during type classification", e)
            null
        }
    }

    private fun preprocessImageForType(context: Context, imageUri: Uri): ByteBuffer {
        val bitmap = loadImageBitmap(context, imageUri)
        val resizedBitmap = resizeBitmap(bitmap, 256, 256)
        return convertBitmapToByteBufferWithScaling(resizedBitmap, 256, 256, 3)
    }

    private fun preprocessImageForColor(context: Context, imageUri: Uri): ByteBuffer {
        Log.d(TAG, "Preprocessing image for color model")
        val bitmap = loadImageBitmap(context, imageUri)
        val resizedBitmap = resizeBitmap(bitmap, 28, 28)
        return convertBitmapToByteBufferWithScaling(resizedBitmap, 28, 28, 3).also {
            Log.d(TAG, "Image preprocessed for color model")
        }
    }

    @Throws(IOException::class)
    fun loadImageBitmap(context: Context, imageUri: Uri): Bitmap {
        Log.d(TAG, "Loading image from URI: $imageUri")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.also {
            Log.d(TAG, "Image loaded successfully from URI: $imageUri")
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        Log.d(TAG, "Resizing bitmap to $newWidth x $newHeight")
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true).also {
            Log.d(TAG, "Bitmap resized successfully")
        }
    }

    private fun convertBitmapToByteBufferWithScaling(bitmap: Bitmap, width: Int, height: Int, channels: Int): ByteBuffer {
        val inputImageBuffer = ByteBuffer.allocateDirect(width * height * channels * 4)
        inputImageBuffer.order(ByteOrder.nativeOrder())
        for (i in 0 until height) {
            for (j in 0 until width) {
                val pixel = bitmap.getPixel(j, i)
                val r = (pixel shr 16 and 0xFF) / 255.0f
                val g = (pixel shr 8 and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f
                inputImageBuffer.putFloat(r)
                inputImageBuffer.putFloat(g)
                inputImageBuffer.putFloat(b)
            }
        }
        return inputImageBuffer
    }

    private fun logClassificationResults(results: FloatArray, modelType: String, labelMapper: (Int) -> String) {
        Log.d(TAG, "$modelType classification percentages:")
        results.forEachIndexed { index, confidence ->
            Log.d(TAG, "${labelMapper(index)}: ${confidence * 100}%")
        }
    }

    private fun mapColorClass(index: Int): String {
        return when (index) {
            0 -> "Black"
            1 -> "Blue"
            2 -> "Brown"
            3 -> "Green"
            4 -> "Grey"
            5 -> "Pink"
            6 -> "Red"
            7 -> "White"
            else -> "Yellow"
        }
    }

    private fun mapClothClass(index: Int): String {
        return when (index) {
            0 -> "Dress"
            1 -> "Hoodie"
            2 -> "Pants"
            3 -> "Shirt"
            4 -> "Shorts"
            5 -> "Skirt"
            6 -> "Suit"
            else -> "Unknown"
        }
    }

    interface ClassifierListener {
        fun onFailure(error: String)
        fun onSuccess(results: List<ClassificationResult>?)
        fun onModelReady()
    }

    data class ClassificationResult(
        val label: String,
        val score: Float
    )

    companion object {
        private const val TAG = "ImageClassifierHelper"
        private const val numThreads = 4
    }
}