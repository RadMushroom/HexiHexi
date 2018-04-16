package com.hexihexi.hexihexi.view

import android.view.View
import com.hexihexi.hexihexi.model.UserSearch
import kotlinx.android.synthetic.main.user_list_item_layout.view.*


class UserListViewHolder(itemView: View, clickListener: OnItemPositionClickListener): BaseViewHolder<UserSearch>(itemView) {

    init {
        itemView.setOnClickListener { clickListener.onItemClicked(adapterPosition) }
    }

    override fun bind(model: UserSearch) {
        with(itemView){
            userEmailTextView.text = model.userEmail
        }
    }
}