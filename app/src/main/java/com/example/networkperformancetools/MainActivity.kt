package com.example.networkperformancetools

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

const val DEV_EMAIL_RESPONSE = 1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        bugButton()

        val screenOrientation = resources.configuration.orientation
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showSidePanel()
        }

    }

    private fun showSidePanel() {
        val sidePane = findViewById<View>(R.id.side_panel)
        if (sidePane.visibility == View.GONE) {
            sidePane.visibility = View.VISIBLE
        }
    }

    private fun bugButton(){
        //Handles bug report button.
        val sendToDev = Intent(Intent.ACTION_SENDTO)
        fab.setOnClickListener {
            sendToDev.data = Uri.parse("mailto:")
            with(sendToDev) {
                putExtra(Intent.EXTRA_EMAIL, Array(1) { "netperformancetools@gmail.com" })
                putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
            }
            try {
                startActivityForResult(sendToDev, DEV_EMAIL_RESPONSE)
            } catch (e: ActivityNotFoundException) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //What happens after bug button is done.
        if (requestCode == DEV_EMAIL_RESPONSE){
            if (resultCode == Activity.RESULT_OK){
                val emailSentToast = "Bug Report Sent Successfully"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, emailSentToast, duration)
                toast.show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val emailSentToast = "Bug Report Not Sent"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, emailSentToast, duration)
                toast.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
