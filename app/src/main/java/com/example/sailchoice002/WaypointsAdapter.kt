package com.example.sailchoice002

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaypointsAdapter(private val waypoints: MutableList<Waypoint>, private val deleteListener: (Int) -> Unit) :
    RecyclerView.Adapter<WaypointsAdapter.WaypointViewHolder>() {

    class WaypointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaypointViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.waypoint_item, parent, false)
        return WaypointViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WaypointViewHolder, position: Int) {
        val currentWaypoint = waypoints[position]
        holder.nameTextView.text = currentWaypoint.name
        holder.latitudeTextView.text = currentWaypoint.latitude
        holder.longitudeTextView.text = currentWaypoint.longitude

        holder.deleteButton.setOnClickListener {
            deleteListener(position)
        }
    }

    override fun getItemCount(): Int {
        return waypoints.size
    }
}
