package com.husnain.admincarbuddy.ui.fragments.addProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.husnain.admincarbuddy.data.ModelVendorProfile
import com.husnain.admincarbuddy.databinding.FragmentAddProfileBinding
import com.husnain.admincarbuddy.preferences.PreferenceManager
import com.husnain.admincarbuddy.ui.activities.MapActivity
import com.husnain.admincarbuddy.utils.DataState
import com.husnain.admincarbuddy.utils.Dialogs
import com.husnain.admincarbuddy.utils.PhotoPicker.openPhotoPicker
import com.husnain.admincarbuddy.utils.PhotoPicker.setupPhotoPicker
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.dismissProgressDialog
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.showProgressDialog
import com.husnain.admincarbuddy.utils.ifEmpty
import com.husnain.admincarbuddy.utils.onClick
import com.husnain.admincarbuddy.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddProfileFragment : Fragment() {

    private var _binding: FragmentAddProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileImgUri: Uri
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject lateinit var prefs: PreferenceManager
    private var profileLocationLatLng: String? = null
    private val vm: VmAddVendorProfile by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProfileBinding.inflate(inflater, container, false)
        inIt()
        return binding.root
    }

    private fun inIt() {
        setOnClickListener()
        setUpObserver()
    }

    private fun setOnClickListener() {
        binding.backArrow.onClick { findNavController().navigateUp() }
        binding.etStartTime.onClick { showTimePicker(true) }
        binding.etEndTime.onClick { showTimePicker(false) }
        binding.btnImagePicker.onClick { openPhotoPicker(pickMedia) }
        binding.etSelectCity.onClick { showCityPickerDialog() }

        binding.btnChooseLocationOnMap.onClick {
            val intent = Intent(requireContext(), MapActivity::class.java)
            startMapActivityForResult.launch(intent)
        }

        binding.btnAddService.onClick {
            if (validateForm()) {
                addProfile()
            }
        }
    }

    private fun showCityPickerDialog() {
        Dialogs.cityPickerDialog(requireContext(), layoutInflater) {
            binding.etSelectCity.setText(it)
        }
    }

    private fun addProfile() {
        val fullName = binding.etFullName.text.toString()
        val contactNumber = binding.etPhoneNumber.text.toString()
        val whatsappNumber = binding.etWhatsappNumber.text.toString()
        val speciality = binding.etSpeciality.text.toString()
        val experience = "${binding.etYearsOfExperience.text.toString()} years"
        val city = binding.etSelectCity.text.toString()
        val shopName = binding.etShopName.text.toString()
        val availability =
            "${binding.etStartTime.text.toString()} - ${binding.etEndTime.text.toString()}"
        val cityLocation = binding.etCityLocation.text.toString()
        val shopMapLocation = profileLocationLatLng

        val fcmToken = prefs.getUserData()
        fcmToken?.let {
            val vendorProfile = ModelVendorProfile(
                vendorImage = profileImgUri.toString(),
                fullName = fullName,
                contactNumber = contactNumber,
                whatsappNumber = whatsappNumber,
                speciality = speciality,
                yearsOfExperience = experience,
                city = city,
                shopName = shopName,
                availability = availability,
                locationNameInCity = cityLocation,
                addressFromMap = shopMapLocation!!,
                vendorUid = auth.uid!!,
                fcmToken = it.fcmToken
            )
            lifecycleScope.launch {
                vm.addVendor(vendorProfile)
            }
        }
    }

    private fun setUpObserver() {
        vm.addVendorStatus.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    dismissProgressDialog()
                    toast("Vendor added.")
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog()
                }
            }
        }
    }

    private fun showTimePicker(isStartTime: Boolean) {
        Dialogs.showTimePickerMaterialDialog(isStartTime, childFragmentManager) { formattedTime ->
            if (isStartTime) {
                binding.etStartTime.setText(formattedTime)
            } else {
                binding.etEndTime.setText(formattedTime)
            }
        }
    }

    private var isImageChanged = false

    private val pickMedia = setupPhotoPicker { uri ->
        uri?.let {
            profileImgUri = it
            binding.profilePic.setImageURI(it)
            isImageChanged = true
        }
    }

    //to get the location latLng
    private val startMapActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data: Intent? = result.data
                    val latLngString = data?.getStringExtra("LOCATION")
                    if (latLngString != null) {
                        profileLocationLatLng = latLngString
                    }
                }
            }
        }

    private fun validateForm(): Boolean {
        val fullNameEditText = binding.etFullName
        val contact = binding.etPhoneNumber
        val whatsappNumber = binding.etWhatsappNumber
        val speciality = binding.etSpeciality
        val experience = binding.etYearsOfExperience
        val city = binding.etSelectCity
        val shopName = binding.etShopName
        val startTimeEt = binding.etStartTime
        val endTimeEt = binding.etEndTime
        val cityLocation = binding.etCityLocation
        //LatLng
        fullNameEditText.error = null
        contact.error = null
        whatsappNumber.error = null
        speciality.error = null
        experience.error = null
        city.error = null
        shopName.error = null
        startTimeEt.error = null
        endTimeEt.error = null
        cityLocation.error = null

        return when {
            fullNameEditText.ifEmpty("Can't be empty!") -> {
                false
            }

            contact.ifEmpty("Can't be empty!") -> {
                false
            }

            whatsappNumber.ifEmpty("Can't be empty!") -> {
                false
            }

            speciality.ifEmpty("Can't be empty!") -> {
                false
            }

            experience.ifEmpty("Can't be empty!") -> {
                false
            }

            shopName.ifEmpty("Can't be empty!") -> {
                false
            }

            startTimeEt.ifEmpty("Can't be empty!") -> {
                false
            }

            endTimeEt.ifEmpty("Can't be empty!") -> {
                false
            }

            city.ifEmpty("Can't be empty!") -> {
                false
            }

            cityLocation.ifEmpty("Can't be empty!") -> {
                false
            }

            !isImageChanged -> {
                toast("Please select an image to proceed!")
                false
            }

            profileLocationLatLng == null -> {
                toast("Please select a location from map.")
                false
            }

            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}