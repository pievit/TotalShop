package it.torino.totalshop.venditore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.torino.totalshop.R
import it.torino.totalshop.venditore.OrdiniFragmentVenditore

class DettagliFragmentVenditore : Fragment() {

    fun getOrderId() = arguments?.getInt("OrderId")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = inflater.inflate(R.layout.venditore_ordini_info, container, false)
        val orderId = getOrderId()
        Log.d("Test","ok")

//        if (order != null) {
//
//        }
        return null
    }

}