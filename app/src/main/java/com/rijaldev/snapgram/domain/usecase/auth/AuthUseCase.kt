package com.rijaldev.snapgram.domain.usecase.auth

import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.auth.Login
import com.rijaldev.snapgram.domain.model.auth.Register
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun register(name: String, email: String, password: String): Flow<Result<Register>>

    fun login(email: String, password: String): Flow<Result<Login>>

    fun getLoginStatus(): Flow<Boolean>

    suspend fun deleteCredential()
}
