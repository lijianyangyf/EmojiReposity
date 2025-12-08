package com.example.localshare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.localshare.databinding.ActivityPreviewBinding
import java.io.File

class PreviewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = intent.getStringExtra("emoji_path")?:return

        Glide.with(this)
            .load(File(path))
            .fitCenter()
            .into(binding.imagePreview)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}