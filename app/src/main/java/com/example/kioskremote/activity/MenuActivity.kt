package com.example.kioskremote.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskremote.R
import com.example.kioskremote.adapter.RecyclerViewerAdapter
import com.example.kioskremote.dto.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.lang.Integer.parseInt

class MenuActivity : AppCompatActivity() {
    var adapter: RecyclerViewerAdapter? = null
    private val TAG = "Firestore"
    val db = FirebaseFirestore.getInstance()
    var storeName: String = ""
    var menuList : List<Menu>? = null
    var table: Int = 0
    lateinit var ord: Button
    lateinit var splitText: List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        ord = findViewById<Button>(R.id.order_button);
        splitText = intent.getStringExtra("storeName").split(",")
        init()
        getData()


        val docRef = db.collection("order").document("신수동_중국집")

        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                var orderList = snapshot.toObject(OrderList::class.java)!!

                for(order in orderList.orderList!!){
                    if(order.flag == true && order.table == this.table){
                        Toast.makeText(this, "따뜻한 음식이 완성되었습니다! 받아가세요!", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        })

        ord.setOnClickListener() {
            var totalAmount: Int = 0
            var order = Order(storeName, this.table, null, Timestamp.now(), false)

            for(i in menuList!!.indices){
                totalAmount += OrderCount.list!![i+1] * menuList!![i].price!!
                order.menu?.let {
                    it.add(i, "${menuList!![i].name},${OrderCount.list!![i + 1]}")
                }?:let {
                    order.menu = mutableListOf("${menuList!![i].name},${OrderCount.list!![i + 1]}")
                }
                order.totalAmount = totalAmount
            }

            db.collection("order").document(order.name.toString())
                .update("orderList", FieldValue.arrayUnion(order))
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            Toast.makeText(this, "총 ${totalAmount}원 주문 완료!", Toast.LENGTH_SHORT).show()

        }
    }

    fun init() {
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        adapter = RecyclerViewerAdapter()
        recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {
        /**
         * firestore는 비동기밖에 지원을 안함
         * 따라서 콜백 리스너(addOnSuccessListener)가 콜백을 받아서 뷰를 그려주기에 더 느린 문제
         * 그래서 그냥 맨 첫번째 데이터는 static하게 넣고 뷰를 그려준 후에 나중에 콜백받을때 그려주는 방법 채택(그냥 돌아가게 보이기만...)
         */
        // TODO "중국집" 이부분은 하드코딩!!! nfc, QR로 체크인 하면 해당 Store가 넘어가야함
        storeName = "신수동_중국집"
        // TODO Table 번호
        table = 2
        storeName = splitText[0]
        table = parseInt(splitText[1])

        dataInit(table)

        ord.setText("주문하기 (${table}번 테이블)")
        Log.d("ABCDE","Store: "+storeName+" table: "+table);

        val docRef = db.collection("store").document(storeName)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            println()
            val store = documentSnapshot.toObject(Store::class.java)
            Log.d(TAG, "store : $store")

            menuList = store!!.menuList!!


            for(i in menuList!!.indices + 1) {
                OrderCount.list?.let {
                    it.add(i, 0)
                }?:let {
                    OrderCount.list = mutableListOf(0)
                }
            }



        }.addOnCanceledListener {
            Log.d(TAG, "?????")
        }
        makeViewData(R.drawable.jja, "짜장면 - 6000원", "신선한 춘장소스, 고기와 면을 넣은 짜장면")
        makeViewData(R.drawable.jjam, "짬뽕 - 7000원", "신선한 해산물과 육수와 면을 넣은 짬뽕")
        makeViewData(R.drawable.tang1, "탕수육(중) - 7000원", "신선한 돼지고기에 국내산 녹말가루를 입혀 튀긴 탕수육")
        makeViewData(R.drawable.tang2, "탕수육(대) - 10000원", "신선한 돼지고기에 국내산 녹말가루를 입혀 튀긴 탕수육")
        makeViewData(R.drawable.bbok, "볶음밥 - 7000원", "신선한 야채들과 매일 직접만드는 짜장소스와 국내산 쌀을 사용함")
        makeViewData(R.drawable.yang, "양장피 - 11000원", "신선한 야채들, 해산물, 돼지고기가 사용된 양장피")
        makeViewData(R.drawable.ggan, "깐풍기 - 11000원", "신선한 닭고기에 밀가루 반죽을 입히고 튀겨서 간장, 식초, 설탕으로 버무린 깐풍기")


        Thread.sleep(300L)
    }

