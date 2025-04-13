package com.example.sailchoice002

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class WaypointsFragment : Fragment() {
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var waypointsAdapter: WaypointsAdapter
    private lateinit var formatLabel: TextView
    private lateinit var latitudeEditText: TextInputEditText
    private lateinit var longitudeEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private val waypointsFileName = "waypoints.csv"
    private var nextWaypointNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_waypoints, container, false)

        // Set up the Spinner
        val formatSpinner: Spinner = view.findViewById(R.id.formatSpinner)
        formatLabel = view.findViewById(R.id.formatLabel)
        latitudeEditText = view.findViewById(R.id.latitudeEditText)
        longitudeEditText = view.findViewById(R.id.longitudeEditText)
        nameEditText = view.findViewById(R.id.nameEditText)

        // Set initial default value for nameEditText
        nameEditText.setText("GPS$nextWaypointNumber")

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.waypoint_formats,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            formatSpinner.adapter = adapter
        }

        formatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFormat = parent?.getItemAtPosition(position).toString()
                updateFormatLabel(selectedFormat) // Call the method to update the label
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing when nothing is selected
            }
        }
        // Set the default selection to "Degrees Decimal Minutes (DDM)"
        formatSpinner.setSelection(2) // Index 2 corresponds to "Degrees Decimal Minutes (DDM)"
        // Update formatLabel with the default selection
        val defaultFormat = formatSpinner.getItemAtPosition(2).toString()
        updateFormatLabel(defaultFormat)
        // Set up RecyclerView
        waypoints = mutableListOf() // Initialize as an empty list
        loadWaypoints()
        waypointsAdapter = WaypointsAdapter(waypoints, this::deleteWaypoint)
        val waypointsRecyclerView: RecyclerView = view.findViewById(R.id.waypointsRecyclerView)
        waypointsRecyclerView.adapter = waypointsAdapter
        waypointsRecyclerView.layoutManager = LinearLayoutManager(context)
        // Setup the add waypoint button
        val addWaypointButton: Button = view.findViewById(R.id.addWaypointButton)
        addWaypointButton.setOnClickListener {
            addWaypoint()
        }

        return view
    }

    private fun addWaypoint() {
        // Get values from text fields, default name to GPS if empty
        var name = nameEditText.text.toString()
        if (name.isBlank()) {
            name = "GPS$nextWaypointNumber"
        }

        val latitude = latitudeEditText.text.toString()
        val longitude = longitudeEditText.text.toString()
        val format = formatLabel.text.toString()

        // Create a new Waypoint
        val newWaypoint = Waypoint(name, latitude, longitude, format)

        // Append to file
        appendWaypointToFile(newWaypoint)

        // Update the next GPS number
        if (name.startsWith("GPS")) {
            nextWaypointNumber++
        }

        // Set the default name
        nameEditText.setText("GPS$nextWaypointNumber")

        // Clear the input fields
        latitudeEditText.text?.clear()
        longitudeEditText.text?.clear()
    }

    private fun appendWaypointToFile(waypoint: Waypoint) {
        val file = File(context?.filesDir, waypointsFileName)
        val fileExists = file.exists()
        try {
            val fileOutputStream = FileOutputStream(file, true) // Append mode
            if (!fileExists) {
                // Write header if file was just created
                fileOutputStream.write("Name,Latitude,Longitude,Format\n".toByteArray())
            }
            val line = "${waypoint.name},${waypoint.latitude},${waypoint.longitude},${waypoint.format}\n"
            fileOutputStream.write(line.toByteArray())
            fileOutputStream.close()
            Log.d("WaypointsFragment", "Waypoint appended to file: $waypoint")
        } catch (e: IOException) {
            Log.e("WaypointsFragment", "Error appending waypoint to file", e)
        }
    }

    private fun updateFormatLabel(selectedFormat: String) {
        val formatText = when (selectedFormat) {
            "Decimal Degrees (DD)" -> {
                latitudeEditText.setText("51.5074")
                longitudeEditText.setText("0.1278")
                "Format: Decimal Degrees (DD) e.g 51.5074°N, 0.1278°W"
            }

            "Degrees Minutes Seconds (DMS)" -> {
                latitudeEditText.setText("51°30'27\"")
                longitudeEditText.setText("0°07'40\"")
                "Format: Degrees Minutes Seconds (DMS) e.g 51°30'27\"N, 0°07'40\"W"
            }

            "Degrees Decimal Minutes (DDM)" -> {
                latitudeEditText.setText("51°30.45'")
                longitudeEditText.setText("0°07.67'")
                "Format: Degrees Decimal Minutes (DDM) e.g 51°30.45'N, 0°07.67'W"
            }

            else -> ""
        }
        formatLabel.text = formatText
    }

    private fun saveWaypoints() {
        Log.d("WaypointsFragment", "saveWaypoints called")
        try {
            val fileOutputStream: FileOutputStream? = context?.openFileOutput(waypointsFileName, Context.MODE_PRIVATE)
            if (fileOutputStream != null) {
                val stringBuilder = StringBuilder()
                for (waypoint in waypoints) {
                    stringBuilder.append("${waypoint.name},${waypoint.latitude},${waypoint.longitude},${waypoint.format}\n")
                    Log.d("WaypointsFragment", "Writing to file: ${waypoint.name},${waypoint.latitude},${waypoint.longitude},${waypoint.format}")
                }
                fileOutputStream.write(stringBuilder.toString().toByteArray())
                fileOutputStream.close()
                Log.d("WaypointsFragment", "Waypoints saved successfully.")
            } else {
                Log.e("WaypointsFragment", "Error saving waypoints: fileOutputStream is null")
            }
        } catch (e: IOException) {
            Log.e("WaypointsFragment", "Error saving waypoints", e)
        }
    }

    private fun loadWaypoints() {
        val file = File(context?.filesDir, waypointsFileName)
        if (!file.exists()) {
            Log.d("WaypointsFragment", "File does not exist.")
            return
        }

        var highestNumber = 0
        try {
            context?.openFileInput(waypointsFileName)?.bufferedReader()?.useLines { lines ->
                // Skip the header line
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size == 4) {
                        val name = parts[0]
                        val latitude = parts[1]
                        val longitude = parts[2]
                        val format = parts[3]
                        val waypoint = Waypoint(name, latitude, longitude, format)
                        waypoints.add(waypoint)

                        // Update highestNumber if needed
                        if (name.startsWith("GPS")) {
                            val numberPart = name.substringAfter("GPS").toIntOrNull()
                            numberPart?.let {
                                if (it > highestNumber) {
                                    highestNumber = it
                                }
                            }
                        }
                    }
                }
            }
            // Set nextWaypointNumber to the highest number + 1
            nextWaypointNumber = highestNumber + 1
            if (highestNumber > 0){
                nameEditText.setText("GPS$nextWaypointNumber")
            }
        } catch (e: IOException) {
            Log.e("WaypointsFragment", "Error loading waypoints", e)
        }
    }


    private fun deleteWaypoint(position: Int) {
        waypoints.removeAt(position)
        waypointsAdapter.notifyItemRemoved(position)
        waypointsAdapter.notifyItemRangeChanged(position, waypoints.size)
        saveWaypoints()
    }
}