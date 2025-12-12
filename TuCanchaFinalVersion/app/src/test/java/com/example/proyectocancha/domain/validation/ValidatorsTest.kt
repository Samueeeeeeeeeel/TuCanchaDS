package com.example.proyectocancha.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `validarEmail con correo valido retorna null`() {
        val result = validarEmail("test@test.com")
        assertEquals(null, result)
    }

    @Test
    fun `validarEmail con correo invalido retorna mensaje de error`() {
        val result = validarEmail("test")
        assertEquals("Formato de Correo no valido", result)
    }

    @Test
    fun `validarEmail con correo vacio retorna mensaje de error`() {
        val result = validarEmail("")
        assertEquals("Por favor, ingresa un correo electrónico válido", result)
    }

    @Test
    fun `validarClaveFuerte con clave valida retorna null`() {
        val result = validarClaveFuerte("Password123!")
        assertEquals(null, result)
    }

    @Test
    fun `validarClaveFuerte con clave sin mayuscula retorna mensaje de error`() {
        val result = validarClaveFuerte("password123!")
        assertEquals("Debe contener al menos una mayúscula", result)
    }

    @Test
    fun `validarClaveFuerte con clave sin minuscula retorna mensaje de error`() {
        val result = validarClaveFuerte("PASSWORD123!")
        assertEquals("Debe contener al menos una minúscula", result)
    }

    @Test
    fun `validarClaveFuerte con clave muy corta retorna mensaje de error`() {
        val result = validarClaveFuerte("Pass1!")
        assertEquals("La contraseña debe tener más de 7 caracteres", result)
    }

    @Test
    fun `validarClaveFuerte con clave sin simbolo retorna mensaje de error`() {
        val result = validarClaveFuerte("Password123")
        assertEquals("Debe contener al menos un símbolo", result)
    }

    @Test
    fun `validarClaveFuerte con clave sin numero retorna mensaje de error`() {
        val result = validarClaveFuerte("Password!")
        assertEquals("Debe contener al menos un número", result)
    }

    @Test
    fun `validarNombreSoloLetras con nombre valido retorna null`() {
        val result = validarNombreSoloLetras("Nombre Apellido")
        assertEquals(null, result)
    }

    @Test
    fun `validarNombreSoloLetras con nombre con numeros retorna mensaje de error`() {
        val result = validarNombreSoloLetras("Nombre123")
        assertEquals("Debe contener solo letras", result)
    }

    @Test
    fun `validarNombreSoloLetras con nombre vacio retorna mensaje de error`() {
        val result = validarNombreSoloLetras("")
        assertEquals("El nombre es obligatorio", result)
    }

    @Test
    fun `validatePhoneisDigitsOnly con telefono valido retorna null`() {
        val result = validatePhoneisDigitsOnly("12345678")
        assertEquals(null, result)
    }

    @Test
    fun `validatePhoneisDigitsOnly con telefono con letras retorna mensaje de error`() {
        val result = validatePhoneisDigitsOnly("1234567a")
        assertEquals("Debe ingresar solo números", result)
    }

    @Test
    fun `validatePhoneisDigitsOnly con telefono muy corto retorna mensaje de error`() {
        val result = validatePhoneisDigitsOnly("1234567")
        assertEquals("Debe tener entre 8 y 9 digitos", result)
    }

    @Test
    fun `validatePhoneisDigitsOnly con telefono muy largo retorna mensaje de error`() {
        val result = validatePhoneisDigitsOnly("1234567890")
        assertEquals("Debe tener entre 8 y 9 digitos", result)
    }

    @Test
    fun `validatePhoneisDigitsOnly con telefono vacio retorna mensaje de error`() {
        val result = validatePhoneisDigitsOnly("")
        assertEquals("Debe escribir su número de teléfono", result)
    }

    @Test
    fun `validarConfirmacion con claves iguales retorna null`() {
        val result = validarConfirmacion("password", "password")
        assertEquals(null, result)
    }

    @Test
    fun `validarConfirmacion con claves diferentes retorna mensaje de error`() {
        val result = validarConfirmacion("password", "diferente")
        assertEquals("Las contraseñas no coinciden", result)
    }

    @Test
    fun `validarConfirmacion con confirmacion vacia retorna mensaje de error`() {
        val result = validarConfirmacion("password", "")
        assertEquals("Debe confirmar su contraseña", result)
    }
}