package com.example.kioskremote.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kioskremote.R
import kotlinx.android.synthetic.main.activity_foodview.*

class FoodviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodview)

        //select_food 버튼은 아직 사용x
        //select_food 버튼 누르면 장바구니에 담기게 코드 작성


        //이 음식 바로 주문하겠으니 바로 결제로 갈게요
                order_food.setOnClickListener{
            val intent=Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }

}
