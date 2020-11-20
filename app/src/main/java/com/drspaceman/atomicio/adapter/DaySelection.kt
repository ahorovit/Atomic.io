package com.drspaceman.atomicio.adapter

import org.threeten.bp.DayOfWeek

class DaySelection(private val dayBits: Int) {

    private val selectedDays = mutableMapOf<DayOfWeek, Boolean>().apply {
        DAYS.forEach {
            val isSelected = dayBits and (1 shl (it.value - 1))
            put(it, isSelected != 0)
        }
    }

    fun isDaySelected(day: DayOfWeek) = selectedDays[day] ?: false

    fun selectDay(day: DayOfWeek) {
        selectedDays[day] = true
    }

    fun deselectDay(day: DayOfWeek) {
        selectedDays[day] = false
    }

    fun toInt(): Int {
        var result = 0

        DAYS.forEach {
            if (selectedDays[it] == true) {
                result += (1 shl (it.value - 1))
            }
        }

        return result
    }

    companion object {
        val DAYS = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }
}