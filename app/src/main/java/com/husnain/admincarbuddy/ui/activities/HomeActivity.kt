package com.husnain.admincarbuddy.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.ActivityHomeBinding
import com.husnain.admincarbuddy.utils.gone
import com.husnain.admincarbuddy.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHostFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.afterAuthActivityNavHostFragment)!!
        inIt()
    }

    private fun inIt() {
        setUpBottomBar()
        setOnClickListener()
    }

    private fun setOnClickListener() {

    }

    private fun setUpBottomBar() {
        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment || destination.id == R.id.ordersFragment || destination.id == R.id.profileFragment) {
                binding.bottomNavigationView.visible()
            } else {
                binding.bottomNavigationView.gone()
            }
        }
    }
}