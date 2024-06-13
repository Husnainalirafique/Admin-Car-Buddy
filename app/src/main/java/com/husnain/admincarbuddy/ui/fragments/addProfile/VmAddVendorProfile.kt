package com.husnain.admincarbuddy.ui.fragments.addProfile

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.husnain.admincarbuddy.data.ModelVendorProfile
import com.husnain.admincarbuddy.utils.DataState
import com.husnain.admincarbuddy.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class VmAddVendorProfile @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageReference,
    private val context: Context
) : ViewModel() {
    private val _addVendorStatus = MutableLiveData<DataState<Nothing>>()
    val addVendorStatus: LiveData<DataState<Nothing>> = _addVendorStatus

    fun addVendor(vendorProfile: ModelVendorProfile) {
        _addVendorStatus.value = DataState.Loading
        val ref = storage.child("vendor_profile_images/${System.currentTimeMillis()}")

        val bitmap = ImageUtils.uriToBitmap(context, vendorProfile.vendorImage.toUri())
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        ref.putBytes(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener { uri1 ->
                        vendorProfile.vendorImage = uri1.toString()
                        addMechanicProfile(vendorProfile)
                    }
                } else {
                    _addVendorStatus.value = DataState.Error(it.exception?.localizedMessage!!)
                }
            }
    }

    private fun addMechanicProfile(profile: ModelVendorProfile) {
        db.collection("mechanics").document(profile.vendorUid).set(profile)
            .addOnSuccessListener {
                _addVendorStatus.value = DataState.Success()
            }
            .addOnFailureListener {
                _addVendorStatus.value = DataState.Error(it.localizedMessage!!)
            }
    }

}