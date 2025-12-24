package com.name.petmemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "vaccinations",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.Companion.CASCADE
    )]
)
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(index = true)
    val petId: Int,
    val date: Date,
    val note: String?,
    val vaccineName: String,
    @ColumnInfo(name = "next_due_date")
    val nextDueDate: Date?
)