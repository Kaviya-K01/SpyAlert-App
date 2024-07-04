package com.example.permissionchecker

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class AppUsage : AppCompatActivity(), FetchApplicationList.OnFetchApplicationListListener {

    lateinit var adapter: UsageAdapter
    lateinit var usage_list: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)
        FetchApplicationList(this@AppUsage,this@AppUsage).execute()
        usage_list = findViewById(R.id.usage_list)

            usage_list.layoutManager = LinearLayoutManager(this)

    }

    override fun onApplicationList(applications: MutableList<AppName>) {
        adapter = UsageAdapter(applications,applicationContext)
        usage_list.adapter = adapter
        adapter.SetOnItemClickListener(object :UsageAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@AppUsage,UsageTime::class.java)
                intent.putExtra("AppName",adapter.items[position].packageName)
                startActivity(intent)
            }

        })
    }

}