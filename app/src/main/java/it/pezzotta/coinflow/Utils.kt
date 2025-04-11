package it.pezzotta.coinflow

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
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

fun convertToItalianTime(utc: String): String {
    return if (sdkEqlOrAbv26()) {
        convertToItalianTimeAbv26(utc)
    } else {
        convertToItalianTimeLwr26(utc)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertToItalianTimeAbv26(utc: String): String =
    Instant.parse(utc).atZone(ZoneId.of(italianTimeZone))
        .format(DateTimeFormatter.ofPattern(italianDatePattern, Locale.ITALY))

private fun convertToItalianTimeLwr26(utc: String): String {
    val sdfUtc = SimpleDateFormat(simpleDateFormat, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(utcTimeZone)
    }

    val sdfLocal = SimpleDateFormat(italianDatePattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(italianTimeZone)
    }

    val date = sdfUtc.parse(utc)
    return sdfLocal.format(date ?: return "")
}

fun getNumberOfDays(startMillis: Long): List<String> {
    val startCal = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = startMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val endCal = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val days = mutableListOf<String>()
    while (!startCal.after(endCal)) {
        if (startCal.get(Calendar.DAY_OF_MONTH).toString().length == 1) {
            days.add("0" + startCal.get(Calendar.DAY_OF_MONTH))
            startCal.add(Calendar.DATE, 1)
        } else {
            days.add(startCal.get(Calendar.DAY_OF_MONTH).toString())
            startCal.add(Calendar.DATE, 1)
        }
    }

    return days
}

fun findMinAndMax(prices: List<List<Double>>): Pair<Double, Double> {
    val min = prices.minByOrNull { it[1] }?.get(1) ?: Double.MAX_VALUE
    val max = prices.maxByOrNull { it[1] }?.get(1) ?: Double.MIN_VALUE
    return Pair(min, max)
}

fun Modifier.noRippleClickable(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

fun Double.prettyFormat(): String {
    val symbols = DecimalFormatSymbols().apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    return DecimalFormat("#,##0.00", symbols).format(this)
}
