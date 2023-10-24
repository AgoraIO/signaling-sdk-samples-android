package io.agora.signaling_reference_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Sample list of items
        val itemList = listOf(
            ListItem("GET STARTED", ListItem.ExampleId.HEADER),
            ListItem("SDK quickstart", ListItem.ExampleId.SDK_QUICKSTART),
            ListItem("Secure authentication with tokens", ListItem.ExampleId.AUTHENTICATION_WORKFLOW),

            ListItem("CORE FUNCTIONALITY", ListItem.ExampleId.HEADER),
            ListItem("Stream channels", ListItem.ExampleId.STREAM_CHANNEL),
            ListItem("Store channel and user data", ListItem.ExampleId.STORE_DATA),
            ListItem("Connect through restricted networks with cloud proxy", ListItem.ExampleId.CLOUD_PROXY),
            ListItem("Data encryption", ListItem.ExampleId.DATA_ENCRYPTION),

            ListItem("INTEGRATE FEATURES", ListItem.ExampleId.HEADER),
            ListItem("Geofencing", ListItem.ExampleId.GEOFENCING),
        )
        // Set up the adapter with the list of items and click listener
        val adapter = ItemListAdapter(itemList, object : ItemListAdapter.ItemClickListener {
            override fun onItemClick(item: ListItem) {
                when (item.id) {
                    ListItem.ExampleId.SDK_QUICKSTART -> launchActivity(BasicImplementationActivity::class.java)
                    ListItem.ExampleId.AUTHENTICATION_WORKFLOW -> launchActivity(AuthenticationActivity::class.java)
                    ListItem.ExampleId.STREAM_CHANNEL -> launchActivity(StreamChannelActivity::class.java)
                    ListItem.ExampleId.CLOUD_PROXY -> launchActivity(CloudProxyActivity::class.java)
                    ListItem.ExampleId.DATA_ENCRYPTION -> launchActivity(DataEncryptionActivity::class.java)
                    ListItem.ExampleId.GEOFENCING -> launchActivity(GeofencingActivity::class.java)
                    else -> {}
                }
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun launchActivity(activityClass: Class<*>) {
        // Launch the corresponding activity when an example is selected
        val intent = Intent(applicationContext, activityClass)
        startActivity(intent)
    }
}
