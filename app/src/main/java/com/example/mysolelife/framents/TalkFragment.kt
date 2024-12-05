package com.example.mysolelife.framents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.mysolelife.R
import com.example.mysolelife.board.BoardInsideActivity
import com.example.mysolelife.board.BoardListLVAdapter
import com.example.mysolelife.board.BoardModel
import com.example.mysolelife.board.BoardWriteActivity
import com.example.mysolelife.contentsList.ContentModel
import com.example.mysolelife.databinding.FragmentTalkBinding
import com.example.mysolelife.databinding.FragmentTipBinding
import com.example.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding

    private val boardDataList = mutableListOf<BoardModel>() // 보드 모델 담는 리스트 선언
    private val boardKeyList = mutableListOf<String>() // 게시글 key값(id) 담는 리스트 선언

    private val TAG = TalkFragment::class.java.simpleName // 태그 선언

    private lateinit var boardLVAdapter : BoardListLVAdapter // 보드리스트 어댑터 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false)


        // ListView 어댑터연결
        boardLVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardLVAdapter

        // 게시글 보기 페이지 만드는 두 가지 방법
        // 1 - Listview에 있는 데이터(title, content, time) 모두 다른 액티비티로 전달해서 만들기
        // 2 - firebase에 있는 board에 대한 데이터의 고유 id를 기반으로 다시 데이터를 받아오는 방법


        binding.boardListView.setOnItemClickListener { parent, view, position, id ->
            // 1-1. 게시글 누르면 액티비티로 이동하도록 연결 (액티비티 먼저 생성)
//            val intent = Intent(context, BoardInsideActivity::class.java)
//            intent.putExtra("title", boardDataList[position].title) // 액티비티와 함께 데이터 함께 넘김
//            intent.putExtra("content", boardDataList[position].content)
//            intent.putExtra("time", boardDataList[position].time)
//            startActivity(intent)

            // 2. firebase에 있는 board에 대한 데이터의 고유 id를 기반으로 다시 데이터를 받아오는 방법

            val intent = Intent(context, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position]) // key값 intent로 넘겨줌
            startActivity(intent)

        }


        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_tipFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_storeFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)
        }

        getFBBoardData()

        return binding.root
    }

    private fun getFBBoardData() {

        // firebase에서 데이터를 읽어옴
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear() // firebase문제로 새 게시글 입력시 기존 글이 겹쳐나오는 문제 해결하기 위해 리스트 초기화

                for(dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    //dataModel.key

                    val item = dataModel.getValue(BoardModel::class.java) // 데이터를 보드모델로 받음
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())  // key값들을 리스트에 넣음

                }

                boardKeyList.reverse() // 키 리스트도 DataList에 맞추어 순서 반전
                boardDataList.reverse() // 최신 게시물이 최상단에 오도록 리스트뷰 뒤집음
                boardLVAdapter.notifyDataSetChanged() // boardLVAdapter 동기화

                Log.d(TAG, boardDataList.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)

    }

}