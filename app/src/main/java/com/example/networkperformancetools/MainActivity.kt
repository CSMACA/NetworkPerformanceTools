package com.example.networkperformancetools


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.EditText
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager


const val DEV_EMAIL_RESPONSE = 1

class MainActivity : AppCompatActivity(),
    PingTabFragment.OnFragmentInteractionListener,
    TraceTabFragment.OnFragmentInteractionListener,
    SpeedTab.OnFragmentInteractionListener {

    var pFragObj : Fragment? = null
    var tFragObj : Fragment? = null
    var sFragObj : Fragment? = null

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        tabLayoutViewPager.adapter = mSectionsPagerAdapter

        tabLayoutViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(tabLayoutViewPager))

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

        when(id){
            R.id.action_settings ->{
                startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
            }
            R.id.action_feedback ->{
                val sendToDev = Intent(Intent.ACTION_SENDTO)
                sendToDev.data = Uri.parse("mailto:")
                with(sendToDev) {
                    putExtra(android.content.Intent.EXTRA_EMAIL, kotlin.Array(1) { "netperformancetools@gmail.com" })
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Bug Report")
                }
                try {
                    startActivityForResult(sendToDev, DEV_EMAIL_RESPONSE)
                } catch (e: ActivityNotFoundException) {}
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //What happens after bug button is done.
        if (requestCode == DEV_EMAIL_RESPONSE) {
            if (resultCode == Activity.RESULT_OK) {
                val emailSentSnackbar = "Bug Report Sent Successfully"
                val duration = Snackbar.LENGTH_LONG
                val snackbar = Snackbar.make(main_content, emailSentSnackbar, duration)
                snackbar.show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val emailNotSentSnackbar = "Bug Report Not Sent"
                val duration = Snackbar.LENGTH_LONG
                val snackbar = Snackbar.make(main_content, emailNotSentSnackbar, duration)
                snackbar.show()
            }
        }
    }

    //Setup and manage fragments over tabs.
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment = PingTabFragment()

            when (position) {
                0 -> fragment = PingTabFragment()      //Use .newInstance(param1, param2) if possible for each fragment.
                1 -> fragment = TraceTabFragment()
                2 -> fragment = SpeedTab()
            }

            return fragment

        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    override fun onAttachFragment(fragment: Fragment?) = Unit
    override fun onFragmentInteraction(uri:Uri) = Unit


    //Unfocuses input fields when tapping outside of said fields.
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


}
