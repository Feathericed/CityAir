package com.cityair.data.remote

import com.google.gson.annotations.SerializedName

data class WaqiResponse(
    val status: String,
    val data: WaqiData?
)

data class WaqiData(
    val aqi: Int?,
    val idx: Int?,
    val attributions: List<WaqiAttribution>?,
    val city: WaqiCity,

    @SerializedName("dominentpol")
    val dominatPollutant: String?,
    val iaqi: WaqiIaqi?,
    val time: WaqiTime?,
    val forecast: WaqiForecast?,
    val debug: WaqiDebug?
)
data class WaqiAttribution (
    val url: String?,
    val name: String?,
    val logo: String?
)
data class WaqiCity (
    val geo: List<Double>?,
    val name: String?,
    val url: String?,
    val location: String?
)
data class WaqiIaqi (
    val co: WaqiValue?,
    val h: WaqiValue?,
    val no2: WaqiValue?,
    val o3: WaqiValue?,
    val p: WaqiValue?,
    val pm25: WaqiValue?,
    val so2: WaqiValue?,
    val t: WaqiValue?,
    val w: WaqiValue?
)
data class WaqiValue (
    val v: Double?
)
data class WaqiTime (
    val s: String?,
    val tz: String?,
    val v: Long?,
    val iso: String?
)
data class WaqiForecast (
    val daily: WaqiDailyForecast?
)
data class WaqiDailyForecast (
    val o3: List<WaqiForecastItem>?,
    val pm10: List<WaqiForecastItem>?,
    val pm25: List<WaqiForecastItem>?,
    val uvi: List<WaqiForecastItem>?
)
data class WaqiForecastItem (
    val avg: Int?,
    val day: String?,
    val max: Int?,
    val min: Int?
)
data class WaqiDebug (
    val sync: String?
)