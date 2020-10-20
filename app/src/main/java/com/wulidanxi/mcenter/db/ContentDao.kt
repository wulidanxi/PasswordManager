package com.wulidanxi.mcenter.db

import androidx.room.*

@Dao
interface ContentDao {
    @Insert
    fun insertContent(content: Content): Long

    @Update
    fun updateContent(newContent: Content)

    @Query("select * from Content")
    fun loadAllContent(): List<Content>

    @Query("select * from Content order by channel ")
    fun loadContentOrderByChannel() : List<Content>

    @Query("select * from Content where channel = :channel")
    fun loadContentWithChannel(channel: String): Content

    @Query("select * from Content where id = :id")
    fun loadContentWithId(id: Long): Content

    @Delete
    fun deleteContent(content: Content)

    @Query("delete from Content where account = :account")
    fun deleteContentWithAccount(account: String): Int

    @Query("delete from Content where id = :id")
    fun deleteContentById(id: Long)

}