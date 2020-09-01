package com.verma.casalarestaurant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestDao {
    @Insert
    fun insertRest(restEntity: RestEntity)

    @Delete
    fun deleteRest(restEntity: RestEntity)

    @Query("SELECT * FROM rests")
    fun getAllRests(): List<RestEntity>

    @Query("SELECT * FROM rests WHERE rest_id= :restID")
    fun getRestByID(restID: String): RestEntity

}