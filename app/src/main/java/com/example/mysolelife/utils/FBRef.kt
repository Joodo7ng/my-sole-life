package com.example.mysolelife.utils

import com.google.firebase.Firebase
import com.google.firebase.database.database

class FBRef {

    companion object{

        private val database = Firebase.database

        // 북마크 탭을 만들기 위해 추가
        val category1 = database.getReference("contents")
        val category2 = database.getReference("contents2")

        val bookmarkRef = database.getReference("bookmark_list") // bookmarkRef라는 주소를 만듦
        val boardRef = database.getReference("board") // board 라는 주소지에 저장할 것이다.

    }
}