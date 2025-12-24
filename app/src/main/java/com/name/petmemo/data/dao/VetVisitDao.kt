package com.name.petmemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.name.petmemo.data.model.VetVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface VetVisitDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(vetVisit: VetVisit)

    @Query("SELECT * FROM vet_visits WHERE petId = :petId ORDER BY date DESC")
    fun getVetVisitsForPet(petId: Int): Flow<List<VetVisit>>
}