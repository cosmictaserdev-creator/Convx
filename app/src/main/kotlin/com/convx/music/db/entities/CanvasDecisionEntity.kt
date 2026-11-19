package com.convx.music.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "canvas_decision")
data class CanvasDecisionEntity(@PrimaryKey val mediaId: String, val enabled: Boolean)
