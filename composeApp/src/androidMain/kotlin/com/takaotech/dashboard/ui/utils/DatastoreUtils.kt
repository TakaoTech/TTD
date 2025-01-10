package com.takaotech.dashboard.ui.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createSessionDataStore(context: Context): DataStore<Preferences> =
    getSessionDatastore(
        producePath = { context.filesDir.resolve(sessionDataStoreFileName).absolutePath },
    )
