package com.example.weatherapp.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapter.ForecastAdapter
import com.example.weatherapp.R
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel:WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor=Color.TRANSPARENT
        }

        binding.apply {
            var lot=51.50
            var lon=-0.12
            var none="Türkiye"

            //current temp
            cityTxt.text=none
            progressBar.visibility= View.VISIBLE
            weatherViewModel.loadCurrentWeather(lot,lon,"metric").enqueue(object :
            retrofit2.Callback<CurrentResponseApi> {
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
                    if(response.isSuccessful){
                        val data=response.body()
                        progressBar.visibility=View.GONE
                        detailLayout.visibility=View.VISIBLE


                        data?.let {
                            statusTxt.text= it.weather?.get(0)?.main ?: "-"
                            nemTxt.text=it.main?.humidity?.toString()+"%"
                            ruzgarTxt.text= it.wind?.speed.let { it?.let { it1 -> Math.round(it1).toString() } } +"Km"
                            currentTempTxt.text= it.main?.temp.let { it?.let { it1 -> Math.round(it1).toString() } }+"°"
                            maxTempTxt.text= it.main?.tempMax.let { it?.let { it1 -> Math.round(it1).toString() } }+"°"
                            minTempTxt.text= it.main?.tempMin.let { it?.let { it1 -> Math.round(it1).toString() } }+"°"

                            val drawable=if(isNightNow()) R.drawable.snow_bg
                            else{
                                setDynamicallyWallpaper(it.weather?.get(0)?.icon?:"-")
                            }
                            bgImage.setImageResource(drawable)
                            setEffectRainSnow(it.weather?.get(0)?.icon?:"-")



                        }

                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.toString(),Toast.LENGTH_SHORT).show()
                }
            })


            //settings blue view
            var radius=10f
            val decorView=window.decorView
            val rootView=(decorView.findViewById(android.R.id.content)as ViewGroup?)
            val windowBackground=decorView.background

            rootView?.let {
                blueView.setupWith(it,RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blueView.outlineProvider=ViewOutlineProvider.BACKGROUND
                blueView.clipToOutline=true

            }


            //forecast temp

            weatherViewModel.loadForecastWeather(lot, lon, "metric").enqueue(object : retrofit2.Callback<ForecastResponseApi> {
                override fun onResponse(call: Call<ForecastResponseApi>, response: Response<ForecastResponseApi>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        blueView.visibility = View.VISIBLE

                        data?.let {
                            forecastAdapter.differ.submitList(it.list)
                            forecastView.apply {
                                layoutManager = LinearLayoutManager(this@MainActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                adapter = forecastAdapter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity,t.toString(),Toast.LENGTH_SHORT).show()
                }

            })

        }

    }
    private fun isNightNow():Boolean{
        return calendar.get(Calendar.HOUR_OF_DAY)>=14
    }

    private fun setDynamicallyWallpaper(icon: String):Int{
        return when(icon.dropLast(1)){
            "01"-> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }
            "02","03","04"-> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09","10","11" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.rainy_bg
            }
            "13"-> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }
            "50"-> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else ->0
        }

    }
    private fun setEffectRainSnow(icon: String){
        when(icon.dropLast(1)){
            "01"-> {
                initWeatherView(PrecipType.CLEAR)
            }
            "02","03","04"-> {
                initWeatherView(PrecipType.CLEAR)
            }
            "09","10","11" -> {
                initWeatherView(PrecipType.CLEAR)
            }
            "13"-> {
                initWeatherView(PrecipType.CLEAR)
            }
            "50"-> {
                initWeatherView(PrecipType.CLEAR)
            }
        }

    }

    private fun initWeatherView(type: PrecipType){
        binding.weatherView.apply {
            setWeatherData(type)
            angle=-20
            emissionRate=100.0f
        }
    }
}



