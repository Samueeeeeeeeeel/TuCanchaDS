package com.example.proyectocancha.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectocancha.ui.model.dummyCourts

@Composable
fun CourtSmallCard(
    court: Court,
    modifier: Modifier = Modifier,
    onClick: (Court) -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable { onClick(court) },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // versión compatible y clara: si court.imageUrl es 0 usamos placeholder
            val imageRes = if (court.imageUrl != 0) court.imageUrl else R.drawable.court_1

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = court.name,
                modifier = Modifier.size(88.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = court.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${court.price} CLP",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = court.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun CourtSmallCardPreview() {
    CourtSmallCard(court = dummyCourts.first())
}