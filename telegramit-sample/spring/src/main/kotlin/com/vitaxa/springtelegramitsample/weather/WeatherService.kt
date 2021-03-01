package com.vitaxa.springtelegramitsample.weather

import com.vitaxa.springtelegramitsample.weather.model.Weather
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WeatherService {

    fun getWeather(country: String, city: String): Weather {
        TimeUnit.SECONDS.sleep(2) // Long running task
        return Weather(32.4f, "Clear sky")
    }

}
