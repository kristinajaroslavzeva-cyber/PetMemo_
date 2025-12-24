package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)
    @Update
    suspend fun update(expense: Expense)
    @Delete
    suspend fun delete(expense: Expense)
    @Query("SELECT * FROM expenses WHERE petId = :petId ORDER BY date DESC")
    fun getExpensesForPet(petId: Int): Flow<List<Expense>>
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    @Query("SELECT DISTINCT category FROM expenses ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Int): Flow<Expense?>
    @Delete
    suspend fun deleteExpense(expense: Expense)
}