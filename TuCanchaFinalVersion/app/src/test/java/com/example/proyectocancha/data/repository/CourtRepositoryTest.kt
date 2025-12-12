package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.court.CourtDao
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CourtRepositoryTest {

    private lateinit var courtDao: CourtDao
    private lateinit var courtRepository: CourtRepository

    @Before
    fun setUp() {
        courtDao = mockk(relaxed = true)
        courtRepository = CourtRepository(courtDao)
    }

    @Test
    fun `getAllCourts retorna lista de canchas`() = runBlocking {
        val courts = listOf(
            CourtEntity(id = 1, name = "Cancha de Tenis", price = 15.0, imageUrl = "url_tenis", description = "Superficie de arcilla."),
            CourtEntity(id = 2, name = "Cancha de Fútbol 5", price = 25.0, imageUrl = "url_futbol", description = "Césped sintético de última generación.")
        )
        coEvery { courtDao.getAll() } returns courts

        val result = courtRepository.getAllCourts()

        assertEquals(2, result.size)
        assertEquals("Cancha de Tenis", result[0].name)
    }

    @Test
    fun `getCourtById con id valido retorna cancha`() = runBlocking {
        val court = CourtEntity(id = 1, name = "Cancha de Tenis", price = 15.0, imageUrl = "url_tenis", description = "Superficie de arcilla.")
        coEvery { courtDao.getById(1) } returns court

        val result = courtRepository.getCourtById(1)

        assertEquals(court, result)
    }

    @Test
    fun `getCourtById con id invalido retorna null`() = runBlocking {
        coEvery { courtDao.getById(99) } returns null

        val result = courtRepository.getCourtById(99)

        assertEquals(null, result)
    }

    @Test
    fun `insertCourt llama a dao insert`() = runBlocking {
        val newCourt = CourtEntity(name = "Cancha de Pádel", price = 12.0, imageUrl = "url_padel", description = "Paredes de cristal.")
        
        courtRepository.insertCourt(newCourt)

        coVerify { courtDao.insert(newCourt) }
    }

    @Test
    fun `updateCourt llama a dao update`() = runBlocking {
        val courtToUpdate = CourtEntity(id = 1, name = "Cancha de Tenis", price = 18.0, imageUrl = "url_tenis_new", description = "Superficie de arcilla renovada.")

        courtRepository.updateCourt(courtToUpdate)

        coVerify { courtDao.update(courtToUpdate) }
    }

    @Test
    fun `deleteCourt llama a dao delete`() = runBlocking {
        val courtToDelete = CourtEntity(id = 1, name = "Cancha de Tenis", price = 15.0, imageUrl = "url_tenis", description = "Superficie de arcilla.")

        courtRepository.deleteCourt(courtToDelete)

        coVerify { courtDao.delete(courtToDelete) }
    }
}