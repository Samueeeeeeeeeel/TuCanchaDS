package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    // Se eliminan las @Rule para usar una configuración manual.

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        // Se configura el Dispatcher.Main para usar el hilo del test.
        // Esto es crucial para que el viewModelScope funcione correctamente.
        Dispatchers.setMain(Dispatchers.Unconfined)
        userRepository = mockk(relaxed = true)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        // Se limpia el Dispatcher después de cada test para evitar que los tests se afecten entre sí.
        Dispatchers.resetMain()
    }

    @Test
    fun `submitLogin - con credenciales válidas - el estado es exitoso`() = runTest {
        // Arrange
        val fakeUser = UserEntity(id = 1L, name = "Test", email = "test@test.com", phone = "123", password = "123456")
        coEvery { userRepository.login("test@test.com", "123456") } returns Result.success(fakeUser)

        // Act
        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPassChange("123456")
        viewModel.submitLogin()

        // Assert
        val finalState = viewModel.login.value
        assertTrue(finalState.success)
        assertEquals(fakeUser, finalState.user)
    }

    @Test
    fun `submitLogin - con credenciales inválidas - el estado es de error`() = runTest {
        // Arrange
        coEvery { userRepository.login(any(), any()) } returns Result.failure(Exception("Credenciales inválidas"))

        // Act
        viewModel.onLoginEmailChange("test@test.com")
        viewModel.onLoginPassChange("wrongpass")
        viewModel.submitLogin()

        // Assert
        val finalState = viewModel.login.value
        assertEquals(false, finalState.success)
        assertNotNull(finalState.errorMsg)
    }

    @Test
    fun `submitRegister - con datos válidos - el estado es exitoso`() = runTest {
        // Arrange
        coEvery { userRepository.register(any(), any(), any(), any(), any()) } returns Result.success(1L)

        // Act
        viewModel.onNameChange("Test User")
        viewModel.onPhoneChange("123456789")
        viewModel.onRegisterEmailChange("new@test.com")
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        viewModel.submitRegister()

        // Assert
        val finalState = viewModel.register.value
        assertTrue(finalState.success)
    }

    @Test
    fun `submitRegister - con email duplicado - el estado es de error`() = runTest {
        // Arrange
        coEvery { userRepository.register(any(), any(), any(), any(), any()) } returns Result.failure(IllegalStateException("El correo ya existe"))

        // Act
        viewModel.onNameChange("Test User")
        viewModel.onPhoneChange("123456789")
        viewModel.onRegisterEmailChange("registered@test.com")
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("Password123!")
        viewModel.submitRegister()

        // Assert
        val finalState = viewModel.register.value
        assertEquals(false, finalState.success)
        assertNotNull(finalState.errorMsg)
    }
}