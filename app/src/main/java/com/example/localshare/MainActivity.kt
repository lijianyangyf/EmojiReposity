package com.example.localshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.localshare.databinding.ActivityMainBinding
import com.example.localshare.ui.EmojiAdapter
import com.example.localshare.ui.EmojiViewModel
import com.example.localshare.ui.PreviewActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: EmojiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = EmojiAdapter{emoji->
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("emoji_path", emoji.filePath)
            startActivity(intent)
        }

        binding.recyclerViewEmoji.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            setHasFixedSize(true)
            itemAnimator = null
            this.adapter = adapter
        }

        viewModel.emojis.observe(this) { list ->
            adapter.submitList(list)
        }
    }
}
