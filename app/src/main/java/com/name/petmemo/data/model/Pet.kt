package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.name.petmemo.data.model.Gender
import java.util.Date

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val photoUri: String? = null,
    val type: String?,


    val gender: Gender,
    val breed: String?,
    val color: String?,
    val birthDate: Date?,
    val familyDate: Date?,
    val isCastrated: Boolean,
    val hasMicrochip: Boolean,
    val showTrainingLog: Boolean = true,
    val note: String?
)