package com.husnain.admincarbuddy.ui.fragments.auth.login

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.FragmentLoginBinding
import com.husnain.admincarbuddy.ui.activities.HomeActivity
import com.husnain.admincarbuddy.ui.fragments.auth.VmAuth
import com.husnain.admincarbuddy.utils.BackPressedExtensions.goBackPressed
import com.husnain.admincarbuddy.utils.DataState
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.dismissProgressDialog
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.showProgressDialog
import com.husnain.admincarbuddy.utils.getColorFromId
import com.husnain.admincarbuddy.utils.setPasswordVisibilityToggle
import com.husnain.admincarbuddy.utils.startActivity
import com.husnain.admincarbuddy.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val vm: VmAuth by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        inIt()
        return binding.root
    }

    private fun inIt() {
        setUpUI()
        setOnClickListeners()
        backPressed()
        setUpObserver()
    }

    private fun setOnClickListeners() {
        binding.btnSignIn.setOnClickListener {
            if (isValid()) {
                login()
            }
        }
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        lifecycleScope.launch {
            vm.loginWithEmailPass(email, password)
        }
    }

    private fun setUpObserver() {
        vm.loginStatus.observe(viewLifecycleOwner) { dataState ->
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

    private fun setUpUI() {
        setPasswordVisibilityToggle(binding.etPassword)
    }

    private fun isValid(): Boolean {
        val email = binding.etEmail
        val password = binding.etEmail

        email.error = null
        password.error = null
        return when {
            email.text.isNullOrEmpty() -> {
                email.error = "Please fill!"
                email.requestFocus()
                false
            }

            password.text.isNullOrEmpty() -> {
                password.error = "Please fill!"
                password.requestFocus()
                false
            }

            password.text.toString().length < 7 -> {
                password.error = "Password must be at least 7 characters long"
                password.requestFocus()
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() -> {
                email.error = "Invalid email pattern"
                email.requestFocus()
                false
            }

            else -> {
                true
            }
        }
    }

    private fun backPressed() = goBackPressed { requireActivity().finishAffinity() }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}