package com.example.proyectocancha.data.repository
import com.example.proyectocancha.data.remote.JsonPlaceholderApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostRepositoryTest {
    @Test
    fun fetchPosts_retorna_lista_valida() = runBlocking {
        val api = mockk<JsonPlaceholderApi>() //mock de la api (copia)
        val repo = PostRepository(api)
        //json fake de retorno para el mock de la api
        val sample = listOf(PostDto(1,1,"Hola","Mesaje"))

        //al mock como actuar
        coEvery { api.getPosts() } returns sample
        var result = repo.fetchPosts()
        //criterios de aceptacion
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Hola",result.getOrNull()!![0].title)
    }
}