package com.tucancha.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.tucancha.data.models.Rol
import com.tucancha.data.models.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extensión para crear DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

/**
 * Gestor de sesión de usuario usando DataStore
 * Persiste los datos del usuario logueado
 */
class UserSessionManager(private val context: Context) {
    
    companion object {
        // Claves para DataStore
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_NOMBRE = stringPreferencesKey("nombre")
        private val KEY_APELLIDO = stringPreferencesKey("apellido")
        private val KEY_TELEFONO = stringPreferencesKey("telefono")
        private val KEY_ROL = stringPreferencesKey("rol")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
    
    /**
     * Guardar datos del usuario después del login
     */
    suspend fun saveUserSession(usuario: Usuario) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = usuario.id ?: 0L
            preferences[KEY_EMAIL] = usuario.email
            preferences[KEY_NOMBRE] = usuario.nombre
            preferences[KEY_APELLIDO] = usuario.apellido ?: ""
            preferences[KEY_TELEFONO] = usuario.telefono ?: ""
            preferences[KEY_ROL] = usuario.rol?.name ?: Rol.USUARIO.name
            preferences[KEY_IS_LOGGED_IN] = true
        }
    }
    
    /**
     * Obtener ID del usuario logueado
     */
    val userId: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID] ?: 0L
    }
    
    /**
     * Obtener email del usuario logueado
     */
    val userEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMAIL] ?: ""
    }
    
    /**
     * Verificar si hay un usuario logueado
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }
    
    /**
     * Verificar si el usuario es admin
     */
    val isAdmin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ROL] == Rol.ADMIN.name
    }
    
    /**
     * Obtener datos completos del usuario
     */
    val currentUser: Flow<Usuario?> = context.dataStore.data.map { preferences ->
        val isLogged = preferences[KEY_IS_LOGGED_IN] ?: false
        if (isLogged) {
            Usuario(
                id = preferences[KEY_USER_ID],
                email = preferences[KEY_EMAIL] ?: "",
                password = "", // No guardamos password
                nombre = preferences[KEY_NOMBRE] ?: "",
                apellido = preferences[KEY_APELLIDO],
                telefono = preferences[KEY_TELEFONO],
                rol = try { 
                    Rol.valueOf(preferences[KEY_ROL] ?: Rol.USUARIO.name) 
                } catch (e: Exception) { 
                    Rol.USUARIO 
                }
            )
        } else {
            null
        }
    }
    
    /**
     * Obtener ID de usuario de forma síncrona (usar con cuidado)
     */
    suspend fun getUserIdSync(): Long {
        return context.dataStore.data.first()[KEY_USER_ID] ?: 0L
    }
    
    /**
     * Obtener email para operaciones de admin
     */
    suspend fun getAdminEmail(): String {
        return context.dataStore.data.first()[KEY_EMAIL] ?: ""
    }
    
    /**
     * Cerrar sesión
     */
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

