package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.name.petmemo.data.model.MedicalRecord
import java.util.Date

@Entity(
    tableName = "record_notes",
    foreignKeys = [ForeignKey(
        entity = MedicalRecord::class,
        parentColumns = ["id"],
        childColumns = ["recordId"],
        onDelete = ForeignKey.Companion.CASCADE
    )],
    indices = [Index(value = ["recordId"])]
)
data class RecordNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recordId: Int,
    val date: Date,
    val note: String
)