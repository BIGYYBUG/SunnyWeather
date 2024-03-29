package com.example.sunnyweather.ui.weather

import android.content.ContentProvider
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
  lateinit var  binding : ActivityWeatherBinding

  val viewmodel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)


       setContentView(binding.root)
        if (viewmodel.locationLng.isEmpty()) {
            viewmodel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }

        if (viewmodel.locationLat.isEmpty()) {
            viewmodel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewmodel.placeName.isEmpty()){
            viewmodel.placeName = intent.getStringExtra("place_name")?:""

        }
        viewmodel.weatherliveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()

            }

       binding.swipeRefresh.isRefreshing = false
        })
          binding.swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
        refreWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreWeather()
        }
        val navBtn = findViewById<Button>(R.id.navBtn)

   navBtn.setOnClickListener  {
         binding.drawer.openDrawer(GravityCompat.START)
      }
        binding.drawer.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

           override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

           override fun onDrawerOpened(drawerView: View) {}

           override fun onDrawerClosed(drawerView: View) {
               val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
           }
        })
    }

     fun refreWeather(){

       viewmodel.refreshWeather(viewmodel.locationLng,viewmodel.locationLat)
       binding.swipeRefresh.isRefreshing = true

   }
    fun Closedrawers(){
        binding.drawer.closeDrawers()

    }

    private fun showWeatherInfo(weather: Weather) {
        val placeName = findViewById<TextView>(R.id.placeName)
        val currentTemp = findViewById<TextView>(R.id.currentTemp)
        val currentSky = findViewById<TextView>(R.id.currentSky)
        val currentAQI = findViewById<TextView>(R.id.currentAQI)
        val nowLayout = findViewById<RelativeLayout>(R.id.nowLayout)

        placeName?.text = viewmodel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //现在填充now——xml布局的数据
        val currentTempText = "${realtime.temperature.toInt()}°C"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info

        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast
        val forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout)
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dataInfo = view.findViewById(R.id.dataInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            dataInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            forecastLayout?.addView(view)
        }
     //填充life——index

        val coldRisk = findViewById<TextView>(R.id.coldRiskText)
        val dressing = findViewById<TextView>(R.id.dressingText)
        val ultraviolet = findViewById<TextView>(R.id.ultravioletText)
        val carwashing = findViewById<TextView>(R.id.carWashingText)
        val weatherLayout = findViewById<ScrollView>(R.id.weatherLayout)
        val lifeIndex = daily.lifeIndex
        coldRisk.text = lifeIndex.coldRisk[0].desc
        dressing.text = lifeIndex.dressing[0].desc
        ultraviolet.text=lifeIndex.ultraviolet[0].desc
        carwashing.text=lifeIndex.carWashing[0].desc
        weatherLayout.visibility=View.VISIBLE
    }

}