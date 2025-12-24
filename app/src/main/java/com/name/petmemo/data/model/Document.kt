package com.name.petmemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val name: String,
    val filePath: String,
    val dateAdded: Date
)