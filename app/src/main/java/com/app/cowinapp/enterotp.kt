package com.app.cowinapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest

class enterotp : AppCompatActivity() {

    private lateinit var thread:Thread

    lateinit var txnId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterotp)

        val button = findViewById<Button>(R.id.button)

        val edittext = findViewById<EditText>(R.id.edittext)

        txnId = intent.getStringExtra("message_key").toString()
        Log.d("initial confirm txnid",txnId)

        button.setOnClickListener()
        {
            if(edittext.text.isEmpty())
            {
                Toast.makeText(this,"Please enter an OTP", Toast.LENGTH_SHORT).show()
            }
            else
            {
                ////////////////////////////////////////////////////////////////////////////////////
                val md = MessageDigest.getInstance("SHA-256")
                val messageDigest = md.digest(edittext.text.toString().toByteArray())
                val no = BigInteger(1, messageDigest)
                var hashtext = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                Log.d("SHA hash",hashtext)
                ////////////////////////////////////////////////////////////////////////////////////
                verifyotp(txnId,hashtext)
                thread.start()
                Toast.makeText(this,"Verifying OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyotp(id:String,otp:String) {
        thread = Thread {
            try {
                Log.d("confirm txnid",id)
                Log.d("confirm hash",otp)

                val url = "https://cdn-api.co-vin.in/api/v2/auth/public/confirmOTP"

                val client = OkHttpClient()

                val JSON = "application/json; charset=utf-8".toMediaType()
                val body = RequestBody.create(JSON,"{\"otp\":\"${otp}\",\"txnId\":\"${id}\"}")
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

                var myObj = JSONObject(result)

                Log.d("token", myObj.getString("token"))

                val intent = Intent(this,token::class.java)
                intent.putExtra("message_key", myObj!!.getString("token"));
                startActivity(intent)


            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}