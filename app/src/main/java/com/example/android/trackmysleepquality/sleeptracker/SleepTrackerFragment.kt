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

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)
        val application = requireNotNull(this.activity).application
        val database = SleepDatabase.getInstance(application)
        val sleepTrackerViewModelFactory = SleepTrackerViewModelFactory(database.sleepDatabaseDao, application)
        val sleepTrackerViewModel = ViewModelProvider(this, sleepTrackerViewModelFactory).get(SleepTrackerViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        sleepTrackerViewModel.navigateToSleepQualityStatus.observe(this, Observer {
            night -> night?.let {
            this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
            sleepTrackerViewModel.doneNavigating()
        }
        })

        sleepTrackerViewModel.showSnackbarEvent.observe(this, Observer {
            if(it){
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message) ,
                        Snackbar.LENGTH_SHORT
                ).show()
                sleepTrackerViewModel.doneShowSnackBar()
            }
        })
       // testWithContext()



        return binding.root
    }


    fun testWithContext() {
        var job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)
        var resultOne = "Hardstyle"
        var resultTwo = "Minions"
        Log.i("withContext", "Before")
        uiScope.launch {
            resultOne = withContext(Dispatchers.IO) { function1() }
            resultTwo = withContext(Dispatchers.IO) { function2() }
        }

        Log.i("withContext", "After")
        val resultText = resultOne + resultTwo
        Log.i("withContext", resultText)
    }

    suspend fun function1(): String {
        delay(1000L)
        val message = "function1"
        Log.i("withContext", message)
        return message
    }

    suspend fun function2(): String {
        delay(100L)
        val message = "function2"
        Log.i("withContext", message)
        return message
    }
}
