package com.rijaldev.snapgram.data.repository

import com.rijaldev.snapgram.data.local.datastore.UserPreferences
import com.rijaldev.snapgram.data.remote.RemoteDataSource
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.repository.AuthRepository
import com.rijaldev.snapgram.util.getErrorMessage
import com.rijaldev.snapgram.util.toDomainLogin
import com.rijaldev.snapgram.util.toDomainRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userPreferences: UserPreferences,
) : AuthRepository {

    override fun register(name: String, email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val response = remoteDataSource.register(name, email, password)
            val result = response.toDomainRegister()

            emit(Result.Success(result))
        } catch (e: HttpException) {
            emit(Result.Error(e.getErrorMessage()))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun login(email: String, password: String) = flow {
        emit(Result.Loading())
        try {
            val response = remoteDataSource.login(email, password)
            val result = response.toDomainLogin()
            userPreferences.run {
                saveToken(result.token.toString())
                setLoginStatus(true)
            }

            emit(Result.Success(result))
        } catch (e: HttpException) {
            emit(Result.Error(e.getErrorMessage()))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteCredential() {
        userPreferences.run {
            deleteToken()
            setLoginStatus(false)
        }
    }

    override fun getLoginStatus() = userPreferences.getLoginStatus()
}