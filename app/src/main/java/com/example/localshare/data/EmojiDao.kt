package com.example.localshare.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmojiDao {
    @Query("SELECT * FROM emoji")
    fun getAll(): Flow<List<EmojiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list : List<EmojiEntity>)

    @Query("SELECT COUNT(*) FROM emoji")
    suspend fun count(): Int
}