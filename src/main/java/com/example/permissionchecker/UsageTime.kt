package com.example.permissionchecker

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UsageTime : AppCompatActivity() {

    lateinit var hour :TextView
    lateinit var minute :TextView
    lateinit var seconds :TextView
    lateinit var packageNames : String
    lateinit var appInfo:ApplicationInfo
    lateinit var appName:String
    lateinit var appIcon:Drawable
    lateinit var barChart: BarChart
    lateinit var usage : TextView


    lateinit var utils:Utils
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_time)



        hour = findViewById(R.id.hour)
        minute = findViewById(R.id.minute)
        seconds = findViewById(R.id.second)
        usage = findViewById(R.id.usage)
        utils = Utils()
        packageNames = intent.getStringExtra("AppName").toString()
        appInfo = packageManager.getApplicationInfo(packageNames,0)
        appName = packageManager.getApplicationLabel(appInfo) as String
        appIcon = packageManager.getApplicationIcon(appInfo)

        val Name = findViewById<TextView>(R.id.chart_app_name)
        Name.text = appName
        val Icon = findViewById<ImageView>(R.id.list_app_icon)
        Icon.setImageDrawable(appIcon)
        barChart = findViewById(R.id.barchart)
        queryDayWiseUsage()

    }

    override fun onResume() {
        super.onResume()
        val timeSpent : Long = getTimeSpent()
        showTimeSpent(timeSpent)
        perfomance(timeSpent)
    }

    fun perfomance(timeSpent: Long){
        if(((timeSpent/3600000)%24) > 0){
            usage.text = "High"
            usage.setTextColor(Color.parseColor("#E40D38"))
        }else{
            usage.text = "Normal"
        }
    }

    fun showTimeSpent(timeSpent: Long){
        hour.setText(((timeSpent/3600000)%24).toString())
        minute.setText(((timeSpent/60000)%60).toString())
        seconds.setText(((timeSpent/1000)%60).toString())
    }

    fun getTimeSpent():Long{
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime-86400000 , currentTime)
        var totalUsageTime: Long = 0

        if (stats != null) {
            for (usageStats in stats) {
                if (usageStats.packageName == packageNames) {
                    totalUsageTime = usageStats.totalTimeInForeground
                    // Process totalUsageTime as needed
                    break
                }
            }
        } else {
            Toast.makeText(this@UsageTime,"Package Not found",Toast.LENGTH_LONG).show()
        }
        return totalUsageTime
    }

    private fun queryDayWiseUsage() {
        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -6) // Fetch data for the last 7 days
        val startTime = calendar.timeInMillis
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val dayWiseData = mutableListOf<DayWiseUsageData>()

        if (stats != null) {
            for (UsageStats in stats){
                if (UsageStats.packageName == packageNames){
                    val totalTimeInForeground = UsageStats.totalTimeInForeground
                    val minutes = (totalTimeInForeground/60000)%60
                    dayWiseData.add(DayWiseUsageData(minutes))
                }
            }
            if (dayWiseData.isNotEmpty()){
                setupBarChart(barChart,dayWiseData)
            }else{
                Toast.makeText(this@UsageTime,"No data Availablle for the target app ",Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this@UsageTime,"UsageStats not availabe",Toast.LENGTH_LONG).show()
        }
    }
    private fun setupBarChart(chart: BarChart, data: List<DayWiseUsageData>) {
        val entries = mutableListOf<BarEntry>()

        data.forEachIndexed { index, dayWiseData ->
            entries.add(BarEntry(index.toFloat(), dayWiseData.totalTimeInForeground.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "Time Spent")
        barDataSet.color = Color.BLUE

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f

        chart.data = barData
        chart.setFitBars(true)
        chart.description.isEnabled = false
        chart.setDrawValueAboveBar(false)

        val xAxis: XAxis = chart.xAxis
        xAxis.valueFormatter = DayValueFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.granularity = 1f
        chart.axisLeft.setDrawGridLines(false)

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        barDataSet.valueTextColor = Color.TRANSPARENT
        chart.invalidate()
    }
    private class DayValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return if (value >= 0) {
                val dayIndex = value.toInt()
                if (dayIndex < 7) {
                    SimpleDateFormat("EEE", Locale.getDefault())
                        .format(System.currentTimeMillis() - (6 - dayIndex) * 24 * 60 * 60 * 1000)
                } else {
                    ""
                }
            } else {
                ""
            }
        }
    }
}


