package it.pezzotta.coinflow

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

const val simpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val italianDatePattern = "dd/MM/yyyy HH:mm"
const val utcTimeZone = "UTC"
const val italianTimeZone = "Europe/Rome"

fun sdkEqlOrAbv26() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun sdkEqlOrAbv33() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun convertTime(utc: String): String {
    return if (sdkEqlOrAbv26()) {
        convertToItalianTime(utc)
    } else {
        convertToItalianTimeLegacy(utc)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertToItalianTime(utc: String): String =
    Instant.parse(utc)
        .atZone(ZoneId.of(italianTimeZone))
        .format(DateTimeFormatter.ofPattern(italianDatePattern, Locale.ITALY))

private fun convertToItalianTimeLegacy(utc: String): String {
    val sdfUtc = SimpleDateFormat(simpleDateFormat, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(utcTimeZone)
    }

    val sdfLocal = SimpleDateFormat(italianDatePattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(italianTimeZone)
    }

    val date = sdfUtc.parse(utc)
    return sdfLocal.format(date ?: return "")
}

fun findMinAndMax(prices: List<List<Double>>): Pair<Double, Double> {
    val min = prices.minByOrNull { it[1] }?.get(1) ?: Double.MAX_VALUE
    val max = prices.maxByOrNull { it[1] }?.get(1) ?: Double.MIN_VALUE
    return Pair(min, max)
}
