package ru.neosvet.neonasa.repository.room

import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM AsteroidEntity WHERE name=:name")
    fun get(name: String): AsteroidEntity?

    @Query("SELECT * FROM AsteroidEntity WHERE updated=:date ORDER BY position")
    fun getDateList(date: Long): List<AsteroidEntity>?

    @Query("SELECT * FROM AsteroidEntity WHERE marked=1")
    fun getMarkedList(): List<AsteroidEntity>?

    @Query("SELECT * FROM AsteroidEntity WHERE name LIKE :filter OR note LIKE :filter")
    fun getFilteredList(filter: String): List<AsteroidEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(entity: AsteroidEntity)

    @Update
    fun update(entity: AsteroidEntity)

    @Delete
    fun delete(entity: AsteroidEntity)
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM GroupEntity WHERE date=:date")
    fun get(date: Long): GroupEntity?

    @Query("SELECT * FROM GroupEntity")
    fun getList(): List<GroupEntity>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: GroupEntity)

    @Update
    fun update(entity: GroupEntity)

    @Delete
    fun delete(entity: GroupEntity)
}
