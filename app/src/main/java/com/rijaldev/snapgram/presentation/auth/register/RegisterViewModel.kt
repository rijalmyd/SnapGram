package com.rijaldev.snapgram.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.auth.Register
import com.rijaldev.snapgram.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Register>>()
    val registerResult: LiveData<Result<Register>>
        get() = _registerResult

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        authUseCase.register(name, email, password).collect { result ->
            _registerResult.value = result
        }
    }
}