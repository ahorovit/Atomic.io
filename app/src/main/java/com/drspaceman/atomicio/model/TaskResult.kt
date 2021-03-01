package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.drspaceman.atomicio.model.TaskResultViewData.Companion.STATUS_PENDING
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("taskId"),
        Index("date"),
    ]
)
data class TaskResult(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var taskId: Long,
    var date: LocalDate,
    var name: String,
    var status: String = STATUS_PENDING,
    var startTime: LocalTime,
    var duration: Int,
    var maxVal: Int,
    var resultVal: Int = 0
)


data class TaskResultViewData(
    override var id: Long? = null,
    var taskId: Long,
    var date: LocalDate,
    var name: String,
    var status: String = STATUS_PENDING,
    var startTime: LocalTime,
    var duration: Int,
    var maxVal: Int,
    var resultVal: Int = 0
): BaseViewModel.BaseViewData() {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_COMPLETE = "Complete"
        const val STATUS_CANCELED = "Canceled"
    }

    override fun toModel(): Any {
        return TaskResult(
            id,
            taskId,
            date,
            name,
            status,
            startTime,
            duration,
            maxVal,
            resultVal
        )
    }
}