package com.example.mysolelife.board

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.mysolelife.R
import com.example.mysolelife.contentsList.BookmarkModel
import com.example.mysolelife.databinding.ActivityBoardWriteBinding
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding

    private val TAG = BoardWriteActivity::class.java.simpleName

    private var isImageUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        binding.writeBtn.setOnClickListener {

            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            Log.d(TAG, title)
            Log.d(TAG, content)

            // 게시판에 넣는 데이터 구조
            // board
            //    -  key (고유값)
            //        - boardModel(title, content, uid, time) (만든 데이터들을 넣어줌 - 타이틀, 컨텐츠, 글쓴 사람의 uid, 글작성 시간)

            // firebase store에 이미지를 저장하고 싶습니다.
            // 만약에 내가 게시글을 클릭했을때, 게시글에 대한 정보를 받아와야하는데 key 값을 바탕으로 받아옴
            // 이미지 이름에 대한 정보를 모르기 때문에 
            // 게시글에 대한 key값으로 이름을 붙여주어 이미지를 받아옴

            val key = FBRef.boardRef.push().key.toString() // boardRef 안에 key 값 받아옴

            FBRef.boardRef
                .child(key) // 생성한 key값 넣음
                .setValue(BoardModel(title, content, uid, time))

            Toast.makeText(this, "게시글 입력 완료", Toast.LENGTH_LONG).show()

            if(isImageUpload == true) {
                imageUpload(key)
            }

            finish()

        }


        // imageArea 클릭시
        binding.imageArea.setOnClickListener {

            // 갤러리로 이동
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true

        }
    }

    private fun imageUpload(key : String){
        // Get the data from an ImageView as bytes
        val storage = Firebase.storage //storage 선언(firebase 이미지 db)
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")


        val imageView = binding.imageArea

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

        uploadTask.addOnFailureListener { exception ->
            Log.e(TAG, "이미지 업로드 실패: ${exception.message}")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 갤러리에서 데이터 받아오기
        if (resultCode == RESULT_OK && requestCode == 100) {

            // 받아온 데이터를 레이아웃에 표시(갤러리 데이터가 imageArea에 표시)
            binding.imageArea.setImageURI(data?.data)

        }
    }
}