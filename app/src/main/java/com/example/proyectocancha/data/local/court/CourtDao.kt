package com.example.proyectocancha.data.local.court

import androidx.room.*

@Dao
interface CourtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(court: CourtEntity)

    @Update
    suspend fun update(court: CourtEntity)

    @Delete
    suspend fun delete(court: CourtEntity)

    @Query("SELECT * FROM courts ORDER BY name ASC")
    suspend fun getAll(): List<CourtEntity>

    // --- ¡NUEVO! Función para obtener una cancha por su ID ---
    @Query("SELECT * FROM courts WHERE id = :courtId")
    suspend fun getById(courtId: Int): CourtEntity?
}
