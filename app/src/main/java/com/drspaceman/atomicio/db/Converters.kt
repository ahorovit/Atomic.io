package com.drspaceman.atomicio.db

import androidx.room.TypeConverter
import com.drspaceman.atomicio.util.DateUtil
import java.time.LocalDate
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun localDateToDateString(localDate: LocalDate?): String?
    {
        return localDate?.toString()
    }

    @TypeConverter
    fun dateStringToLocalDate(dateString: String?): LocalDate?
    {
        return DateUtil.dateStringToLocalDate(dateString)
    }
}
