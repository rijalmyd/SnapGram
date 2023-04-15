package com.rijaldev.snapgram.domain.usecase.auth

import com.rijaldev.snapgram.domain.repository.AuthRepository
import javax.inject.Inject

class AuthInteractor @Inject constructor(private val authRepository: AuthRepository) : AuthUseCase {

    override fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    override fun login(email: String, password: String) =
        authRepository.login(email, password)

    override fun getLoginStatus() =
        authRepository.getLoginStatus()

    override suspend fun deleteCredential() =
        authRepository.deleteCredential()
}