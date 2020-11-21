package com.drspaceman.atomicio.adapter

import org.threeten.bp.DayOfWeek

class DaySelection(private val dayBits: Int = 0) {

    private val selectedDays = mutableMapOf<DayOfWeek, Boolean>().apply {
        DAYS.forEach {
            val isSelected = dayBits and getDayMask(it)
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
                result += getDayMask(it)
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

        fun getDayMask(day: DayOfWeek): Int {
            return 1 shl (day.value - 1)
        }
    }
}