package com.capstone.attirely.helper

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.capstone.attirely.R
import com.capstone.attirely.helper.ImageClassifierHelper
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.io.File

class ModelDownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            downloadAndSetupModel("color_model")
            downloadAndSetupModel("type_model")
            Result.success()
        } catch (e: Exception) {
            Log.e("ModelDownloadWorker", "Model download failed", e)
            Result.failure()
        }
    }

    private fun downloadAndSetupModel(modelFileName: String) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel(modelFileName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile: File? = model?.file
                if (modelFile != null) {
                    Log.d("ModelDownloadWorker", "Model $modelFileName downloaded successfully")
                    // Perform any additional setup if necessary
                } else {
                    Log.e("ModelDownloadWorker", "Failed to download model $modelFileName")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ModelDownloadWorker", "Model $modelFileName download failed", exception)
            }
    }
}