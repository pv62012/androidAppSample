package com.verma.casalarestaurant.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [RestEntity::class, OrderEntity::class], version = 1)
abstract class RestDatabase: RoomDatabase() {
    abstract fun restDao(): RestDao
    abstract fun orderDao() : OrderDao

}