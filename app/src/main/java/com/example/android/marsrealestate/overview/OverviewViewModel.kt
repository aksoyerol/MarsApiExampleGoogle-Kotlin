/*
 * Copyright 2019, The Android Open Source Project
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
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsApiStatus
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val _marsProperty = MutableLiveData<MarsProperty>()
    val marsProperty: LiveData<MarsProperty>
        get() = _marsProperty

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status

    init {
        getMarsRealEstatePropertiesWithCoroutines(MarsApiFilter.SHOW_ALL)
    }

//    private fun getMarsRealEstateProperties() {
//        MarsApi.retrofitService.getProperties().enqueue(object : retrofit2.Callback<List<MarsProperty>> {
//            override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
//                _response.value = "${response.body()?.size}"
//            }
//            override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
//              println(t.printStackTrace())
//            }
//        })
//    }

    private fun getMarsRealEstatePropertiesWithCoroutines(filter: MarsApiFilter) {
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                val data = MarsApi.retrofitService.getProperties(filter.value)
                _properties.value = data
                _response.value = "Connection Successful"
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _response.value = "Connection Error -> ${e.message}"
                //Cleaning the live data
                _properties.value = arrayListOf()
            }
        }
    }

    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstatePropertiesWithCoroutines(filter)
    }
}
