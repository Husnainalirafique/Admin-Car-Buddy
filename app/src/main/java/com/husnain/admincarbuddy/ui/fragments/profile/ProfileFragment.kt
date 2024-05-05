package com.husnain.admincarbuddy.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.FragmentProfileBinding
import com.husnain.admincarbuddy.preferences.PreferenceManager
import com.husnain.admincarbuddy.ui.activities.AuthActivity
import com.husnain.admincarbuddy.utils.Dialogs
import com.husnain.admincarbuddy.utils.Glide
import com.husnain.admincarbuddy.utils.startActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var prefs: PreferenceManager
    @Inject lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        inIt()
        return binding.root
    }

    private fun inIt() {
        setOnClickListener()
        getAndSetUserDataFromPrefs()
    }

    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun getAndSetUserDataFromPrefs() {
        val user = prefs.getUserData()
        if (user != null){
            binding.tvUserName.text = user.fullName
            Glide.loadImage(requireContext(),user.profileImageUri,binding.imgUser)
        }
    }

    private fun logout(){
        Dialogs.logoutDialog(requireContext(),layoutInflater){
            auth.signOut()
            startActivity(AuthActivity::class.java)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}