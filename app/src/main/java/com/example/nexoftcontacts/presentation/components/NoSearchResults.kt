package com.example.nexoftcontacts.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexoftcontacts.R

object Variables {
    val headlineColor: Color = Color(0xFF202020)
    val subtitleColor: Color = Color(0xFF3D3D3D)
}

@Composable
fun NoSearchResults(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 46.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Background ellipse with search icon
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Background ellipse
            Image(
                painter = painterResource(id = R.drawable.ellipse),
                contentDescription = "Background",
                contentScale = ContentScale.None
            )
            
            // Search icon on top with 20dp padding from ellipse edges
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search icon",
                contentScale = ContentScale.None,
                modifier = Modifier.padding(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // "No Results" text
        Text(
            text = "No Results",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight(700),
                color = Variables.headlineColor,
                textAlign = TextAlign.Center,
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Description text
        Text(
            text = "The user you are looking for could not be found.",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(500),
                color = Variables.subtitleColor,
                textAlign = TextAlign.Center,
            )
        )
    }
}
