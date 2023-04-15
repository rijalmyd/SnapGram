package com.rijaldev.snapgram.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    suspend fun saveToken(token: String)

    suspend fun deleteToken()

    fun getToken(): Flow<String>

    suspend fun setLoginStatus(isLogin: Boolean)

    fun getLoginStatus(): Flow<Boolean>
}