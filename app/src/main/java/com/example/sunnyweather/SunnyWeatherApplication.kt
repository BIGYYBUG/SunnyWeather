package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/*
*这里我们定义了一个全局获取Context的方式
*
 */
class SunnyWeatherApplication : Application(){
    companion object{
        const val TOKEN = "QTNeWqorFVIpoW0s"
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}