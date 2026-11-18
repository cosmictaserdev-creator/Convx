package com.convx.music.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.convx.music.db.entities.CanvasDecisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvasDecisionDao {
    @Query("SELECT * FROM canvas_decision WHERE mediaId = :mediaId")
    fun observe(mediaId: String): Flow<CanvasDecisionEntity?>

    @Upsert
    suspend fun upsert(decision: CanvasDecisionEntity)
}
