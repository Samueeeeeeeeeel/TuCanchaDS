package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChangePasswordViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: ChangePasswordViewModel

    private val fakeUser = UserEntity(id = 1L, name = "Test User", email = "test@test.com", phone = "123", password = "currentPassword123")

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        userRepository = mockk(relaxed = true)
        viewModel = ChangePasswordViewModel(userRepository)
        // Mockeamos el AuthManager para todos los tests
        mockkObject(AuthManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // Limpiamos el mock de AuthManager
    }

    @Test
    fun `changePassword - con datos válidos - actualiza la contraseña con éxito`() = runTest {
        // Arrange
        coEvery { AuthManager.currentUser.value } returns fakeUser
        coEvery { userRepository.updateUser(any()) } returns Result.success(Unit)

        // Act
        viewModel.onCurrentPasswordChange("currentPassword123")
        viewModel.onNewPasswordChange("newValidPassword123!")
        viewModel.onConfirmPasswordChange("newValidPassword123!")
        viewModel.changePassword()

        // Assert
        val finalState = viewModel.uiState.value
        assertTrue(finalState.success)
        assertNull(finalState.error)

        // Verificamos que se llamó al repositorio con el usuario actualizado
        val updatedUser = fakeUser.copy(password = "newValidPassword123!")
        coVerify { userRepository.updateUser(updatedUser) }
        // Y que se actualizó el AuthManager
        coVerify { AuthManager.updateUser(updatedUser) }
    }

    @Test
    fun `changePassword - con contraseña actual incorrecta - muestra error`() = runTest {
        // Arrange
        coEvery { AuthManager.currentUser.value } returns fakeUser

        // Act
        viewModel.onCurrentPasswordChange("wrongPassword")
        viewModel.onNewPasswordChange("newValidPassword123!")
        viewModel.onConfirmPasswordChange("newValidPassword123!")
        viewModel.changePassword()

        // Assert
        val finalState = viewModel.uiState.value
        assertEquals(false, finalState.success)
        assertNotNull(finalState.currentPasswordError)
        assertEquals("La contraseña actual es incorrecta.", finalState.currentPasswordError)
    }

    @Test
    fun `changePassword - cuando el repositorio falla - muestra error`() = runTest {
        // Arrange
        coEvery { AuthManager.currentUser.value } returns fakeUser
        coEvery { userRepository.updateUser(any()) } returns Result.failure(Exception("Error de BD"))

        // Act
        viewModel.onCurrentPasswordChange("currentPassword123")
        viewModel.onNewPasswordChange("newValidPassword123!")
        viewModel.onConfirmPasswordChange("newValidPassword123!")
        viewModel.changePassword()

        // Assert
        val finalState = viewModel.uiState.value
        assertEquals(false, finalState.success)
        assertNotNull(finalState.error)
        assertEquals("Error al actualizar la contraseña.", finalState.error)
    }
}
