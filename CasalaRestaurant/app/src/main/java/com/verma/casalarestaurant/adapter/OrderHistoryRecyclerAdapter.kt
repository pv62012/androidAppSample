package com.verma.casalarestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.model.FoodItems
import com.verma.casalarestaurant.model.OrderHistoryDetail
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderHistoryRecyclerAdapter(val context: Context,
                                  private val orderHistoryList: List<OrderHistoryDetail>): RecyclerView
                                 .Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {
    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restNameHistory: TextView = view.findViewById(R.id.txtRestName)
        val orderTime: TextView = view.findViewById(R.id.txtOrderTime)
        val reyclerResHistory:RecyclerView=view.findViewById(R.id.recyclerResHistory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val history = orderHistoryList[position]
        holder.restNameHistory.text = history.restaurantName
        holder.orderTime.text = history.orderTime
        setUpRecycler(holder.reyclerResHistory, history)
    }



private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderHistoryDetail) {
    val foodItemsList = ArrayList<FoodItems>()
    for (i in 0 until orderHistoryList.foodItems.length()) {
        val foodJson = orderHistoryList.foodItems.getJSONObject(i)
        foodItemsList.add(
            FoodItems(
                foodJson.getString("food_item_id"),
                foodJson.getString("name"),
                foodJson.getString("cost")
            )
        )
    }
    val cartItemAdapter = CartItemAdapter(context, foodItemsList)
    val mLayoutManager = LinearLayoutManager(context)
    recyclerResHistory.layoutManager = mLayoutManager
    recyclerResHistory.itemAnimator = DefaultItemAnimator()
    recyclerResHistory.adapter = cartItemAdapter
}

private fun formatDate(dateString: String): String? {
    val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
    val date: Date = inputFormatter.parse(dateString) as Date

    val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return outputFormatter.format(date)
}


}