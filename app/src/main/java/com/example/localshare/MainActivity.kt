package com.example.localshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.localshare.databinding.ActivityMainBinding
import com.example.localshare.ui.EmojiAdapter
import com.example.localshare.ui.EmojiViewModel
import com.example.localshare.ui.PreviewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: EmojiViewModel by viewModels()

    private lateinit var  adapter: EmojiAdapter

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            importEmojis(uris)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EmojiAdapter(
            onItemClick = {emoji->
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("emoji_path", emoji.filePath)
            startActivity(intent)
            },
            onLongClick = {
                enterEditMode()
            }
        )

        binding.recyclerViewEmoji.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            setHasFixedSize(true)
            itemAnimator = null
            this.adapter = this@MainActivity.adapter
        }

        viewModel.emojis.observe(this) { list ->
            adapter.submitList(list)
        }

        binding.btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("image/*"))
        }

        binding.btnCancelDelete.setOnClickListener {
            exitEditMode()
        }

        binding.btnConfirmDelete.setOnClickListener {
            val selected = adapter.getSelectedList()
            if (selected.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("删除表情")
                    .setMessage("你确定要删除 ${selected.size} 张表情吗?")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteSelected(selected)
                        exitEditMode()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            } else {
                Toast.makeText(this,"必须先选择表情才能删除",Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (adapter.isEditMode) {
                    exitEditMode()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun importEmojis(uris: List<Uri>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val emojiDir = viewModel.repository.getEmojiDir(this@MainActivity)
            if (!emojiDir.exists()) {
                emojiDir.mkdirs()
            }

            for (uri in uris) {
                val input = contentResolver.openInputStream(uri) ?: continue

                val originalName =getFileNameFromUri(uri) ?: "${System.currentTimeMillis()}"

                val destFile = File(emojiDir, originalName)

                val sourceHash = calculateHash(input)

                val newInput = contentResolver.openInputStream(uri)?:continue

                if (destFile.exists()) {
                    val dashHash = calculateHash(destFile.inputStream())

                    if (sourceHash == dashHash) {
                        continue
                    } else {
                        newInput.use { inp ->
                            destFile.outputStream().use { out ->
                                inp.copyTo(out)
                            }
                        }
                    }
                } else {
                    newInput.use { inp ->
                        destFile.outputStream().use { out ->
                            inp.copyTo(out)
                        }
                    }
                }
            }
            viewModel.refreshFromDisk()
        }
    }

    private fun getFileNameFromUri(uri:Uri): String?{
        var name: String? = null
        contentResolver.query(uri,arrayOf(OpenableColumns.DISPLAY_NAME),null,null,null)?.use{ cursor ->
            if(cursor.moveToFirst()) {
                name = cursor.getString(0)
            }
        }
        return name
    }

    fun calculateHash(inputStream: InputStream, algorithm: String = "MD5"): String {
        val digest = MessageDigest.getInstance(algorithm)

        return inputStream.use { stream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int

            do {
                bytesRead = stream.read(buffer)
                if (bytesRead > 0) {
                    digest.update(buffer, 0, bytesRead)
                }
            } while (bytesRead != -1)

            val hashBytes = digest.digest()
            BigInteger(1, hashBytes).toString(16).padStart(32, '0')
        }
    }

    private fun enterEditMode() {
        if (adapter.isEditMode) return
        adapter.isEditMode = true
        binding.layoutEditMode.visibility = View.VISIBLE
        binding.btnImport.visibility = View.GONE
    }

    private fun exitEditMode() {
        adapter.isEditMode = false
        binding.layoutEditMode.visibility = View.GONE
        binding.btnImport.visibility = View.VISIBLE
    }
}
