package com.verma.casalarestaurant.model

import org.json.JSONArray

data class OrderHistoryDetail (
    val orderId: String,
    val restaurantName: String,
    val orderTime: String,
    val foodItems: JSONArray
)
