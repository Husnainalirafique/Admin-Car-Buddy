package com.husnain.admincarbuddy.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.transition.Visibility
import com.husnain.admincarbuddy.preferences.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VmHome @Inject constructor(
    private val prefs: PreferenceManager
) : ViewModel() {
    private val _incomeVisibility = MutableLiveData<Boolean>()
    val incomeVisibility: LiveData<Boolean> = _incomeVisibility

    init {
        _incomeVisibility.value = prefs.getIsIncomeVisible()
    }

    fun setIncomeVisibility(visibility: Boolean){
        _incomeVisibility.value = visibility
        prefs.setIsIncomeVisible(visibility)
    }
}