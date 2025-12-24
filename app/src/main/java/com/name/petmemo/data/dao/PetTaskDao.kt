package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.PetTask
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PetTaskDao {
    @Insert
    suspend fun insert(task: PetTask)

    @Update
    suspend fun update(task: PetTask)

    @Delete
    suspend fun delete(task: PetTask)

    @Query("SELECT * FROM tasks WHERE petId = :petId ORDER BY isCompleted ASC, date ASC")
    fun getTasksForPet(petId: Int): Flow<List<PetTask>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<PetTask?>

    @Query("SELECT * FROM tasks WHERE reminderDateTime IS NOT NULL AND date >= :currentDate ORDER BY date ASC")
    fun getUpcomingReminders(currentDate: Long): Flow<List<PetTask>>

    @Query("SELECT * FROM tasks WHERE date >= :currentDate ORDER BY date ASC")
    fun getUpcomingTasks(currentDate: Date): Flow<List<PetTask>>

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, date ASC")
    fun getAllTasks(): Flow<List<PetTask>>

}