package com.husnain.admincarbuddy.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.husnain.admincarbuddy.databinding.DialogCityPickerBinding
import com.husnain.admincarbuddy.databinding.DialogConformOrderCancelationBinding
import com.husnain.admincarbuddy.databinding.DialogLogoutBinding
import com.husnain.admincarbuddy.ui.fragments.addProfile.CityAdapter
import java.util.Calendar
import java.util.Date

object Dialogs {

    fun logoutDialog(
        context: Context,
        inflater: LayoutInflater,
        logout: () -> Unit
    ) {
        val dialog = Dialog(context)
        val binding = DialogLogoutBinding.inflate(inflater, null, false)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.verticalMargin = 0.02f // 10% margin from bottom

        dialog.window?.attributes = layoutParams
        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.btnYes.setOnClickListener {
                logout.invoke()
                dismiss()
            }
            binding.btnNo.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun dialogOrderCancel(
        context: Context,
        inflater: LayoutInflater,
        cancel: () -> Unit
    ) {
        val dialog = Dialog(context)
        val binding = DialogConformOrderCancelationBinding.inflate(inflater, null, false)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.verticalMargin = 0.018f //margin from bottom

        dialog.window?.attributes = layoutParams
        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.btnYes.setOnClickListener {
                cancel.invoke()
                dismiss()
            }
            binding.btnNo.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun cityPickerDialog(
        context: Context,
        inflater: LayoutInflater,
        onCitySelected: (String) -> Unit
    ) {
        val cities = listOf(
            "Khanpur", "Lahore", "Faisalabad", "Rawalpindi", "Multan",
            "Gujranwala", "Peshawar", "Quetta", "Islamabad", "Rahim Yar Khan"
        ).sorted()


        val dialog = Dialog(context)
        val binding = DialogCityPickerBinding.inflate(inflater, null, false)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.verticalMargin = 0.02f // 10% margin from bottom

        dialog.window?.attributes = layoutParams
        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.recyclerViewCities.adapter = CityAdapter(cities) { selectedCity ->
                onCitySelected.invoke(selectedCity)
                dismiss()
            }
            show()
        }
    }

    inline fun permissionAlertDialog(context: Context,message:String,crossinline callback:() -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.apply{
            setMessage(message)
            setPositiveButton("Yes") { dialog, _ ->
                callback.invoke()
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    inline fun showDatePickerDialog(
        context: Context,
        crossinline onDateSelected: (date: Date) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val date = selectedDate.time
                onDateSelected.invoke(date)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    fun showTimePickerMaterialDialog(isStartTime: Boolean, fragmentManager: FragmentManager,dateCallBack:(String) -> Unit){
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setHour(12)
            .setMinute(0)
            .setTitleText(if (isStartTime) "Select Start Time" else "Select End Time")
            .build()

        picker.show(fragmentManager,"MATERIAL_TIME_PICKER")

        picker.addOnPositiveButtonClickListener {
            val formattedTime = String.format("%02d:%02d %s",
                if (picker.hour % 12 == 0) 12 else picker.hour % 12, picker.minute,
                if (picker.hour < 12) "AM" else "PM"
            )
            dateCallBack.invoke(formattedTime)
        }
    }

}