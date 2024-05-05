package com.husnain.admincarbuddy.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.husnain.admincarbuddy.databinding.ActivityAuthBinding
import com.husnain.admincarbuddy.utils.startActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    @Inject lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inIt()
    }

    private fun inIt() {
        setOnClickListener()
        checkUserLogedInStatus()
    }

    private fun setOnClickListener() {

    }

    private fun checkUserLogedInStatus(){
        if (auth.currentUser != null){
            startActivity(HomeActivity::class.java)
            finish()
        }
    }

}