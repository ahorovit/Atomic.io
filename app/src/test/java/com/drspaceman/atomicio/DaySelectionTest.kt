package com.drspaceman.atomicio

import com.drspaceman.atomicio.adapter.DaySelection
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.threeten.bp.DayOfWeek

class DaySelectionTest {
    @Test
    fun test_select_and_deselect_day() {
        val daySelection = DaySelection(0)
        val sunday = DayOfWeek.SUNDAY

        assertThat(daySelection.isDaySelected(sunday)).isFalse()

        daySelection.selectDay(sunday)
        assertThat(daySelection.isDaySelected(sunday)).isTrue()

        daySelection.deselectDay(sunday)
        assertThat(daySelection.isDaySelected(sunday)).isFalse()
    }

    @Test
    fun test_toInt() {
        val daySelection = DaySelection(0)
        daySelection.selectDay(DayOfWeek.MONDAY)
        daySelection.selectDay(DayOfWeek.TUESDAY)
        daySelection.selectDay(DayOfWeek.WEDNESDAY)

        assertThat(daySelection.toInt()).isEqualTo(7)
    }

    @Test
    fun test_fromInt() {
        val daySelection = DaySelection(7)

        assertThat(daySelection.isDaySelected(DayOfWeek.MONDAY)).isTrue()
        assertThat(daySelection.isDaySelected(DayOfWeek.TUESDAY)).isTrue()
        assertThat(daySelection.isDaySelected(DayOfWeek.WEDNESDAY)).isTrue()
        assertThat(daySelection.isDaySelected(DayOfWeek.THURSDAY)).isFalse()
        assertThat(daySelection.isDaySelected(DayOfWeek.FRIDAY)).isFalse()
        assertThat(daySelection.isDaySelected(DayOfWeek.SATURDAY)).isFalse()
        assertThat(daySelection.isDaySelected(DayOfWeek.SUNDAY)).isFalse()
    }
}
