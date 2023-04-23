package com.rijaldev.snapgram.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rijaldev.snapgram.util.Constants.LOGIN_STATUS
import com.rijaldev.snapgram.util.Constants.USER_TOKEN
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserPreferences {

    override suspend fun saveToken(token: String) {
        dataStore.edit {
            it[USER_TOKEN] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit {
            it.remove(USER_TOKEN)
        }
    }

    override fun getToken() =
        dataStore.data.map {
            it[USER_TOKEN] ?: ""
        }

    override suspend fun setLoginStatus(isLogin: Boolean) {
        dataStore.edit {
            it[LOGIN_STATUS] = isLogin
        }
    }

    override fun getLoginStatus() =
        dataStore.data.map {
            it[LOGIN_STATUS] ?: false
        }
}