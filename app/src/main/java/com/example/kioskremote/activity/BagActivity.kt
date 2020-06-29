package com.example.kioskremote.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kioskremote.R
import kotlinx.android.synthetic.main.activity_bag.*

class BagActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bag)

        // 장바구니 파일.
        // 고객이 다음 음식을 볼 수 있어야 함.

        // 이 버튼 누르면 결제화면으로 감.
        go_pay.setOnClickListener{
            val intent= Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }
}
