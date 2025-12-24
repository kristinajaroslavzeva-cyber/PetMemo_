package com.name.petmemo.data.dao

import androidx.room.*
import com.name.petmemo.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name ASC") // Убедитесь, что здесь "contacts"
    fun getAllContacts(): Flow<List<Contact>>
}