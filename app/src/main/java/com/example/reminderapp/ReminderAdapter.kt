package com.example.reminderapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_view_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter internal constructor(context: Context) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reminders = emptyList<ReminderEntity>()

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemMessageView: TextView = itemView.findViewById(R.id.itemMessage)
        val itemTriggerView: TextView = itemView.findViewById(R.id.itemTrigger)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView = inflater.inflate(R.layout.list_view_item, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val current = reminders[position]
        holder.itemMessageView.text = current.message
        if(current.time != null){
            val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy")
            sdf.timeZone = TimeZone.getDefault()
            holder.itemTriggerView.text = sdf.format(current.time)
        }
        else if(current.location != null){
            holder.itemTriggerView.text = current.location
        }
    }


    internal fun setReminders(reminders: List<ReminderEntity>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }

    override fun getItemCount() = reminders.size
}

/*private class ListRowHolder(row: View?) {
    public val label: TextView
    init {
        this.label = row?.findViewById(R.id.itemMessage) as TextView
    }
}*/