package com.olgun.unutma

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject

/** Transient messages are separate from scheduled reminders and expire after five seconds. */
object InstantMessage {
 private const val LIFE=5000L
 private val handler=Handler(Looper.getMainLooper())
 private fun prefs(c:Context)=c.getSharedPreferences("instant_messages",Context.MODE_PRIVATE)
 @Synchronized fun all(c:Context):List<Pair<String,Long>> {
  val now=System.currentTimeMillis()
  val a=JSONArray(prefs(c).getString("items","[]"))
  val kept=mutableListOf<Pair<String,Long>>()
  for(i in 0 until a.length()){
   val o=a.getJSONObject(i);val until=o.getLong("until")
   if(until>now)kept.add(o.getString("text") to until)
  }
  if(kept.size!=a.length())persist(c,kept)
  return kept
 }
 private fun persist(c:Context,items:List<Pair<String,Long>>){
  val a=JSONArray();items.forEach{(text,until)->a.put(JSONObject().put("text",text).put("until",until))}
  check(prefs(c).edit().putString("items",a.toString()).commit())
 }
 @Synchronized fun add(c:Context,text:String):Boolean {
  require(text.isNotBlank() && text.length<=20000)
  val until=System.currentTimeMillis()+LIFE
  val items=all(c).toMutableList()
  // A repeated delivery or tap must not create a second visible entry.
  if(items.any{it.first==text})return false
  items.add(text to until);persist(c,items)
  ReminderWidget.refresh(c)
  val app=c.applicationContext
  handler.postDelayed({all(app);ReminderWidget.refresh(app)},LIFE+100L)
  return true
 }
}
