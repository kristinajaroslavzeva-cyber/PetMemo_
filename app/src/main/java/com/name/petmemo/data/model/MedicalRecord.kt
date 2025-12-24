package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "medical_records")
@TypeConverters(DateConverter::class, StringListConverter::class)
data class MedicalRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val date: Date,
    val title: String,
    val symptoms: String,
    val diagnosis: String?,
    val treatment: String?,
    val vetName: String?,
    val clinicName: String?,
    val notes: String?,
    val attachments: List<String> = emptyList()
)