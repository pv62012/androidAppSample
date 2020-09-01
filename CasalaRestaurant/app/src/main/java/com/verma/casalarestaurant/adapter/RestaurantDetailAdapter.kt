package com.verma.casalarestaurant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verma.casalarestaurant.R
import com.verma.casalarestaurant.model.RestaurantDetail

class RestaurantDetailAdapter( val context: Context, private var resdetail: ArrayList<RestaurantDetail>,  private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RestaurantDetailAdapter.RestDetailViewHolder>() {
    companion object {
        var isCartEmpty = true
    }

    class RestDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val serialNo: TextView = view.findViewById(R.id.txtSerialNo)
        val price: TextView = view.findViewById(R.id.txtFoodCost)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button=view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_detail_single_row, parent, false)
        return RestDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return resdetail.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: RestaurantDetail)
        fun onRemoveItemClick(foodItem: RestaurantDetail)
    }

    override fun onBindViewHolder(holder: RestDetailViewHolder, position: Int) {
        val menuObject = resdetail[position]
        holder.txtFoodName.text = menuObject.foodName
        val cost  = "Rs.${menuObject.CostForOne}"
        holder.price.text = cost
        holder.serialNo.text = (position + 1).toString()
        holder.btnAdd.setOnClickListener {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
            listener.onAddItemClick(menuObject)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuObject)
        }

    }
}
