package com.example.ch1.section3_recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ch1.R
import com.example.ch1.databinding.ActivityTest32Binding
import com.example.ch1.databinding.RecyclerItemUpdateDeleteBinding

class Test3_2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityTest32Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val data = mutableListOf<String>()
        for (i in 1..10) {
            data.add("${i * 100}")
        }
        binding.main.adapter = MyAdapter2(data)
        binding.main.layoutManager = LinearLayoutManager(this)
        binding.main.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}

class MyViewHolder2(val binding: RecyclerItemUpdateDeleteBinding) :
    RecyclerView.ViewHolder(binding.root)

class MyAdapter2(val data: MutableList<String>) : RecyclerView.Adapter<MyViewHolder2>() {
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
        return MyViewHolder2(
            RecyclerItemUpdateDeleteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        val binding = holder.binding
        binding.itemData.text = data[position]

        // test1 - notifyXXX() 함수 이용하여 변경사항 적용
//        binding.updateButton.setOnClickListener {
//            var newData = data[position].toInt()
//            newData++
//            data[position] = newData.toString()
//            // 특정 항목의 위치를 지정하여 그 항목만 다시 구성되게 변경사항 반영
//            notifyItemChanged(position)
//        }
//
//        binding.deleteButton.setOnClickListener {
//            // 데이터 삭제
//            data.removeAt(position)
//            notifyItemRemoved(position)
//            // 삭제가 되긴 하지만 아래 항목의 index 값이 변경 되었음을 알려주지 않으면
//            // 아래 항목 update, delete 시에 다른 항목이 update, delete 될 수 있거나
//            // out of bound exception 가능성 있음
//            notifyItemRangeChanged(position, data.size - position)
//        }

        binding.updateButton.setOnClickListener {
            if (position in 0 until data.size) {
                val currentValue = data[position].toInt()
                val newValue = (currentValue + 1).toString()

                // 복제본 생성
                val newList = data.toMutableList()
                newList[position] = newValue
                setData(newList)
            }
        }
        binding.deleteButton.setOnClickListener {
            if (position in 0 until data.size) {
                val newList = data.toMutableList()
                newList.removeAt(position)
                setData(newList)
            }
        }
    }
    fun setData(newNumbers: MutableList<String>) {
        val diffCallback = MyDiffClassback(data, newNumbers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        data.clear()
        data.addAll(newNumbers)
        diffResult.dispatchUpdatesTo(this)  // 항목 업데이트
    }
}

// test2 - DiffUtil 이용하여 변경, 삭제를 자체적으로 판단하고 업데이트
// 변경된 항목의 index 를 우리가 계산하지 않아도 되기 때문에 동시다발로 불규칙하게 변화할 때
class MyDiffClassback(
    private val oldList: MutableList<String>,
    private val newList: MutableList<String>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    // 두 아이템이 동일 아이템인 지 판단하는 로직
    // 일반적으로 VO 클래스로 항목이 구성된다면 객체가 동일한지에 대한 비교
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition &&
                oldItemPosition < oldList.size &&
                newItemPosition < newList.size
    }

    // 항목에 찍히는 데이터의 비교
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}