package com.hexihexi.hexihexi

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by yurii on 10/16/17.
 */

abstract class BaseViewHolder<in Model>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal abstract fun bind(model: Model)
}
