package com.example.ch1.section3_todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ch1.databinding.FragmentListBinding
import java.text.SimpleDateFormat
import java.util.Date

class ListFragment: Fragment() {
    lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(LayoutInflater.from(activity), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {
            (activity as Test3_3Activity).goAddFragment()   // 화면 전환
        }
        // 초기 데이터 구축 db select
        val itemList = setRecyclerViewData(activity as Context)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = TodoAdapter(activity as Test3_3Activity, itemList)
        binding.recyclerView.addItemDecoration(TodoDecoration(itemList))
    }

    fun setRecyclerViewData(context: Context): MutableList<Item> {
        val dbList = selectTodos(context)
        val itemList = mutableListOf<Item>()

        var preDate: String? = null
        for (vo in dbList) {
            if (!vo.date.equals(preDate)) {
                // header 항목 데이터 추가
                val headerItem = HeaderItem(date = SimpleDateFormat("yyyy-MM-dd").format(Date(vo.date.toLong())))
                itemList.add(headerItem)
                preDate = vo.date
            }
            val completed = vo.completed != 0
            val dataItem = DataItem(id = vo.id, title = vo.title, content = vo.content,
                completed = completed)
            itemList.add(dataItem)
        }
        return itemList
    }
}