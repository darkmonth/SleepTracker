/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    //vars
    private var sleepTrackerJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + sleepTrackerJob)
    private var toNight = MutableLiveData<SleepNight?>()
    //end vars


    init {
        initializeNight()
    }

    private fun initializeNight() {
        uiScope.launch {
            toNight.value = getToNightFromDb()

        }
    }

    private suspend fun getToNightFromDb(): SleepNight? {

        return withContext(Dispatchers.IO) {
            var night = database.getLastSleep()
            if (night?.startTimeMilli != night?.endTimeMilli) {
                night = null
            }
            night
        }
    }

    fun onStartTracking() {
        uiScope.launch {
            val night = SleepNight()
            insertNight(night)
            toNight.value = getToNightFromDb()
        }
    }

    private suspend fun insertNight(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insertSleep(night)
        }
    }

    override fun onCleared() {
        super.onCleared()
        sleepTrackerJob.cancel()
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = toNight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            updateNight(oldNight)
            toNight.value = getToNightFromDb()
        }
    }

    private suspend fun updateNight(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.updateSleep(night)
        }
    }

    fun clear(){
        uiScope.launch {
            clearNights()
            toNight.value=null
        }
    }
    private suspend fun clearNights(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

}

