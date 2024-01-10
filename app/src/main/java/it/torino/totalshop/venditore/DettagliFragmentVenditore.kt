package it.torino.totalshop.venditore

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.venditore.OrdiniFragmentVenditore
import it.torino.totalshop.viewModel

class DettagliFragmentVenditore : Fragment() {
    var vm: viewModel? = null
    var order : OrdersData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.venditore_ordini_info, container, false)
        Log.d("Test","ok")
        view.findViewById<FloatingActionButton>(R.id.floatingbtn).setOnClickListener {
            childFragmentManager.popBackStack("ord_info_list",0)
        }
        if(isOrientationPortrait()){
            view.findViewById<FloatingActionButton>(R.id.floatingbtn).visibility = View.INVISIBLE
        }else{
            view.findViewById<FloatingActionButton>(R.id.floatingbtn).visibility = View.VISIBLE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(order != null){
            update()
        }
    }
    fun update(){
        view?.findViewById<TextView>(R.id.ord_inf_email)?.text = order?.usermail
        view?.findViewById<TextView>(R.id.ord_inf_id)?.text = "Ordine N." + order?.id
//        view?.findViewById<TextView>(R.id.ord_inf_data)?.text = order?.dataOrd
        view?.findViewById<TextView>(R.id.ord_inf_status)?.text = order?.status
        view?.findViewById<TextView>(R.id.ord_inf_commento)?.text = order?.comment
        view?.findViewById<TextView>(R.id.ord_inf_totprice)?.text = order?.listaProd!!.prods.keys.sumOf { it.price.toDouble() }.toString()
        view?.findViewById<TextView>(R.id.ord_inf_prodlist)?.text = order?.listaProd!!.toString()

    }

    fun isOrientationPortrait(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }
}