package com.example.kioskremote.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskremote.view.OnViewHolderItemClickListener
import com.example.kioskremote.R
import com.example.kioskremote.view.ViewHolderFood
import com.example.kioskremote.dto.FoodData
import com.google.protobuf.Internal
import java.util.*

class RecyclerViewerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // adapter에 들어갈 list 입니다.
    private val listData: ArrayList<FoodData> = ArrayList<FoodData>()

    // Item의 클릭 상태를 저장할 array 객체
    private val selectedItems = SparseBooleanArray()

    lateinit var orderNum : MutableList<Int>

    // 직전에 클릭됐던 Item의 position
    private var prePosition = -1
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)

        for(i in 0 .. itemCount ) {
            orderNum = mutableListOf(0, 0, 0, 0, 0, 0, 0)
        }

        return ViewHolderFood(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val viewHolderFood: ViewHolderFood = holder as ViewHolderFood
        viewHolderFood.onBind(listData[position], position, selectedItems)
        ViewHolderFood.setOnViewHolderItemClickListener(viewHolderFood, object :
            OnViewHolderItemClickListener {
            override fun onViewHolderItemClick() {
                orderNum[position] = viewHolderFood.et.text.toString().toInt()

                if (selectedItems[position]) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(position)
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition)
                    // 클릭한 Item의 position을 저장
                    selectedItems.put(position, true)
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) notifyItemChanged(prePosition)
                notifyItemChanged(position)
                // 클릭된 position 저장
                prePosition = position
            }
        })
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun addItem(data: FoodData) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data)
    }
}