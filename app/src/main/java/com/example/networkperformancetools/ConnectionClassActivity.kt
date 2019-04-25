/*
 *  This file provided by Facebook is for non-commercial testing and evaluation
 *  purposes only.  Facebook reserves all rights not expressly granted.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.example.networkperformancetools

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import java.io.IOException
import java.net.URL


class ConnectionClassActivity : Activity() {

    private var mConnectionClassManager: ConnectionClassManager? = null
    private var mDeviceBandwidthSampler: DeviceBandwidthSampler? = null
    private var mListener: ConnectionChangedListener? = null
    private var mTextView: TextView? = null
    private var mRunningBar: View? = null

    private val mURL = "http://connectionclass.parseapp.com/m100_hubble_4060.jpg"
    private var mTries = 0
    private var mConnectionClass = ConnectionQuality.UNKNOWN

    private val testButtonClicked = View.OnClickListener { DownloadImage().execute(mURL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mConnectionClassManager = ConnectionClassManager.getInstance()
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance()
        findViewById<Button>(R.id.test_btn).setOnClickListener(testButtonClicked)
        mTextView = findViewById<TextView>(R.id.connection_class)
        mTextView!!.text = mConnectionClassManager!!.currentBandwidthQuality.toString()
        mRunningBar = findViewById(R.id.runningBar)
        mRunningBar!!.visibility = View.GONE
        mListener = ConnectionChangedListener()
    }

    override fun onPause() {
        super.onPause()
        mConnectionClassManager!!.remove(mListener)
    }

    override fun onResume() {
        super.onResume()
        mConnectionClassManager!!.register(mListener)
    }

    /**
     * Listener to update the UI upon connectionclass change.
     */
    private inner class ConnectionChangedListener : ConnectionClassManager.ConnectionClassStateChangeListener {

        override fun onBandwidthStateChange(bandwidthState: ConnectionQuality) {
            mConnectionClass = bandwidthState
            runOnUiThread { mTextView!!.text = mConnectionClass.toString() }
        }
    }

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private inner class DownloadImage : AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            mDeviceBandwidthSampler!!.startSampling()
            mRunningBar!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg url: String): Void? {
            val imageURL = url[0]
            try {
                // Open a stream to download the image from our URL.
                val connection = URL(imageURL).openConnection()
                connection.useCaches = false
                connection.connect()
                val input = connection.getInputStream()
                try {
                    val buffer = ByteArray(1024)

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error while downloading image.")
            }

            return null
        }

        override fun onPostExecute(v: Void) {
            mDeviceBandwidthSampler!!.stopSampling()
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++
                DownloadImage().execute(mURL)
            }
            if (!mDeviceBandwidthSampler!!.isSampling) {
                mRunningBar!!.visibility = View.GONE
            }
        }
    }

    companion object {

        private val TAG = "ConnectionClass-Sample"
    }
}
