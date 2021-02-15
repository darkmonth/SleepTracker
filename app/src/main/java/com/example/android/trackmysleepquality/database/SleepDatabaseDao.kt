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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepDatabaseDao{
    @Insert
    fun insertSleep(sleepNight:SleepNight)

    @Update
    fun updateSleep (sleepNight: SleepNight)

    @Query("SELECT *FROM daily_sleep_quality WHERE nightId=:nightId")
    fun getSleep(nightId:Long):SleepNight

    @Query("SELECT *FROM daily_sleep_quality ORDER By nightId DESC")
    fun getSleeps():LiveData<List<SleepNight>>

    @Delete
    fun deleteSleep (night: SleepNight)

    @Query("SELECT *FROM daily_sleep_quality ORDER By nightId DESC LIMIT 1")
    fun getLastSleep():SleepNight?

    @Query("DELETE FROM daily_sleep_quality")
    fun clear()

}
