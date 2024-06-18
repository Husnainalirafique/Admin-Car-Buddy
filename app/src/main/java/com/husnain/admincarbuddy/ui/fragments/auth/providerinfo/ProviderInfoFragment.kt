package com.husnain.admincarbuddy.ui.fragments.auth.providerinfo

import android.app.Activity
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
import com.example.carbuddy.utils.DateTimeUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.husnain.admincarbuddy.data.ModelUser
import com.husnain.admincarbuddy.databinding.FragmentProviderInfoBinding
import com.husnain.admincarbuddy.ui.activities.HomeActivity
import com.husnain.admincarbuddy.ui.fragments.auth.VmAuth
import com.husnain.admincarbuddy.utils.Constants
import com.husnain.admincarbuddy.utils.DataState
import com.husnain.admincarbuddy.utils.Dialogs
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.dismissProgressDialog
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.showProgressDialog
import com.husnain.admincarbuddy.utils.ifEmpty
import com.husnain.admincarbuddy.utils.startActivity
import com.husnain.admincarbuddy.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProviderInfoFragment : Fragment() {
    private var _binding: FragmentProviderInfoBinding? = null
    private val binding get() = _binding!!
    private val ccp by lazy { binding.countryCodePicker }
    private val vm: VmAuth by viewModels()
    private lateinit var profileImgUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderInfoBinding.inflate(inflater, container, false)
        inIt()
        return binding.root
    }

    private fun inIt() {
        ccp.registerCarrierNumberEditText(binding.phoneNumber)
        setOnClickListener()
        setupObserver()
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnImagePicker.setOnClickListener {
            setUpImagePicker()
        }
        binding.etDateOfBirth.setOnClickListener {
            Dialogs.showDatePickerDialog(requireContext()) {
                binding.etDateOfBirth.setText(DateTimeUtils.formatDateOfBirth(it))
            }
        }
        binding.btnContinue.setOnClickListener {
            if (validateForm()) {
                getFcmToken()
            }
        }
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                toast(task.exception?.localizedMessage!!)
                return@OnCompleteListener
            }
            val token = task.result
            createAccount(token)
        })
    }

    private fun createAccount(fcmToken: String) {
        val email = arguments?.getString(Constants.KEY_EMAIL)!!
        val password = arguments?.getString(Constants.KEY_PASSWORD)!!
        val fullName = binding.etFullName.text.toString()
        val dateOfBirth = binding.etDateOfBirth.text.toString()
        val phoneNumber = ccp.fullNumberWithPlus
        val address = binding.etAddress.text.toString()

        val user = ModelUser(
            email,
            password,
            fullName,
            dateOfBirth,
            phoneNumber,
            address,
            profileImgUri.toString(),
            fcmToken
        )

        lifecycleScope.launch(Dispatchers.IO) {
            vm.signUpWithEmailPass(user)
        }
    }

    private fun setupObserver() {
        vm.signUpStatus.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    dismissProgressDialog()
                    startActivity(HomeActivity::class.java)
                    requireActivity().finish()
                }

                is DataState.Error -> {
                    toast(dataState.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog()
                }
            }
        }

    }

    private var isImageChanged = false
    private val launcherForImagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    if (uri != null) {
                        profileImgUri = uri
                        binding.profilePic.setImageURI(data.data)
                        isImageChanged = true
                    }
                }

                ImagePicker.RESULT_ERROR -> {
                    toast(ImagePicker.getError(data))
                }

                else -> {
                    toast("Task Cancelled")
                }
            }
        }


    private fun setUpImagePicker() {
        ImagePicker.with(requireActivity())
            .crop()
            .createIntent {
                launcherForImagePicker.launch(it)
            }
    }


    private fun validateForm(): Boolean {
        val fullNameEditText = binding.etFullName
        val dateOfBirthEditText = binding.etDateOfBirth
        val addressEditText = binding.etAddress
        val phoneNumberEditText = binding.phoneNumber

        fullNameEditText.error = null
        dateOfBirthEditText.error = null
        addressEditText.error = null

        return when {
            fullNameEditText.ifEmpty("Full Name is required") -> { false }
            dateOfBirthEditText.ifEmpty("Date of Birth is required") -> { false }
            phoneNumberEditText.ifEmpty("Phone number is required")-> { false }

            !ccp.isValidFullNumber -> {
                toast("Invalid phone number for ${ccp.selectedCountryName}")
                false
            }

            !isImageChanged -> {
                toast("Please Select an Image!")
                false
            }

            addressEditText.ifEmpty("Address is required") -> { false }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}