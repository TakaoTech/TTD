package com.takaotech.dashboard.ui.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

internal const val sessionDataStoreFileName = "session.preferences_ds"
private lateinit var sessionDatastore: DataStore<Preferences>

private val lock = SynchronizedObject()

fun getSessionDatastore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::sessionDatastore.isInitialized) {
            sessionDatastore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { sessionDatastore = it }
        }
    }
