package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val date: Date,
    val amount: Double,
    val category: String,
    val description: String? = null
)