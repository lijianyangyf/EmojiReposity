package com.example.localshare.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.localshare.data.AppDatabase
import com.example.localshare.data.EmojiEntity
import com.example.localshare.data.EmojiRepository
import kotlinx.coroutines.launch

class EmojiViewModel(application: Application) : AndroidViewModel(application) {
    val repository: EmojiRepository
    val emojis: LiveData<List<EmojiEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        val dao = db.emojiDao()
        repository = EmojiRepository(dao)
        emojis = repository.allEmoji.asLiveData()

        viewModelScope.launch {
            repository.scanAndInsertIfEmpty(application)
        }
    }

    fun refreshFromDisk() {
        viewModelScope.launch {
            repository.scanAndInsertAll(getApplication())
        }
    }
}