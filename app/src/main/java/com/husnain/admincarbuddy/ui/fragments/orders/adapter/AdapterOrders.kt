package com.husnain.admincarbuddy.ui.fragments.orders.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.carbuddy.utils.DateTimeUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.data.ModelBookingData
import com.husnain.admincarbuddy.databinding.ItemBookingBinding
import com.husnain.admincarbuddy.utils.Dialogs
import com.husnain.admincarbuddy.utils.Glide
import com.husnain.admincarbuddy.utils.LatLngUtil
import com.husnain.admincarbuddy.utils.gone
import com.husnain.admincarbuddy.utils.onClick
import com.husnain.admincarbuddy.utils.visible

class AdapterOrders(private val items: List<ModelBookingData>) :
    RecyclerView.Adapter<AdapterOrders.ViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.bind(data)
    }

    inner class ViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ModelBookingData) {

            when{
                data.bookingStatus == "In progress" ->{
                    binding.btnAcceptBooking.gone()
                    binding.btnCancelBooking.gone()
                    binding.btnEndJob.visible()
                }
                data.bookingStatus == "Completed" ->{
                    binding.btnAcceptBooking.gone()
                    binding.btnCancelBooking.gone()
                    binding.btnEndJob.gone()
                }
                data.bookingStatus == "Canceled" ->{
                    binding.btnAcceptBooking.gone()
                    binding.btnCancelBooking.gone()
                    binding.btnEndJob.gone()
                }
            }

            setCardAndTextColor(itemView.context,data)
            Glide.loadImageWithListener(itemView.context, data.userImageUri, binding.imgUser) {
                binding.progressCircular.gone()
            }
            binding.tvUserName.text = data.userName
            binding.tvTypeTag.text = data.bookingTypeTag
            binding.tvBookingStatus.text = data.bookingStatus
            val time = DateTimeUtils.forBookingDateAndTime(data.timestamp)
            binding.tvTimeStamp.text = time
            binding.tvMake.text = data.vehicleMake
            binding.tvModel.text = data.vehicleModel
            binding.tvYear.text = data.vehicleYear
            binding.tvLnp.text = data.vehicleLpn
            binding.tvAddress.text = data.address
            binding.tvDetail.text = data.detail

            val latLng = LatLngUtil.stringToLatLng(data.latLng)
            binding.btnOpenMapLocation.onClick { openMap(latLng) }
            binding.btnShowHide.onClick { showHideLayout() }
            binding.btnAcceptBooking.onClick { acceptOrder(data) }
            binding.btnCancelBooking.onClick { cancelOrder(data) }
            binding.btnEndJob.onClick { endJob(data) }
        }

        private fun acceptOrder(data: ModelBookingData) {
            val bookingRef = db.collection("bookings").document(data.docId)
            bookingRef.update(mapOf(
                "accepted" to true,
                "bookingStatus" to "In progress"
            )).addOnSuccessListener {
                Toast.makeText(itemView.context, "Order accepted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(itemView.context, "Failed to accept order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        private fun endJob(data: ModelBookingData) {
            val bookingRef = db.collection("bookings").document(data.docId)
            bookingRef.update(mapOf(
                "accepted" to true,
                "bookingStatus" to "Completed"
            )).addOnSuccessListener {
                Toast.makeText(itemView.context, "Order Completed", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(itemView.context, "Failed to accept order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        private fun cancelOrder(data: ModelBookingData) {
            Dialogs.dialogOrderCancel(itemView.context, LayoutInflater.from(itemView.context)) {
                val bookingRef = db.collection("bookings").document(data.docId)
                bookingRef.update(mapOf(
                    "accepted" to false,
                    "bookingStatus" to "Canceled"
                )).addOnSuccessListener {
                    Toast.makeText(itemView.context, "Order Canceled", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(itemView.context, "Failed to accept order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun openMap(latLng: LatLng) {

        }

        private fun showHideLayout() {
            if (binding.layoutVehicleContent.isVisible) {
                binding.layoutVehicleContent.gone()
                binding.btnShowHide.setImageResource(R.drawable.icon_booking_show_content)
            } else {
                binding.layoutVehicleContent.visible()
                binding.btnShowHide.setImageResource(R.drawable.icon_booking_hide_content)
            }
        }

        private fun setCardAndTextColor(context: Context, data: ModelBookingData) {
            // Example: Set different colors based on booking status
            val cardColor = when (data.bookingStatus) {
                "Requested" -> Color.parseColor("#f9f7ee")
                "In progress" -> Color.parseColor("#1AFFA963")
                "Completed" -> Color.parseColor("#8BC34A")
                "Canceled" -> Color.parseColor("#F40B1C")
                else -> Color.parseColor("#f9f7ee")
            }

            val textColor = when (data.bookingStatus) {
                "Requested" -> Color.parseColor("#af7a2a")
                "In progress" -> Color.parseColor("#FFA963")
                "Completed" -> Color.WHITE
                "Canceled" -> Color.WHITE
                else -> Color.GRAY
            }

            // Assuming you have a CardView in your item layout with id cardView
            binding.cardView5.setCardBackgroundColor(cardColor)
            binding.tvBookingStatus.setTextColor(textColor)
        }
    }

}