package com.rijaldev.snapgram.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val USER_PREFERENCES = "user_preferences"
    val USER_TOKEN = stringPreferencesKey("user_token")
    val LOGIN_STATUS = booleanPreferencesKey("user_login")

    const val STORY_WORKER = "story_work"
}