package com.example.proyectocancha.utilities

import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.ui.model.CourtEntity
import com.example.proyectocancha.ui.model.dummyCourts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun seedCourtsIfEmpty(db: AppDatabase) {
    withContext(Dispatchers.IO) {
        try {
            val dao = db.courtDao()
            val existing = dao.getCourtById(1L)
            if (existing == null) {
                val initial = dummyCourts.map { court ->
                    CourtEntity(
                        id = 0L,
                        name = court.name,
                        price = court.price,
                        imageRes = court.imageUrl,
                        imageUri = null,
                        description = court.description
                    )
                }.toTypedArray()
                dao.insertAll(*initial)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}