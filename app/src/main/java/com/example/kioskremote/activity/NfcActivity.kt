package com.example.kioskremote.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kioskremote.R
import kotlinx.android.synthetic.main.activity_nfc.*

class NfcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        // 임시로 만든거임. NFC 읽으면 화면 넘어갈 수 있게 해야 됨.
        nfc.setOnClickListener{
            val intent= Intent(this, MenuActivity::class.java)
            startActivity(intent)

        }
    }
}
