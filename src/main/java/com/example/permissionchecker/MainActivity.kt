package com.example.permissionchecker

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    @SuppressLint("QueryPermissionsNeeded", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permbtn = findViewById<Button>(R.id.permissions)
        val usebtn = findViewById<Button>(R.id.Usage)
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)

        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        permbtn.setOnClickListener {
            val intent = Intent(this, Permissions::class.java)
            startActivity(intent)
        }
        usebtn.setOnClickListener {
            val intent = Intent(this,AppUsage::class.java)
            startActivity(intent)
        }
        val hour = findViewById<TextView>(R.id.hour)
        val minute = findViewById<TextView>(R.id.minute)
        val seconds = findViewById<TextView>(R.id.second)
        val timeSpent = getTotalScreenTime(this@MainActivity)
        hour.setText(((timeSpent/3600000)%24).toString())
        minute.setText(((timeSpent/60000)%60).toString())
        seconds.setText(((timeSpent/1000)%60).toString())

    }

    fun getTotalScreenTime(context: Context): Long {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Get the beginning and the end of the current day in milliseconds
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = System.currentTimeMillis()

        // Query the usage stats for the given time interval
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, endOfDay)

        if (usageStatsList != null && usageStatsList.isNotEmpty()) {
            var totalTimeForeground: Long = 0
            for (usageStats in usageStatsList) {
                totalTimeForeground += usageStats.totalTimeInForeground
            }
            return totalTimeForeground
        }

        return 0L
    }
}