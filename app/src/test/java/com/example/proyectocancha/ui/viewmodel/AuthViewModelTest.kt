package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitLogin con credenciales validas actualiza estado a success`() = runTest {
        val email = "test@test.com"
        val password = "Password123"
        val user = UserEntity(1, "Test User", email, "12345678", password, false)

        coEvery { userRepository.login(email, password) } returns Result.success(user)

        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(password)
        viewModel.submitLogin()

        advanceUntilIdle() // Avanza el dispatcher para completar la coroutine

        val uiState = viewModel.login.value
        assertTrue(uiState.success)
        assertFalse(uiState.isSubmitting)
        assertNull(uiState.errorMsg)
        assertEquals(user, uiState.user)
        assertFalse(uiState.isAdmin)
    }

    @Test
    fun `submitLogin con credenciales invalidas actualiza estado a error`() = runTest {
        val email = "test@test.com"
        val password = "wrongpassword"
        val errorMessage = "Credenciales inválidas"

        coEvery { userRepository.login(email, password) } returns Result.failure(Exception(errorMessage))

        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPassChange(password)
        viewModel.submitLogin()

        advanceUntilIdle()

        val uiState = viewModel.login.value
        assertFalse(uiState.success)
        assertFalse(uiState.isSubmitting)
        assertEquals(errorMessage, uiState.errorMsg)
        assertNull(uiState.user)
    }

    @Test
    fun `submitRegister con datos validos actualiza estado a success`() = runTest {
        val name = "New User"
        val email = "new@test.com"
        val phone = "12345678"
        val password = "Password123!"

        coEvery { userRepository.register(name, email, phone, password, false) } returns Result.success(1L)

        viewModel.onNameChange(name)
        viewModel.onRegisterEmailChange(email)
        viewModel.onPhoneChange(phone)
        viewModel.onRegisterPassChange(password)
        viewModel.onConfirmChange(password)

        assertTrue("El botón de registro debería estar habilitado", viewModel.register.value.canSubmit)

        viewModel.submitRegister()

        advanceUntilIdle()

        val uiState = viewModel.register.value
        assertTrue(uiState.success)
        assertFalse(uiState.isSubmitting)
        assertNull(uiState.errorMsg)
    }

    @Test
    fun `submitRegister con email existente actualiza estado a error`() = runTest {
        val name = "Existing User"
        val email = "existing@test.com"
        val phone = "12345678"
        val password = "Password123!"
        val errorMessage = "El correo electrónico ya está registrado."

        coEvery { userRepository.register(name, email, phone, password, false) } returns Result.failure(Exception(errorMessage))

        viewModel.onNameChange(name)
        viewModel.onRegisterEmailChange(email)
        viewModel.onPhoneChange(phone)
        viewModel.onRegisterPassChange(password)
        viewModel.onConfirmChange(password)

        viewModel.submitRegister()

        advanceUntilIdle()

        val uiState = viewModel.register.value
        assertFalse(uiState.success)
        assertFalse(uiState.isSubmitting)
        assertEquals(errorMessage, uiState.errorMsg)
    }

    @Test
    fun `onRegisterPassChange con contraseñas que no coinciden actualiza error`() = runTest {
        viewModel.onRegisterPassChange("Password123!")
        viewModel.onConfirmChange("PasswordDiferente123!")

        val uiState = viewModel.register.value
        assertNotNull(uiState.confirmError)
        assertEquals("Las contraseñas no coinciden", uiState.confirmError)
        assertFalse(uiState.canSubmit)
    }
}