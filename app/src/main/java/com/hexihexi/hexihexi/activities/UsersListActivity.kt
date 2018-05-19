package com.hexihexi.hexihexi.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hexihexi.hexihexi.R
import com.hexihexi.hexihexi.model.UserSearch
import com.hexihexi.hexihexi.view.OnItemPositionClickListener
import com.hexihexi.hexihexi.view.UserListAdapter
import kotlinx.android.synthetic.main.activity_users_list.*


class UsersListActivity : AppCompatActivity(), OnItemPositionClickListener {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userListAdapter = UserListAdapter(this)
    private var dbRoot = FirebaseDatabase.getInstance().reference
    private var dbUsers = dbRoot.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        userListRecyclerView.adapter = userListAdapter
        dbUsers.addValueEventListener(eventListener)
    }

    override fun onItemClicked(position: Int) {
        val item = userListAdapter.getItem(position)
        dbUsers.child(item?.userId).child("followers").push().setValue(auth.uid)
        Toast.makeText(this, "User ${item?.userEmail} is now followed", Toast.LENGTH_SHORT).show()
    }

   private var eventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (ds in dataSnapshot.children) {
                val email = ds.child("email").getValue(String::class.java)
                val uID = ds.key.toString()
                userListAdapter.addItem(UserSearch(uID, email))
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("Database Error", databaseError.message)
        }

    }
}
