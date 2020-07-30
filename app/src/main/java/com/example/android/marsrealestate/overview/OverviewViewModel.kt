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
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status String
    val status: LiveData<String>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the MarsProperty with
    // new values
    private val _property = MutableLiveData<MarsProperty>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val property: LiveData<MarsProperty>
        get() = _property

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Gets Mars real estate property information from the Mars API Retrofit service and updates the
     * Mars properties retrieved.
     * [MarsProperty] [LiveData]. The Retrofit service returns a coroutine Deferred, which we await
     * to get the result of the transaction.
    */
     */
    private fun getMarsRealEstateProperties() {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            try {
                // Await the completion of our Retrofit request
                val listResult = getPropertiesDeferred.await()
                _status.value = "Success: ${listResult.size} Mars properties retrieved"
                if (listResult.isNotEmpty()) {
                    _property.value = listResult[0]
                }
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}