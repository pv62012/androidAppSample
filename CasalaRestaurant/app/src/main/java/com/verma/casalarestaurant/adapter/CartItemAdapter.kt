package com.verma.casalarestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.model.FoodItems

class CartItemAdapter(val context: Context,
                      private val foodHistoryList: List<FoodItems>): RecyclerView
.Adapter<CartItemAdapter.CartHistoryViewHolder>() {
    class CartHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFooName:TextView=view.findViewById(R.id.txtCartFoodName)
        val txtFoodCost: TextView=view.findViewById(R.id.txtCartFoodCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_detail_single_row, parent, false)
        return CartHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
       return foodHistoryList.size
    }

    override fun onBindViewHolder(holder: CartHistoryViewHolder, position: Int) {
        val item=foodHistoryList[position]
        holder.txtFooName.text=item.foodName
        holder.txtFoodCost.text=item.foodCost
    }
}