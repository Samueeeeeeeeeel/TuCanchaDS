package com.example.proyectocancha.data.local.court

// Repositorio para las canchas. Actúa como intermediario y centraliza el acceso a datos.
class CourtRepository(private val courtDao: CourtDao) {

    suspend fun getAllCourts(): List<CourtEntity> {
        return courtDao.getAll()
    }

    suspend fun getCourtById(courtId: Int): CourtEntity? {
        return courtDao.getById(courtId)
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
