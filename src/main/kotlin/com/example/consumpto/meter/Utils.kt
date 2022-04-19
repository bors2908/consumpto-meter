package com.example.consumpto.meter

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

fun BigDecimal.currencyScale(): BigDecimal = this.setScale(2, RoundingMode.HALF_UP)

fun String.capitalize() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun LocalDate.toYearMonth(): YearMonth = YearMonth.of(this.year, this.month)