package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val date: Date,
    val weightKg: Double
)