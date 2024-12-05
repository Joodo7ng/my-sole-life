package com.example.mysolelife.contentsList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef

class BookmarkRVAdapter(val context : Context,
                        val items : ArrayList<ContentModel>,
                        val keyList : ArrayList<String>,
                        val bookmarkIdList : MutableList<String>)
    : RecyclerView.Adapter<BookmarkRVAdapter.Viewholder>() {

    // 2. 데이터를 가져와서 아이템을 RV에 하나하나씩 넣어줌
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item, parent, false)

        Log.d("BookmarkRVAdapter", keyList.toString())
        Log.d("BookmarkRVAdapter", bookmarkIdList.toString())
        return Viewholder(v)

    }

    // 아이템에 있는 내용물들을 Viewholder 클래스에서 넣을 수 있도록 연결
    override fun onBindViewHolder(holder: BookmarkRVAdapter.Viewholder, position: Int) {
        holder.bindItem(items[position], keyList[position])
    }

    // 전체 아이템 개수가 몇개인지
    override fun getItemCount(): Int {
        return items.size
    }


    // content_rv_item.xml에 만들어 놓은 아이템에 데이터를 받아와서 넣어줌
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(item : ContentModel, key : String) {

            // RecyclerView의 item을 클릭하면 연결된 링크로 이동하도록 작성
            itemView.setOnClickListener{
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, ContentShowActivity::class.java)
                intent.putExtra("url", item.webUrl) // intent 넘길때 데이터(url)도 함께 넘김
                itemView.context.startActivity(intent)
            }

            // content_rv_item에 만든 아이템들 itemView로 정의
            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            val imageviewArea = itemView.findViewById<ImageView>(R.id.imageArea)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmarkArea)

            if(bookmarkIdList.contains(key)) { // 들어오는 key값들이 bookmarkList의 정보를 가지고 있는지 확인
                bookmarkArea.setImageResource(R.drawable.bookmark_color) // 가지고 있으면 black
            } else {
                bookmarkArea.setImageResource(R.drawable.bookmark_white)
            }



            contentTitle.text = item.title
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageviewArea)


        }

    }
}