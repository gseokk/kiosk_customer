package com.example.kioskremote.view

import android.animation.ValueAnimator
import android.util.SparseBooleanArray
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskremote.R
import com.example.kioskremote.dto.FoodData
import com.example.kioskremote.dto.OrderCount
import java.lang.Integer.parseInt

class ViewHolderFood(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tv_food_title: TextView
    var iv_food: ImageView
    var iv_food2: ImageView
    var tv_description: TextView
    var linearlayout: LinearLayout
    var btplus: Button
    var btminus: Button
    var et : EditText
    var position: Int? = null
    var onViewHolderItemClickListener: OnViewHolderItemClickListener? = null
    fun onBind(data: FoodData, position: Int, selectedItems: SparseBooleanArray) {
        tv_food_title.setText(data.title)
        iv_food.setImageResource(data.image)
        iv_food2.setImageResource(data.image)
        tv_description.setText(data.description)
        changeVisibility(selectedItems[position])
        this.position = position
    }

    private fun changeVisibility(isExpanded: Boolean) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        val va =
            if (isExpanded) ValueAnimator.ofInt(0, 600) else ValueAnimator.ofInt(600, 0)
        // Animation이 실행되는 시간, n/1000초
        va.duration = 500
        va.addUpdateListener { animation -> // imageView의 높이 변경
            iv_food2.layoutParams.height = animation.animatedValue as Int
            tv_description.layoutParams.height = animation.animatedValue as Int - 300
            iv_food2.requestLayout()
            tv_description.requestLayout()

            // imageView가 실제로 사라지게하는 부분
            iv_food2.visibility = if (isExpanded) View.VISIBLE else View.GONE
            tv_description.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
        // Animation start
        va.start()
    }

    init {
        iv_food = itemView.findViewById(R.id.iv_food)
        tv_food_title = itemView.findViewById(R.id.tv_food_title)
        iv_food2 = itemView.findViewById(R.id.iv_food2)
        tv_description = itemView.findViewById(R.id.description)
        linearlayout = itemView.findViewById(R.id.linearlayout)
        linearlayout.setOnClickListener { onViewHolderItemClickListener!!.onViewHolderItemClick() }
        et = itemView.findViewById(R.id.cnt)
        et.setText("0")
        btplus = itemView.findViewById(R.id.plus)
        btplus.setOnClickListener{
            var cnt = parseInt(et.text.toString())
            cnt++
            et.setText(cnt.toString())
            OrderCount.list!![position!!] = cnt
        }
        btminus = itemView.findViewById(R.id.minus)
        btminus.setOnClickListener{
            var cnt = parseInt(et.text.toString())
            cnt--
            et.setText(cnt.toString())
            OrderCount.list!![position!!] = cnt
        }
    }

    companion object {
        fun setOnViewHolderItemClickListener(
            viewHolderFood: ViewHolderFood,
            onViewHolderItemClickListener: OnViewHolderItemClickListener?
        ) {
            viewHolderFood.onViewHolderItemClickListener = onViewHolderItemClickListener
        }
    }
}