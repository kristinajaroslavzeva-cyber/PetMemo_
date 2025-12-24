package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_entries WHERE petId = :petId ORDER BY date DESC")
    fun getMoodEntriesForPet(petId: Int): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :entryId")
    suspend fun getMoodEntryById(entryId: Int): MoodEntry?

    @Query("DELETE FROM mood_entries WHERE id = :entryId")
    suspend fun deleteMoodEntryById(entryId: Int)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entry: MoodEntry) // <-- ИМЯ МЕТОДА 'insert'

    @Query("SELECT * FROM mood_entries WHERE petId = :petId ORDER BY date DESC")
    fun getEntriesForPet(petId: Int): Flow<List<MoodEntry>> // <-- ИМЯ МЕТОДА 'getEntriesForPet'

    @Query("SELECT * FROM mood_entries WHERE petId = :petId AND date = :date LIMIT 1")
    suspend fun getMoodEntryForDate(petId: Int, date: Long): MoodEntry?

    @Update
    suspend fun updateMoodEntry(entry: MoodEntry)
    @Query("SELECT * FROM mood_entries WHERE petId = :petId AND date BETWEEN :startOfDay AND :endOfDay LIMIT 1")
    suspend fun getMoodEntryForDay(petId: Int, startOfDay: Long, endOfDay: Long): MoodEntry?

    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)



}