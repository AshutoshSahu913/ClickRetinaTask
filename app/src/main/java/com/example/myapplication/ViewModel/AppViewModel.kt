package com.example.myapplication.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Model.TaskResponse
import com.example.myapplication.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val repository = Repository()
    private val _data = MutableLiveData<TaskResponse?>()
    val data: LiveData<TaskResponse?> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    fun fetchData() {
        _isLoading.postValue(true)
        _progress.postValue(0)  // Reset progress

        viewModelScope.launch(Dispatchers.IO) {
            // Simulate loading process
            for (i in 1..100) {
                delay(50)  // Simulate network delay
                _progress.postValue(i)
            }

            // Fetch data from repository
            val apiResponse = repository.fetchData()

            // Post result to LiveData on the main thread
            if (apiResponse!=null){
                _data.postValue(apiResponse)
            }
            _isLoading.postValue(false)
        }
    }
}
