package com.hexihexi.hexihexi.view

import android.support.v7.widget.RecyclerView
import java.util.*

abstract class BaseRecyclerAdapter<Model>(protected var data: MutableList<Model> = mutableListOf()):
        RecyclerView.Adapter<BaseViewHolder<Model>>() {

    override fun onBindViewHolder(holder: BaseViewHolder<Model>, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun addItem(model: Model) {
        data.add(model)
        notifyItemInserted(data.size - 1)
    }

    fun addItems(data: List<Model>) {
        val startIndex = this.data.size
        Collections.copy(this.data, data)
        notifyItemRangeInserted(startIndex, data.size)
    }

    fun updateItem(model: Model, position: Int) {
        if (data.size >= position) {
            data[position] = model
            notifyItemChanged(position)
        }
    }

    fun getItem(position: Int): Model? = if (data.size > position) data[position] else null
}
