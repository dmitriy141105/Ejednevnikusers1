package com.example.spisokdelapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ShopListAdapter(context: Context, private val shopList: List<String>) : ArrayAdapter<String>(context, 0, shopList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }

        val shopName = shopList[position].substringBefore(":") // Получаем только название магазина
        (itemView as TextView).text = shopName

        return itemView!!
    }
}
