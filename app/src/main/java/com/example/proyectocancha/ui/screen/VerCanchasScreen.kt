package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.CourtRepository
import com.example.proyectocancha.ui.components.CourtSmallCard
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.dummyCourts
import com.example.proyectocancha.ui.viewmodel.CourtListViewModel
import com.example.proyectocancha.ui.viewmodel.CourtListViewModelFactory

private const val IS_DEBUG_BUILD = true

@Composable
fun VerCanchasScreen(
    onCourtSelected: (Court) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val repo = remember { CourtRepository(db.courtDao()) }
    val vm: CourtListViewModel = viewModel(factory = CourtListViewModelFactory(repo))

    val courts by vm.courts.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (IS_DEBUG_BUILD) {
                FloatingActionButton(onClick = { vm.insertSample(dummyCourts.first()) }) {
                    Text(text = "Insert")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (courts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay canchas. Usa el botón de depuración para insertar una.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(courts) { court ->
                        CourtSmallCard(
                            court = court,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) { selected ->
                            onCourtSelected(selected)
                        }
                    }
                }
            }
        }
    }
}