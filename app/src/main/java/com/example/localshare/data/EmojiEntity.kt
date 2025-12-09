package com.example.localshare.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "emoji",
    indices = [androidx.room.Index(value = ["file_path"], unique = true)]
)
data class EmojiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "desc") val desc: String? =null
)
