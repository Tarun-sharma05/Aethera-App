package com.example.aethera.presentation.screens

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aethera.presentation.viewModel.AppViewModel

@Composable
fun homeScreen(
    viewModel: AppViewModel = hiltViewModel()
) {

    val state = viewModel.getAllCategoryState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllCategory()
    }

    Column(modifier = Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        LazyRow {
            items(state.value.data){
                Text(text = it!!.name)
                Text(text = it.imageUrl.toString())

            }
        }
    }
}