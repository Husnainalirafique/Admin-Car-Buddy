package com.husnain.admincarbuddy.ui.fragments.orders.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.husnain.admincarbuddy.ui.fragments.orders.history.HistoryFragment
import com.husnain.admincarbuddy.ui.fragments.orders.ongoing.OngoingFragment
import com.husnain.admincarbuddy.ui.fragments.orders.pending.PendingFragment

class BookingsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingFragment()
            1 -> OngoingFragment()
            2 -> HistoryFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
