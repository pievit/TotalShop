package it.torino.totalshop.utente

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.ProdsList
import it.torino.totalshop.roomdb.entities.OrdersData
import it.torino.totalshop.roomdb.entities.ProductsData
import it.torino.totalshop.RoomViewModel
import org.apache.commons.text.StringEscapeUtils
import kotlin.math.roundToInt

class DettagliFragmentUtente : Fragment() {
    var vm: RoomViewModel? = null
    var order : OrdersData? = null
    private lateinit var annDialog: AlertDialog
    private lateinit var annDialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.utente_ordini_info, container, false)
        view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if(isOrientationLandscape()){
            view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).visibility = View.INVISIBLE
        }else{
            view.findViewById<FloatingActionButton>(R.id.user_floatingbtn).visibility = View.VISIBLE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(order != null){
            view.visibility = View.VISIBLE
            update()
        }else{
            view.visibility = View.INVISIBLE
        }

        vm?.store?.observe(viewLifecycleOwner){
            store->
            view.findViewById<TextView>(R.id.user_ord_inf_store)?.text = store.storeName
        }

        vm?.user?.observe(viewLifecycleOwner){
            user ->
            view.findViewById<TextView>(R.id.user_ord_inf_phone)?.text = user.phone
        }


        val annBuilder = AlertDialog.Builder(requireActivity())
        val annInfl8r = requireActivity().layoutInflater
        annDialogView = annInfl8r.inflate(R.layout.ann_ord_dialog,null)
        annBuilder.setView(annDialogView)
        annDialog = annBuilder.create()

    }
    fun update(){
        vm?.getOwner(order!!.storeId)
        vm?.getStoreFromId(order!!.storeId)
        var lp = order?.listaProd
        if(lp?.length!=0) {
            lp = StringEscapeUtils.unescapeJava(lp)
            val type = object : TypeToken<Map<String, Int>>() {}.type
            val objectMap: Map<String, Int> = Gson().fromJson(lp, type)
            var prods: MutableMap<ProductsData, Int> = mutableMapOf<ProductsData, Int>()

            objectMap.keys.forEach {
                var v = it.substring(13, it.length - 2).split("',")
                var p = ProductsData(
                    name = v[0].split("='")[1], description = v[1].split("='")[1],
                    price = v[2].split("='")[1].toFloat(), storeId = v[3].split("='")[1].toInt()
                )
                prods[p] = objectMap.get(it)!!
            }

            view?.findViewById<TextView>(R.id.user_ord_inf_id)?.text = "Ordine N." + order?.id
            view?.findViewById<TextView>(R.id.user_ord_inf_data)?.text = order?.dataOrd
            view?.findViewById<TextView>(R.id.user_ord_inf_status)?.text = order?.status
            if(order?.status != "nuovo") {
                view?.findViewById<TextView>(R.id.user_ord_inf_commento)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.user_comment_title)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.user_ord_inf_commento)?.text = order?.comment
            }else{
                view?.findViewById<TextView>(R.id.user_comment_title)?.visibility = View.GONE
                view?.findViewById<TextView>(R.id.user_ord_inf_commento)?.visibility = View.GONE
            }
            var sum = prods.keys.sumOf { key ->
                (key.price * prods.get(key)!!).toDouble()
            }

            view?.findViewById<TextView>(R.id.user_ord_inf_totprice)?.text = "Prezzo Totale: "+((sum*100).roundToInt().toDouble()/100).toString() + " €"
            view?.findViewById<TextView>(R.id.user_ord_inf_prodlist)?.text = ProdsList(prods).toString()

            if(order?.status.equals("nuovo")){

                view?.findViewById<Button>(R.id.user_but_ann_ord)?.setOnClickListener{
                    annDialog.show()
                    annDialogView.findViewById<Button>(R.id.ann_dialog_indietro_but).setOnClickListener{
                        annDialog.dismiss()
                    }
                    annDialogView.findViewById<Button>(R.id.ann_dialog_ann_but).setOnClickListener{
                        cancellaOrdine()
                    }
                }


            }else{
                view?.findViewById<ConstraintLayout>(R.id.utente_order_status_buttons)?.visibility = View.INVISIBLE
            }


        }

    }

    fun cancellaOrdine(){
        var comment = annDialogView.findViewById<EditText>(R.id.ann_dialog_comment).text.toString()
        if(comment.length>10){
            order?.status = "Cancellato"
            order?.comment = comment
            vm?.insertOrder(order!!)
            update()
            annDialog.dismiss()
            Toast.makeText(requireActivity(),"Ordine Cancellato", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireActivity(),"Inserisci un commento di almeno 10 caratteri", Toast.LENGTH_SHORT).show()
        }
    }

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}