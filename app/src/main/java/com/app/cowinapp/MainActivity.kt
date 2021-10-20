package com.app.cowinapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var thread:Thread

    var myObj: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phonenumber = findViewById<EditText>(R.id.edittext)

        val sendotp = findViewById<Button>(R.id.button)

        sendotp.setOnClickListener()
        {
            if(phonenumber.text.isEmpty())
            {
                Toast.makeText(this,"please enter a valid phone number",Toast.LENGTH_SHORT).show()
            }
            else {
                gettxnid(phonenumber.text.toString())
                thread.start()
                Toast.makeText(this,"OTP sent",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gettxnid(phoneno : String) {
        thread = Thread {
            try {
                val url = "https://cdn-api.co-vin.in/api/v2/auth/public/generateOTP"

                val client = OkHttpClient()

                val JSON = "application/json; charset=utf-8".toMediaType()
                val body = RequestBody.create(JSON,"{\"mobile\":\"${phoneno}\"}")
                val request = Request.Builder()
                    .addHeader("Authorization", "Bearer")
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()

                println(response.request)
                //println(response.body!!.string())
                var result: String = Gson().toJson(response.body!!.string())

                result = result.trim()
                result = result.substring(1, result.length -1)
                result = result.replace("\\", "")

                myObj = JSONObject(result)

                Log.d("txnid", myObj!!.getString("txnId"))

                val intent = Intent(this,enterotp::class.java)
                intent.putExtra("message_key", myObj!!.getString("txnId"));
                startActivity(intent)

            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}