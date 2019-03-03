package com.example.networkperformancetools

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        //Handles bug report button.
        val sendToDev = Intent(Intent.ACTION_SENDTO)
        fab.setOnClickListener {
            sendToDev.data = Uri.parse("mailto:")
            with(sendToDev) {
                putExtra(Intent.EXTRA_EMAIL, Array(1){"official.alanroach@outlook.com"})
                putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
            }
            try {
                startActivityForResult(sendToDev, 1)
            } catch (e: ActivityNotFoundException) {

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
