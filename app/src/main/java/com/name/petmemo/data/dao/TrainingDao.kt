package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.TrainingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert(log: TrainingLog)

    @Update
    suspend fun update(log: TrainingLog)

    @Delete
    suspend fun delete(log: TrainingLog)

    @Query("SELECT * FROM training_logs WHERE petId = :petId")
    fun getLogsForPet(petId: Int): Flow<List<TrainingLog>>

    @Query("SELECT * FROM training_logs ORDER BY date DESC")
    fun getAllTrainingLogs(): Flow<List<TrainingLog>>

    @Query("SELECT * FROM training_logs WHERE id = :logId")
    fun getLogById(logId: Int): Flow<TrainingLog?>
}