    fun makeViewData(photo: Int, title: String, description: String) {
        val data = FoodData(photo, title, description)
        adapter!!.addItem(data)
    }

    /**
     * 데이터 생성하는 로직(처음 초기화할때나 데이터가 꼬이면 쓰기)
     */
    fun dataInit(table: Int){
        val menu = listOf(
            Menu("짬뽕 - 7000원", 7000, "https://recipe1.ezmember.co.kr/cache/recipe/2017/10/22/aaeb2a235b89ac305ba919e33da2e6331.jpg", "신선한 해산물과 육수와 면을 넣은 짬뽕", listOf(MenuRating(5, "짬뽕이맛있네요"), MenuRating(2, "별로네요"))),
            Menu("탕수육(중) - 7000원", 7000, "https://img.wkorea.com/w/2014/09/style_561e188b0ef2d.jpg","신선한 돼지고기에 국내산 녹말가루를 입혀 튀긴 탕수육",listOf(MenuRating(5, "특히 소스가 맛있네요"), MenuRating(4, "맛있네요"))),
            Menu("탕수육(대) - 10000원", 10000, "https://img.wkorea.com/w/2014/09/style_561e188b0ef2d.jpg","신선한 돼지고기에 국내산 녹말가루를 입혀 튀긴 탕수육",listOf(MenuRating(5, "소스가 일품이에요"), MenuRating(3, "맛은 좋은데 양이 적네요"))),
            Menu("볶음밥 - 7000원", 7000, "https://recipe1.ezmember.co.kr/cache/recipe/2019/03/10/fb97fc984f4e44f3cbeb55ad87998bd61.jpg","신선한 야채들과 매일 직접만드는 짜장소스와 국내산 쌀을 사용함",listOf(MenuRating(4, "밥이 고슬고슬하게 잘 된거 같아요"), MenuRating(4, "잘먹고 갑니다"))),
            Menu("양장피 - 11000원", 11000, "https://recipe1.ezmember.co.kr/cache/recipe/2016/04/22/8e8d26b8f9a2fef59896ba3e9fc22c0c1.jpg","신선한 야채들, 해산물, 돼지고기가 사용된 양장피",listOf(MenuRating(4, "재료가 신선한것 같아서 좋아요"), MenuRating(5, "잘먹었습니다."))),
            Menu("깐풍기 - 11000원", 11000, "https://homecuisine.co.kr/files/attach/images/142/423/002/99b983892094b5c6d2fc3736e15da7d1.JPG","신선한 닭고기에 밀가루 반죽을 입히고 튀겨서 간장, 식초, 설탕으로 버무린 깐풍기",listOf(MenuRating(4, "잘 된거 같아요 잘먹었습니다"), MenuRating(2, "가격대비 양이 좀 적네요")))
        )

        val store = Store(
            "신수동_중국집",
            menu,
            mutableListOf(Table(0, true), Table(1, true), Table(2, true), Table(3, true), Table(4, true), Table(5, true)),
            "서울시"
        )
        store!!.table!![table] = Table(table, false)

        db.collection("store").document(store.name.toString())
            .set(store, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }

    /**
     * 데이터 생성하는 로직(처음 초기화할때나 데이터가 꼬이면 쓰기)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun dataInitOrder(){
        val order = Order(
            "중국집",
            3,
            mutableListOf("짜장면, 3", "짜장면, 1"),
            Timestamp.now(),
            false
        )

        db.collection("order").document(order.name.toString())
            .set(order, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }
}
