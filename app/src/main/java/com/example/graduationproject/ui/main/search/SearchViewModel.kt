package com.example.graduationproject.ui.main.search

import androidx.lifecycle.ViewModel
import com.example.graduationproject.repository.BookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(var bookRepo: BookRepo) :ViewModel() {

}