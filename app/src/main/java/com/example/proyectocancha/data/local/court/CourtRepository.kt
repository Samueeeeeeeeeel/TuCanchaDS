package com.example.proyectocancha.data.local.court

class CourtRepository(private val courtDao: CourtDao) {

    suspend fun getAllCourts(): List<CourtEntity> {
        return courtDao.getAll()
    }

    suspend fun insertCourt(court: CourtEntity) {
        courtDao.insert(court)
    }

    suspend fun updateCourt(court: CourtEntity) {
        courtDao.update(court)
    }

    suspend fun deleteCourt(court: CourtEntity) {
        courtDao.delete(court)
    }
}
