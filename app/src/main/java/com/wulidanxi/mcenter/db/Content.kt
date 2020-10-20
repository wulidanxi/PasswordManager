package com.wulidanxi.mcenter.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Content(
    var channel: String,
    var account: String,
    var password: String,
    var date: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}