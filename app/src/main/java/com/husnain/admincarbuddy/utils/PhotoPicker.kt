package com.husnain.admincarbuddy.utils

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object PhotoPicker {

    fun Fragment.setupPhotoPicker(onImagePicked: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
        return registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                onImagePicked(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    fun Fragment.openPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}