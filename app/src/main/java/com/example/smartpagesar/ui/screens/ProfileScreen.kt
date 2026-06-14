package com.example.smartpagesar.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.User
import com.example.smartpagesar.ui.composables.MainTopAppBar

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

fun ProfileScreen(
    navController: NavController,
    user: User?,
    onLogout: () -> Unit
){

    Scaffold(
        topBar = { MainTopAppBar(navController, stringResource(R.string.profile_title), true, {}) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (user != null && user.name != null){
                    Text(user.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        fontSize = 24.sp)
                }else{
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(25.dp)
                            .padding(5.dp),
                        strokeWidth = 2.dp)
                }
            }
            item {
                if (user != null && user.role != null){
                    Text(user.role,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        fontSize = 24.sp)
                }else{
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(25.dp)
                            .padding(5.dp),
                        strokeWidth = 2.dp)
                }
            }
            item {
                Button(
                    onClick = onLogout,
                ) {
                    Text("logout")
                }
            }


        }
    }

}