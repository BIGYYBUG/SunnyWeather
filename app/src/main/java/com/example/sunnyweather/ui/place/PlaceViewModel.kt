package com.example.sunnyweather.ui.place

import android.view.animation.Transformation
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
/*
* 定义ViewModel层。相当于逻辑层和UI层之间的一个桥梁
* */
class PlaceViewModel:ViewModel() {
    private val searchLiveData = MutableLiveData<String>()
    val placeList = ArrayList<Place>()
    val placeLiveData = searchLiveData.switchMap { query->
        Repository.searchPlaces(query)

    }
    fun searchPlaces(query:String){
        searchLiveData.value = query
    }
    fun savePlace(place: Place)= Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlaceSaved()= Repository.isPlaceSaved()


}