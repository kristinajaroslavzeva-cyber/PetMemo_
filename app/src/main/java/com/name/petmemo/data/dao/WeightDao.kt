package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Update
    suspend fun update(entry: WeightEntry)

    @Delete
    suspend fun delete(entry: WeightEntry)
    @Query("SELECT * FROM weight_entries WHERE petId = :petId ORDER BY date DESC")
    fun getWeightEntriesForPet(petId: Int): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(entry: WeightEntry)
}