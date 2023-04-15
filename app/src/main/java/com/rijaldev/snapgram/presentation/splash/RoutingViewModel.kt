package com.rijaldev.snapgram.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rijaldev.snapgram.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoutingViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    fun getLoginStatus() = authUseCase.getLoginStatus().asLiveData()
}