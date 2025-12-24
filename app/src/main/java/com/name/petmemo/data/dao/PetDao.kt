package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.name.petmemo.data.model.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(pet: Pet)

    @Update
    suspend fun update(pet: Pet)

    @Delete
    suspend fun delete(pet: Pet)

    @Query("SELECT * FROM pets ORDER BY name ASC")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun getPetById(id: Int): Flow<Pet?>

    @Query("SELECT COUNT(id) FROM pets")
    fun getPetCount(): Flow<Int>
}