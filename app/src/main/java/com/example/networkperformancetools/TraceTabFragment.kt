package com.example.networkperformancetools

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TraceTabFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TraceTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TraceTabFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var setText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trace_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tView = view.findViewById<TextView>(R.id.traceOutput)
        val pBar = view.findViewById<ProgressBar>(R.id.progressBarTrace)

        tView.movementMethod = ScrollingMovementMethod()


        val bttn = view.findViewById<Button>(R.id.traceButton)
        bttn?.setOnClickListener { v ->
            when (v.id) {
                R.id.traceButton -> {
                    object : AsyncTask<Void, String, Void>() {
                        override fun onPreExecute() {
                            super.onPreExecute()
                            pBar.visibility = View.VISIBLE
                        }
                        override fun onPostExecute(aVoid: Void?) {
                            super.onPostExecute(aVoid)
                            pBar.visibility= View.GONE
                        }

                        override fun onProgressUpdate(vararg values: String?) {
                            super.onProgressUpdate(*values)
                            tView?.text = setText
                        }

                        override fun doInBackground(vararg params: Void): Void? {

                            for (i in 1..15) {
                                if (i == 1){
                                    setText = trace(addressInput(view), i)
                                } else {
                                    setText += (trace(addressInput(view), i))
                                }
                                publishProgress(setText)
                            }
                            return null
                        }
                    }.execute()
                }
                else -> {
                }
            }
        }
    }

    @Throws(RuntimeException::class)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TraceTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TraceTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun trace(url: String, iterator: Int): String {
        var str = ""
        try {
            val process = Runtime.getRuntime().exec(
                "ping -t $iterator -c 1 -U $url"
            )
            val reader = BufferedReader(
                InputStreamReader(
                    process.inputStream
                )
            )
            val buffer = CharArray(512)
            val output = StringBuffer()

            reader.read(buffer)

            for (char in buffer) {
                output.append(char)
            }

            reader.close()

            str = output.toString().trim()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "$str\n"
    }

    private fun addressInput(v: View): String {
        val addressTextView = v.findViewById<TextInputEditText>(R.id.addressInText)
        return addressTextView.text.toString().trim()
    }
}