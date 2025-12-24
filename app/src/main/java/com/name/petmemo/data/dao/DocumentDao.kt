package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.name.petmemo.data.model.Document
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert
    suspend fun insert(document: Document)
    @Query("SELECT * FROM documents WHERE petId = :petId ORDER BY dateAdded DESC")
    fun getDocumentsForPet(petId: Int): Flow<List<Document>>
    @Query("SELECT * FROM documents ORDER BY dateAdded DESC")
    fun getAllDocuments(): Flow<List<Document>>
    @Delete
    suspend fun delete(document: Document)

}