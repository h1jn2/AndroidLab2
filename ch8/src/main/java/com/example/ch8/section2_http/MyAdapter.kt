package com.example.ch8.section2_http

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ch8.databinding.ItemBinding

class MyViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

}

// datas - activity 가 서버 연동을 통해 받은 서버 데이터
class MyAdapter(val context: Context, val datas: MutableList<ItemModel>?) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val model = datas!![position]

        // 서버에서 받은 데이터 화면에 출력
        binding.itemTitle.text = model.title
        binding.itemDesc.text = model.desc
        binding.itemTime.text = "${model.author} At ${model.publishedAt}"
        // 서버에서 이미지 다운로드 url 이 전달되는데 glide 사용하여 이미지를 다운로드 받아 이미지로 출력
        Glide.with(context)
            .load(model.urlToImage)
            .into(binding.itemImage)
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }
}