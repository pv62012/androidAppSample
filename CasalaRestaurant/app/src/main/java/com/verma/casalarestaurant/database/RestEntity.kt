package com.verma.casalarestaurant.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "rests")
data class RestEntity(
    @PrimaryKey val rest_id :Int,
    @ColumnInfo(name = "rest_name") val restName: String,
    @ColumnInfo(name = "rest_cost_for_one") val restCostforOne:String,
    @ColumnInfo(name = "rest_rating") val restRating: String,
    @ColumnInfo(name = "rest_image") val restImage: String

)