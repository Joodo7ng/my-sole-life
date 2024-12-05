package com.example.mysolelife.contentsList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ContentListActivity : AppCompatActivity() {

    lateinit var myRef : DatabaseReference

    val bookmarkIdList = mutableListOf<String>() // 북마크 id를 저장할 리스트, dataModel.key 에서 나오는 data 저장

    lateinit var rvAdapter: ContentRVAdapter // 어댑터 동기화 하려고

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val items = ArrayList<ContentModel>() // 1. 데이터 집어 넣기 Activity > Adapter
        val itemKeyList = ArrayList<String>() // 북마크로 추가하는 content Id 저장할 리스트
        rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkIdList) // 어댑터 생성, Context 추가

        // Write a message to the database
        val database = Firebase.database

        val category = intent.getStringExtra("category") // name으로 카테고리를 받으면 그 값이 category1 혹은 2임


        if(category == "category1") {
            myRef = database.getReference("contents")

        } else if(category == "category2") {
            myRef = database.getReference("contents2")
        }

        // firebase에서 데이터를 읽어옴
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children) {
                    Log.d("ContentListActivity", dataModel.toString())
                    Log.d("ContentListActivity", dataModel.key.toString()) // 모델의 키 값 로그 찍음
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(dataModel.key.toString()) // data의 key값(content id)을 넣어줌
                }
                rvAdapter.notifyDataSetChanged()
                Log.d("ContentListActivity", items.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)


        val rv : RecyclerView = findViewById(R.id.rv) // 어댑터를 생성하여 어댑터와 연결


        // 3. activity.xml Rv에 받은 아이템을 넣어줌

        rv.adapter = rvAdapter // activity에 있는 rvAdapter가 내가 만든 어댑터라고 선언

        //  rv.layoutManager = LinearLayoutManager(this)
        rv.layoutManager = GridLayoutManager(this,2) // 아이템들이 2개씩 나오도록

        getBookmarkData()

    }


    // firebase에서 북마크 정보를 읽어옴
    private fun getBookmarkData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkIdList.clear() // 북마크 데이터가 변경될 때 bookmarkIdList 초기화 (데이터 쌓이지 않게)

                for(dataModel in dataSnapshot.children) {
                    bookmarkIdList.add(dataModel.key.toString())
//                    Log.d("getBookmarkData", dataModel.key.toString())
//                    Log.d("getBookmarkData", dataModel.toString())
                }
                Log.d("Bookmark : ", bookmarkIdList.toString()) // 북마크 리스트 로그 확인
                rvAdapter.notifyDataSetChanged() // 어댑터 동기화 (바뀌는 데이터 업데이트)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

}


