package com.husnain.admincarbuddy.ui.fragments.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.husnain.admincarbuddy.data.ModelBookingData
import com.husnain.admincarbuddy.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VmOrders @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val currentUserUid = auth.currentUser?.uid!!

    private val _bookingData = MutableLiveData<DataState<List<ModelBookingData>>>()
    val bookingsData: LiveData<DataState<List<ModelBookingData>>> = _bookingData

    private val _ongoingBookings = MutableLiveData<DataState<List<ModelBookingData>>>()
    val ongoingBookings: LiveData<DataState<List<ModelBookingData>>> = _ongoingBookings

    private val _completedBookings = MutableLiveData<DataState<List<ModelBookingData>>>()
    val completedBookings: LiveData<DataState<List<ModelBookingData>>> = _completedBookings

    fun getPendingBookings() {
        _bookingData.value = DataState.Loading
        db.collection("bookings")
            .whereEqualTo("vendorUid", currentUserUid)
            .whereEqualTo("accepted", false)
            .whereEqualTo("bookingStatus", "Requested")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _bookingData.value = DataState.Error(error.localizedMessage!!)
                    return@addSnapshotListener
                }
                if (value != null) {
                    val bookingList = value.documents.mapNotNull { document ->
                        document.toObject(ModelBookingData::class.java)?.apply {
                            docId = document.id
                        }
                    }
                    _bookingData.value = DataState.Success(bookingList)
                } else {
                    _bookingData.value = DataState.Error("No data found")
                }
            }
    }

    fun getOngoingBookings() {
        _ongoingBookings.value = DataState.Loading
        db.collection("bookings")
            .whereEqualTo("accepted", true)
            .whereEqualTo("bookingStatus", "In progress")
            .whereEqualTo("vendorUid", currentUserUid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _ongoingBookings.value = DataState.Error(error.localizedMessage!!)
                    return@addSnapshotListener
                }

                val bookingList = value?.documents?.mapNotNull { document ->
                    document.toObject(ModelBookingData::class.java)?.apply {
                        docId = document.id
                    }
                }
                _ongoingBookings.value = DataState.Success(bookingList ?: emptyList())
            }
    }

    fun getCompletedBookings() {
        _completedBookings.value = DataState.Loading
        db.collection("bookings")
            .whereIn("bookingStatus", listOf("Completed", "Canceled"))
            .whereEqualTo("vendorUid", currentUserUid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _completedBookings.value = DataState.Error(error.localizedMessage!!)
                    return@addSnapshotListener
                }

                val bookingList = value?.documents?.mapNotNull { document ->
                    document.toObject(ModelBookingData::class.java)?.apply {
                        docId = document.id
                    }
                }
                _completedBookings.value = DataState.Success(bookingList ?: emptyList())
            }
    }
}