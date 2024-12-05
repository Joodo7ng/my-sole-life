package com.example.mysolelife.board

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mysolelife.R
import com.example.mysolelife.databinding.ActivityBoardInsideBinding
import com.example.mysolelife.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding : ActivityBoardInsideBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside) // 바인딩 객체 초기화

        // 1-4. intent로 넘어온 데이터 받음
//        val title = intent.getStringExtra("title").toString()
//        val content = intent.getStringExtra("content").toString()
//        val time = intent.getStringExtra("time").toString()
//
//        binding.titleArea.text = title // 바인딩 객체에 데이터 설정
//        binding.textArea.text = content
//        binding.timeArea.text = time

        //2-4. intent로 넘어온 key값 리스트 받음

        val key = intent.getStringExtra("key")

        // 2-5. key값 바탕으로 데이터 가져옴
        getBoardData(key.toString())
        getImageData(key.toString())

    }

    private fun getImageData(key : String){

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.getImageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) {

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else {

            }
        })

    }



    private fun getBoardData(key : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                // 기존에 data 받았던 방법  -> 반복문을 통해 넣음
//                for(dataModel in dataSnapshot.children) {
//
//                    Log.d(TAG, dataModel.toString())
//                    //dataModel.key
//
//                    val item = dataModel.getValue(BoardModel::class.java) // 데이터를 보드모델로 받음
//                    boardDataList.add(item!!)
//                    boardKeyList.add(dataModel.key.toString())  // key값들을 리스트에 넣음
//
//                }
                // 반복문 없이 데이터 받아옴(1개의 key에 대해서만 가져오니까)
                val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                Log.d(TAG, dataModel!!.title)

                binding.titleArea.text = dataModel!!.title
                binding.textArea.text = dataModel!!.content
                binding.timeArea.text = dataModel!!.time

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener) // firebase 에서 id값 가져오기 위해 child(key)

    }

}