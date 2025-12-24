package com.name.petmemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.name.petmemo.data.model.Pet
import java.util.Date

@Entity(
    tableName = "vet_visits",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.Companion.CASCADE
    )]
)
data class VetVisit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(index = true)
    val petId: Int,
    val date: Date,
    val note: String?,
    val reason: String,
    val diagnosis: String?,
    val treatment: String?
)