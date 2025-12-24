package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.name.petmemo.data.model.Vaccination
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(vaccination: Vaccination)

    @Query("SELECT * FROM vaccinations WHERE petId = :petId ORDER BY date DESC")
    fun getVaccinationsForPet(petId: Int): Flow<List<Vaccination>>
}