package com.rijaldev.snapgram.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.auth.Login
import com.rijaldev.snapgram.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _loginResult = MutableSharedFlow<Result<Login>>()
    val loginResult: SharedFlow<Result<Login>>
        get() = _loginResult

    fun login(email: String, password: String) = viewModelScope.launch {
        authUseCase.login(email, password).collect { result ->
            _loginResult.emit(result)
        }
    }
}