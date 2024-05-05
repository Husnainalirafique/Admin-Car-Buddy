package com.husnain.admincarbuddy.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.FragmentHomeBinding
import com.husnain.admincarbuddy.preferences.PreferenceManager
import com.husnain.admincarbuddy.ui.activities.VmHome
import com.husnain.admincarbuddy.utils.invisible
import com.husnain.admincarbuddy.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var prefs: PreferenceManager
    private val vmHome: VmHome by viewModels()


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
    }

    private fun setOnClickListener() {
        binding.btnShowHideIncome.setOnClickListener {
            if (prefs.getIsIncomeVisible()) {
                vmHome.setIncomeVisibility(false)
            } else {
                vmHome.setIncomeVisibility(true)
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