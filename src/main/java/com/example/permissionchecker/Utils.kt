package com.example.permissionchecker

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

class Utils {
    var DAY_IN_MILLIS :Long = 86400 * 1000

    fun reverseProcessTime(time: Int): Array<Int>{
        val hourMinSec = Array<Int>(3){0}
        hourMinSec[0] = (time/3600)
        hourMinSec[1] = (time%3600)/60
        hourMinSec[2] = (time%3600)%60
        return hourMinSec
    }

    fun getTimeSpent(context: Context,packageName: String,beginTime: Long,endTime: Long):HashMap<String,Int>{
        var currentEvent: UsageEvents.Event
        val allEvents: ArrayList<UsageEvents.Event> = ArrayList<UsageEvents.Event>()
        val appUsageMap:HashMap<String, Int> = HashMap()

        val usageStatsManager : UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents : UsageEvents = usageStatsManager.queryEvents(beginTime,endTime)

        while (usageEvents.hasNextEvent()){
            currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            if (currentEvent.packageName.equals(packageName)){
                if(currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED){
                    allEvents.add(currentEvent)
                    val key: String = currentEvent.packageName
                    if(appUsageMap.get(key) == null){
                        appUsageMap.put(key,0)
                    }
                }
            }
        }

        for (AllEvents in allEvents){
            val E0 : UsageEvents.Event = AllEvents
            val E1 : UsageEvents.Event = AllEvents

            if (E0.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                && E1.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                && E0.className.equals(E1.className)
            ){
                var diff : Int = (E1.timeStamp - E0.timeStamp).toInt()
                diff/=1000
                val prev: Int = appUsageMap.get(E0.packageName)!!
                appUsageMap.put(E0.packageName,prev+diff)
            }
        }

        if (allEvents.size !=0){
            val lastEvent: UsageEvents.Event = allEvents.get(allEvents.size -1)
            if (lastEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED){
                val currentRunningPackageName = lastEvent.packageName
                var diff : Int =  System.currentTimeMillis().toInt() - lastEvent.timeStamp.toInt()
                diff/=1000
                val prev:Int = appUsageMap.get(currentRunningPackageName)!!
                appUsageMap.put(currentRunningPackageName,prev+diff)
                appUsageMap.put("current" + currentRunningPackageName,-1)
            }
        }
        return appUsageMap
    }
}