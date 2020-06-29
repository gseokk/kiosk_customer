package com.example.kioskremote.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kioskremote.R
import kotlinx.android.synthetic.main.activity_qr.*

import com.google.zxing.integration.android.IntentIntegrator

class QrActivity : AppCompatActivity() {
    private var qrScan: IntentIntegrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        qrScan = IntentIntegrator(this)
        qrScan!!.setOrientationLocked(false) // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan!!.setPrompt("Read QR Code!!!")
        qrScan!!.initiateScan()

        // 임시로 만든거임. QR 코드 읽으면 화면 넘어갈 수 있게 해야 됨
        qr.setOnClickListener{
            val intent= Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                val intent= Intent(this, MenuActivity::class.java)
                intent.putExtra("storeName",result.contents)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
