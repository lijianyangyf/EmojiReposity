package com.example.localshare.data

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class EmojiRepository(private val emojiDao: EmojiDao) {
    val allEmoji:Flow<List<EmojiEntity>> = emojiDao.getAll()
    suspend fun scanAndInsertIfEmpty(context: Context) = withContext(Dispatchers.IO){
        val count = emojiDao.count()
        if (count > 0) return@withContext

        val emojiDir = getEmojiDir(context)
        if (!emojiDir.exists()){
            emojiDir.mkdirs()
        }
        val exts = listOf("png","jpg","jpeg","gif","webp")

        val files = emojiDir.listFiles{file->
            val name = file.name.lowercase()
            exts.any { ext-> name.endsWith(ext) }
        }?: emptyArray()

        val entities = files.map {file ->
            EmojiEntity(
                filePath = file.absolutePath,
                desc = file.nameWithoutExtension
            )
        }

        if (entities.isNotEmpty()) {
            emojiDao.insertAll(entities)
        }
    }

    fun getEmojiDir(context: Context): File {
        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(baseDir, "emoji")
    }
}