package com.husnain.admincarbuddy.ui.fragments.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.FragmentHomeBinding
import com.husnain.admincarbuddy.preferences.PreferenceManager
import com.husnain.admincarbuddy.ui.activities.VmHome
import com.husnain.admincarbuddy.utils.animateHide
import com.husnain.admincarbuddy.utils.animateShow
import com.husnain.admincarbuddy.utils.gone
import com.husnain.admincarbuddy.utils.invisible
import com.husnain.admincarbuddy.utils.navigate
import com.husnain.admincarbuddy.utils.onClick
import com.husnain.admincarbuddy.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var prefs: PreferenceManager
    private val vmHome: VmHome by viewModels()
    private var isAllFabsVisible = false
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val requestCode = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        inIt()
        return binding.root
    }

    private fun inIt() {
        setOnClickListener()
        setupIncomeVisibilityObserver()
        setUpFloatingButtonsVisibility()
        requestPermission()
    }

    private fun requestPermission() {
        requireActivity().requestPermissions(permissions, requestCode)
    }
    private fun setUpFloatingButtonsVisibility() {
        isAllFabsVisible = false
        binding.btnAddMechanicProfile.gone()
        binding.tvAddMechanicProfile.gone()
        binding.btnAddServiceShopProfile.gone()
        binding.tvAddServiceShopProfile.gone()
    }

    private fun setOnClickListener() {
        binding.btnShowHideIncome.onClick {
            if (prefs.getIsIncomeVisible()) {
                vmHome.setIncomeVisibility(false)
            } else {
                vmHome.setIncomeVisibility(true)
            }
        }
        binding.btnAddMechanicProfile.onClick {
            navigate(R.id.action_homeFragment_to_addProfileFragment)
        }
        binding.btnAddServiceShopProfile.onClick {

        }

        binding.btnAddProfile.onClick {
            val offsetY = (binding.btnAddProfile.height * 0.4).toFloat()

            if (!isAllFabsVisible) {
                binding.btnAddMechanicProfile.animateShow(offsetY)
                binding.tvAddMechanicProfile.animateShow(offsetY)
                binding.btnAddServiceShopProfile.animateShow(offsetY)
                binding.tvAddServiceShopProfile.animateShow(offsetY)
                isAllFabsVisible = true
            } else {
                binding.btnAddMechanicProfile.animateHide(offsetY)
                binding.tvAddMechanicProfile.animateHide(offsetY)
                binding.btnAddServiceShopProfile.animateHide(offsetY)
                binding.tvAddServiceShopProfile.animateHide(offsetY)
                isAllFabsVisible = false
            }
        }
    }

    private fun setupIncomeVisibilityObserver() {
        vmHome.incomeVisibility.observe(viewLifecycleOwner){ isVisible ->
            if (isVisible){
                binding.let {
                    it.imgShowHideIncome.setImageResource(R.drawable.icon_pkr_visible)
                    it.tvIncomeInPkr.invisible()
                    it.tvIncomeInPkrHidden.visible()
                    it.tvPkr.invisible()
                    it.tvPkrHidden.visible()
                }
            }
            else{
                binding.let {
                    it.imgShowHideIncome.setImageResource(R.drawable.icon_pkr_hidden)
                    it.tvIncomeInPkrHidden.invisible()
                    it.tvIncomeInPkr.visible()
                    it.tvPkrHidden.invisible()
                    it.tvPkr.visible()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}