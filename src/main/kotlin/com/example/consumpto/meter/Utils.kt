package com.example.consumpto.meter

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

fun BigDecimal.currencyScale(): BigDecimal = this.setScale(2, RoundingMode.HALF_UP)

fun String.capitalize() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun LocalDate.toYearMonth(): YearMonth = YearMonth.of(this.year, this.month)

fun YearMonth.toYearMonthLocalizedString(): String {
    val monthName = this.month
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
        .capitalize()

    return "$monthName ${this.year}"
}
