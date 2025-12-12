package com.example.proyectocancha.data.local.court

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CourtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(court: CourtEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(courts: List<CourtEntity>)

    @Update
    suspend fun update(court: CourtEntity)

    @Delete
    suspend fun delete(court: CourtEntity)

    @Query("SELECT * FROM courts ORDER BY name ASC")
    suspend fun getAll(): List<CourtEntity>

    @Query("SELECT COUNT(*) FROM courts")
    suspend fun count(): Int

    @Query("SELECT * FROM courts WHERE id = :courtId")
    suspend fun getById(courtId: Int): CourtEntity?
}
