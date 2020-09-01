package com.verma.casalarestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.model.RestaurantDetail

class CartAdapter(val context: Context, private val foodList: ArrayList<RestaurantDetail>): RecyclerView.Adapter<CartAdapter.CartViewHolder>(){


class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
    val txtFoodName : TextView =view.findViewById(R.id.txtCartFoodName)
    val txtFoodCost : TextView=view.findViewById(R.id.txtCartFoodCost)
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_detail_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartFoodList = foodList[position]
        holder.txtFoodName.text=cartFoodList.foodName
        val cost="Rs.${cartFoodList.CostForOne}"
        holder.txtFoodCost.text=cost
    }

}