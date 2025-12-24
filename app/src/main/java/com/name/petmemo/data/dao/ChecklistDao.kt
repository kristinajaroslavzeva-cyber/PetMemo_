package com.name.petmemo.data.dao

import androidx.room.*
import com.name.petmemo.di.UserChecklist
import com.name.petmemo.di.UserChecklistItem
import kotlinx.coroutines.flow.Flow

data class UserChecklistWithItems(
    @Embedded val checklist: UserChecklist,
    @Relation(
        parentColumn = "id",
        entityColumn = "checklistId"
    )
    val items: List<UserChecklistItem>
)

@Dao
interface ChecklistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: UserChecklist): Long
    @Update
    suspend fun updateChecklist(checklist: UserChecklist)
    @Delete
    suspend fun deleteChecklist(checklist: UserChecklist)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: UserChecklistItem)
    @Update
    suspend fun updateItem(item: UserChecklistItem)
    @Delete
    suspend fun deleteItem(item: UserChecklistItem)
    @Transaction
    @Query("SELECT * FROM user_checklists")
    fun getAllUserChecklistsWithItems(): Flow<List<UserChecklistWithItems>>

}