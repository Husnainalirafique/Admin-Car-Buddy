package com.husnain.admincarbuddy.ui.fragments.orders.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.husnain.admincarbuddy.databinding.FragmentPendingBinding
import com.husnain.admincarbuddy.ui.fragments.orders.VmOrders
import com.husnain.admincarbuddy.ui.fragments.orders.adapter.AdapterOrders
import com.husnain.admincarbuddy.utils.DataState
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.dismissProgressDialog
import com.husnain.admincarbuddy.utils.ProgressDialogUtil.showProgressDialog
import com.husnain.admincarbuddy.utils.gone
import com.husnain.admincarbuddy.utils.toast
import com.husnain.admincarbuddy.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingFragment : Fragment() {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!
    private val vmOrders: VmOrders by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        vmOrders.getPendingBookings()
        inIt()
        return binding.root
    }

    private fun inIt() {
        setOnClickListener()
        setUpObserver()
    }

    private fun setOnClickListener() {

    }

    private fun setUpObserver(){
        vmOrders.bookingsData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    val bookingList = it.data
                    if (bookingList.isNullOrEmpty()) {
                        binding.layoutNoBookings.visible()
                        binding.recyclerView.gone()
                    } else {
                        binding.layoutNoBookings.gone()
                        binding.recyclerView.visible()
                        val adapter = AdapterOrders(bookingList)
                        binding.recyclerView.adapter = adapter
                    }
                    dismissProgressDialog()
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}