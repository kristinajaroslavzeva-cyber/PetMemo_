package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.MedicalRecord
import com.name.petmemo.data.model.RecordNote
import com.name.petmemo.data.model.RecordWithNoteCount
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Insert
    suspend fun insertRecord(record: MedicalRecord)

    @Update
    suspend fun updateRecord(record: MedicalRecord)

    @Delete
    suspend fun deleteRecord(record: MedicalRecord)

    @Query("SELECT * FROM medical_records WHERE petId = :petId ORDER BY date DESC")
    fun getRecordsForPet(petId: Int): Flow<List<MedicalRecord>>

    // --- Функции для работы с заметками ---
    @Insert
    suspend fun insertNote(note: RecordNote)

    @Query("SELECT * FROM record_notes WHERE recordId = :recordId ORDER BY date ASC")
    fun getNotesForRecord(recordId: Int): Flow<List<RecordNote>>

    @Query("""
        SELECT medical_records.*, COUNT(record_notes.id) as noteCount
        FROM medical_records
        LEFT JOIN record_notes ON medical_records.id = record_notes.recordId
        WHERE medical_records.petId = :petId
        GROUP BY medical_records.id
        ORDER BY medical_records.date DESC
    """)
    fun getRecordsWithNoteCount(petId: Int): Flow<List<RecordWithNoteCount>>

    @Query("SELECT * FROM medical_records WHERE id = :recordId")
    fun getRecordById(recordId: Int): Flow<MedicalRecord?>

    @Delete
    suspend fun deleteNote(note: RecordNote)
    @Update
    suspend fun updateNote(note: RecordNote)


}