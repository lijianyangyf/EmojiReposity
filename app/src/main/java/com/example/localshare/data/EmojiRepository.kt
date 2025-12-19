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

    suspend fun scanAndInsertAll(context: Context) = withContext(Dispatchers.IO){
        val emojiDir= getEmojiDir(context)
        if (!emojiDir.exists()){
            emojiDir.mkdirs()
            return@withContext
        }

        val exts = listOf("png","jpg","jpeg","gif","webp")

        val diskFiles = emojiDir.listFiles { file ->
            val name = file.name.lowercase()
            exts.any { ext-> name.endsWith(ext) }
        } ?: emptyArray()

        if(diskFiles.isEmpty()) return@withContext

        val dbPathSet = emojiDao.getAllPaths().toHashSet()

        val newEntities = diskFiles.filter { file ->
            !dbPathSet.contains(file.absolutePath)
        }. map{ file ->
            EmojiEntity(
                filePath = file.absolutePath,
                desc = file.nameWithoutExtension
            )
        }

        if (newEntities.isNotEmpty()){
            emojiDao.insertAll(newEntities)
        }
    }

    suspend fun deleteEmojis(emojis: List<EmojiEntity>) = withContext(Dispatchers.IO) {
        emojis.forEach { entity ->
            try {
                val file = File(entity.filePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        emojiDao.deleteList(emojis)
    }

    fun getEmojiDir(context: Context): File {
        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(baseDir, "emoji")
    }
}