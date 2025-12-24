package com.name.petmemo.data.model

import androidx.room.Embedded
import com.name.petmemo.data.model.MedicalRecord

data class RecordWithNoteCount(
    @Embedded
    val record: MedicalRecord,
    val noteCount: Int
)