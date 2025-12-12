package com.example.proyectocancha.data.repository

import android.util.Log
import com.example.proyectocancha.data.local.user.UserDao
import com.example.proyectocancha.data.local.user.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        
        userDao = mockk(relaxed = true) // relaxed = true para no tener que definir todos los comportamientos
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `login con credenciales validas retorna user`() = runBlocking {
        val user = UserEntity(1, "Test User", "test@test.com", "12345678", "password", false)
        coEvery { userDao.getByEmail("test@test.com") } returns user

        val result = userRepository.login("test@test.com", "password")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `login con usuario no encontrado retorna failure`() = runBlocking {
        coEvery { userDao.getByEmail(any()) } returns null

        val result = userRepository.login("noexiste@test.com", "password")

        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas (usuario no encontrado)", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login con contraseña incorrecta retorna failure`() = runBlocking {
        val user = UserEntity(1, "Test User", "test@test.com", "12345678", "password", false)
        coEvery { userDao.getByEmail("test@test.com") } returns user

        val result = userRepository.login("test@test.com", "wrongpassword")

        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas (contraseña incorrecta)", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register con usuario nuevo retorna success`() = runBlocking {
        coEvery { userDao.getByEmail(any()) } returns null
        coEvery { userDao.insert(any()) } returns 1L

        val result = userRepository.register("New User", "new@test.com", "12345678", "password")

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { userDao.insert(any()) }
    }

    @Test
    fun `register con usuario existente retorna failure`() = runBlocking {
        val existingUser = UserEntity(1, "Existing User", "existing@test.com", "12345678", "password", false)
        coEvery { userDao.getByEmail("existing@test.com") } returns existingUser

        val result = userRepository.register("Another User", "existing@test.com", "87654321", "newpassword")

        assertTrue(result.isFailure)
        assertEquals("El correo electrónico ya está registrado.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { userDao.insert(any()) }
    }

    @Test
    fun `getAllUsers retorna lista de usuarios`() = runBlocking {
        val userList = listOf(UserEntity(1, "User 1", "user1@test.com", "11111111", "pass1", false), UserEntity(2, "User 2", "user2@test.com", "22222222", "pass2", true))
        coEvery { userDao.getAll() } returns userList

        val result = userRepository.getAllUsers()

        assertEquals(2, result.size)
        assertEquals("User 1", result[0].name)
    }

    @Test
    fun `countUsers retorna numero de usuarios`() = runBlocking {
        coEvery { userDao.count() } returns 5

        val result = userRepository.countUsers()

        assertEquals(5, result)
    }
}