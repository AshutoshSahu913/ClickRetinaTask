package com.example.myapplication.View

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.myapplication.R
import com.example.myapplication.ViewModel.AppViewModel
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    private val viewModel: AppViewModel by viewModels()

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Observe the loading state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                // Show loading spinner or progress bar
                binding.progressBar.visibility = View.VISIBLE
            } else {
                // Hide loading spinner or progress bar
                binding.progressBar.visibility = View.GONE
            }
        })

        // Fetch data when activity is created
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.fetchData()
            delay(100)
        }


        // Observe the LiveData from the ViewModel
        viewModel.data.observe(this, Observer { apiResponse ->
            apiResponse?.choices?.forEach { choice ->
                val content = choice.message.content
                Log.d("SCREEN", "onCreate: $content")
                // Set the text to the TextView using ViewBinding
                val details = extractContentFromJson(content)
                binding.title.text = details.titles[0]
                binding.description.text = details.description
            }
        }
        )


//        val title = binding.title
//        val desc = binding.description
    }

}

// Data class to hold titles and description
data class Content(val titles: List<String>, val description: String)


fun extractContentFromJson(jsonString: String): Content {
    // Parse the JSON string into a JSONObject
    val jsonObject = JSONObject(jsonString)

    // Extract the titles from the JSON
    val titlesJsonArray = jsonObject.getJSONArray("titles")
    val titlesList = mutableListOf<String>()

    // Convert JSON Array to a Kotlin List
    for (i in 0 until titlesJsonArray.length()) {
        titlesList.add(titlesJsonArray.getString(i))
    }

    // Extract the description from the JSON
    val description = jsonObject.getString("description")

    Log.d(
        "SCREEN",
        "extractContentFromJson----------------------------------: $titlesList,$description"
    )
    // Return an instance of the Content data class
    return Content(titlesList, description)
}