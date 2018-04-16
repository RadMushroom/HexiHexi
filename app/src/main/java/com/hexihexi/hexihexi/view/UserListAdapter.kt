package com.hexihexi.hexihexi.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hexihexi.hexihexi.R
import com.hexihexi.hexihexi.model.UserSearch


class UserListAdapter(private val onItemPositionClickListener: OnItemPositionClickListener): BaseRecyclerAdapter<UserSearch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserSearch> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item_layout, parent, false)
        return UserListViewHolder(view, onItemPositionClickListener)
    }

}