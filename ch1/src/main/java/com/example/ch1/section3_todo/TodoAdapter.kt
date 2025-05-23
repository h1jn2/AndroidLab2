package com.example.ch1.section3_todo

import android.content.ContentValues
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ch1.R
import com.example.ch1.databinding.RecyclerItemHeaderBinding
import com.example.ch1.databinding.RecyclerItemMainBinding

// 하나의 recycler 에 두가지 항목
// 항목의 데이터를 두가지 타입으로
// 두 타입의 객체를 동일 타입으로 핸들링 하기 위해서 상위 타입
abstract class Item {
    abstract val type: Int  // 하위에서 타입 지정

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_DATA = 1
    }
}

data class HeaderItem(override val type: Int = TYPE_HEADER, var date: String) : Item()
data class DataItem(
    override val type: Int = TYPE_DATA,
    var id: Int, var title: String, var content: String, var completed: Boolean = false
) : Item()

// 항목의 viewholder 를 두가지 타입으로
class HeaderViewHolder(val binding: RecyclerItemHeaderBinding) :
    RecyclerView.ViewHolder(binding.root)

class DataViewHolder(val binding: RecyclerItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(val activity: Test3_3Activity, val itemList: MutableList<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    // recyclerview 의 항목 타입이 여러 개인 경우 getItemViewType 함수를 오버라이드 받아서
    // 각 항목의 타입이 무엇인 지 알려주어야 함
    override fun getItemViewType(position: Int): Int {
        return itemList.get(position).type
    }

    // 항목의 holder 를 결정하기 위해서 자동 호출
    // 항목이 여러 개이기 때문에 어떤 타입의 holder 를 준비할 지 결정해야 함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Item.TYPE_HEADER) {
            HeaderViewHolder(
                RecyclerItemHeaderBinding.inflate(
                    LayoutInflater.from(activity),
                    parent,
                    false
                )
            )
        } else {
            DataViewHolder(
                RecyclerItemMainBinding.inflate(
                    LayoutInflater.from(activity),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList.get(position)
        if (item.type == Item.TYPE_HEADER) {
            val viewHolder = holder as HeaderViewHolder
            val headerItem = item as HeaderItem
            viewHolder.binding.itemHeaderView.text = headerItem.date
        } else {
            val viewHolder = holder as DataViewHolder
            val dataItem = item as DataItem
            viewHolder.binding.itemTitleView.text = dataItem.title
            viewHolder.binding.itemContentView.text = dataItem.content

            if (dataItem.completed) {
                viewHolder.binding.completedIconView.setImageResource(R.drawable.icon_completed)
            } else {
                viewHolder.binding.completedIconView.setImageResource(R.drawable.icon)
            }

            // 유저가 ImageView 클릭 처리 상태 변경
            viewHolder.binding.completedIconView.setOnClickListener {
                val values = if(dataItem.completed) {
                    viewHolder.binding.completedIconView.setImageResource(R.drawable.icon)
                    ContentValues().apply {
                        put("completed", 0)
                    }
                } else {
                    viewHolder.binding.completedIconView.setImageResource(R.drawable.icon_completed)
                    ContentValues().apply {
                        put("completed", 1)
                    }
                }
                updateTodo(activity, values, "_id=${dataItem.id}")
                dataItem.completed = !dataItem.completed
            }
        }
    }
}