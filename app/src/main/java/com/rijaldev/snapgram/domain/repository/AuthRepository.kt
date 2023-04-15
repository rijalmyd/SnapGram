package com.rijaldev.snapgram.domain.repository

import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.auth.Login
import com.rijaldev.snapgram.domain.model.auth.Register
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun register(name: String, email: String, password: String): Flow<Result<Register>>

    fun login(email: String, password: String): Flow<Result<Login>>

    suspend fun deleteCredential()

    fun getLoginStatus(): Flow<Boolean>
}