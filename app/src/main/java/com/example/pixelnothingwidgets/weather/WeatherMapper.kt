package com.example.pixelnothingwidgets.weather

fun mapWeatherCode(code: String): WeatherCode {
    return when (code) {
        "100" -> WeatherCode.CLEAR_DAY
        "150" -> WeatherCode.CLEAR_NIGHT
        "101", "102", "103", "104" -> WeatherCode.PARTLY_CLOUDY_DAY
        "151", "152", "153", "154" -> WeatherCode.PARTLY_CLOUDY_NIGHT
        "105", "106", "107", "108" -> WeatherCode.CLOUDY
        "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "317", "318" -> WeatherCode.LIGHT_RAIN
        "399" -> WeatherCode.MODERATE_RAIN
        "320", "321", "322", "323", "324", "325", "326", "327", "328", "329", "330", "331", "332", "333", "334", "335", "336", "337", "338" -> WeatherCode.HEAVY_RAIN
        "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415", "416", "417", "418", "419", "420", "421", "422", "423", "424", "425", "426", "427", "428", "429", "430", "431", "432", "433", "434", "435", "436", "437", "438", "439" -> WeatherCode.SNOW
        "500", "501", "502", "503", "504", "507", "508" -> WeatherCode.FOG
        "600", "601", "602", "603", "604", "605", "606", "607", "608", "609", "610", "611", "612", "613", "614", "615", "616", "617", "618" -> WeatherCode.WINDY
        else -> WeatherCode.UNKNOWN
    }
}

fun getWeatherConditionText(code: WeatherCode): String {
    return when (code) {
        WeatherCode.CLEAR_DAY -> "Sunny"
        WeatherCode.CLEAR_NIGHT -> "Clear Night"
        WeatherCode.PARTLY_CLOUDY_DAY -> "Partly Cloudy"
        WeatherCode.PARTLY_CLOUDY_NIGHT -> "Partly Cloudy"
        WeatherCode.CLOUDY -> "Cloudy"
        WeatherCode.LIGHT_RAIN -> "Light Rain"
        WeatherCode.MODERATE_RAIN -> "Moderate Rain"
        WeatherCode.HEAVY_RAIN -> "Heavy Rain"
        WeatherCode.SNOW -> "Snow"
        WeatherCode.FOG -> "Fog"
        WeatherCode.WINDY -> "Windy"
        WeatherCode.UNKNOWN -> "Unknown"
    }
}

fun getNothingStyleWeatherText(code: WeatherCode): String {
    return when (code) {
        WeatherCode.CLEAR_DAY -> "SUNNY"
        WeatherCode.CLEAR_NIGHT -> "CLEAR"
        WeatherCode.PARTLY_CLOUDY_DAY -> "PARTLY CLOUDY"
        WeatherCode.PARTLY_CLOUDY_NIGHT -> "PARTLY CLOUDY"
        WeatherCode.CLOUDY -> "CLOUDY"
        WeatherCode.LIGHT_RAIN -> "LIGHT RAIN"
        WeatherCode.MODERATE_RAIN -> "RAIN"
        WeatherCode.HEAVY_RAIN -> "HEAVY RAIN"
        WeatherCode.SNOW -> "SNOW"
        WeatherCode.FOG -> "FOG"
        WeatherCode.WINDY -> "WINDY"
        WeatherCode.UNKNOWN -> "UNKNOWN"
    }
}
