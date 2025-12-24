package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TrainingStatus(val displayName: String) {
    STARTED("Начали"),
    IN_PROGRESS("В процессе"),
    MASTERED("Освоена")
}

@Entity(tableName = "training_logs")
data class TrainingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val date: Date,
    val commandName: String,
    val status: TrainingStatus,
    val notes: String? = null
)