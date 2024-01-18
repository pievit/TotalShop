package it.torino.totalshop.views.venditore

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
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

class DettagliFragmentVenditore : Fragment() {
    var vm: RoomViewModel? = null
    var order : OrdersData? = null
    private lateinit var annDialog: AlertDialog
    private lateinit var annDialogView: View
    private lateinit var confDialog: AlertDialog
    private lateinit var confDialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.venditore_ordini_info, container, false)
        view.findViewById<FloatingActionButton>(R.id.vendor_floatingbtn).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        if(isOrientationLandscape()){
            view.findViewById<FloatingActionButton>(R.id.vendor_floatingbtn).visibility = View.INVISIBLE
        }else{
            view.findViewById<FloatingActionButton>(R.id.vendor_floatingbtn).visibility = View.VISIBLE
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

        vm?.user?.observe(viewLifecycleOwner){
                user ->
            view.findViewById<TextView>(R.id.vendor_ord_inf_phone)?.text = user.phone
        }

        val annBuilder = AlertDialog.Builder(requireActivity())
        val annInfl8r = requireActivity().layoutInflater
        annDialogView = annInfl8r.inflate(R.layout.ann_ord_dialog,null)
        annBuilder.setView(annDialogView)
        annDialog = annBuilder.create()

        val confBuilder = AlertDialog.Builder(requireActivity())
        val confInfl8r = requireActivity().layoutInflater
        confDialogView = confInfl8r.inflate(R.layout.conf_ord_dialog,null)
        confBuilder.setView(confDialogView)
        confDialog = confBuilder.create()



    }
    fun update(){
        vm?.getUser(order!!.usermail,false)
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

            if(order?.status != "nuovo") {
                view?.findViewById<TextView>(R.id.vendor_ord_inf_commento)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.vendor_ord_inf_commento)?.text = order?.comment
                view?.findViewById<TextView>(R.id.vendor_comment_title)?.visibility = View.VISIBLE
            }else{
                view?.findViewById<TextView>(R.id.vendor_comment_title)?.visibility = View.GONE
                view?.findViewById<TextView>(R.id.vendor_ord_inf_commento)?.visibility = View.GONE
            }
            view?.findViewById<TextView>(R.id.vendor_ord_inf_email)?.text = order?.usermail
            view?.findViewById<TextView>(R.id.vendor_ord_inf_id)?.text = "Ordine N." + order?.id
            view?.findViewById<TextView>(R.id.vendor_ord_inf_data)?.text = order?.dataOrd
            view?.findViewById<TextView>(R.id.vendor_ord_inf_status)?.text = order?.status

            var sum = prods.keys.sumOf { key ->
                (key.price * prods.get(key)!!).toDouble()
            }

            view?.findViewById<TextView>(R.id.vendor_ord_inf_totprice)?.text = "Prezzo Totale: "+((sum*100).roundToInt().toDouble()/100).toString() + " €"
            view?.findViewById<TextView>(R.id.vendor_ord_inf_prodlist)?.text = ProdsList(prods).toString()

            if(order?.status.equals("nuovo")){

                view?.findViewById<Button>(R.id.vendor_but_ann_ord)?.setOnClickListener{
                    annDialog.show()
                    annDialogView.findViewById<Button>(R.id.ann_dialog_indietro_but).setOnClickListener{
                        annDialog.dismiss()
                    }
                    annDialogView.findViewById<Button>(R.id.ann_dialog_ann_but).setOnClickListener{
                        annullaOrdine()
                    }
                }

                view?.findViewById<Button>(R.id.vendor_but_conf_ord)?.setOnClickListener{
                    confDialog.show()
                    confDialogView.findViewById<Button>(R.id.conf_dialog_indietro_but).setOnClickListener{
                        confDialog.dismiss()
                    }
                    confDialogView.findViewById<Button>(R.id.conf_dialog_conf_but).setOnClickListener{
                        confermaOrdine()
                    }
                }

            }else{
                view?.findViewById<ConstraintLayout>(R.id.vendor_order_status_buttons)?.visibility = INVISIBLE
            }
        }

    }


    fun annullaOrdine(){
        var comment = annDialogView.findViewById<EditText>(R.id.ann_dialog_comment).text.toString()
        if(comment.length>10){
            order?.status = "Annullato"
            order?.comment = comment
            vm?.insertOrder(order!!)
            update()
            annDialog.dismiss()
            Toast.makeText(requireActivity(),"Ordine Annullato",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireActivity(),"Inserisci un commento di almeno 10 caratteri",Toast.LENGTH_SHORT).show()
        }
    }


    fun confermaOrdine(){
        var comment = confDialogView.findViewById<EditText>(R.id.conf_dialog_comment).text.toString()
        if(comment.length>10){
            order?.status = "Confermato"
            order?.comment = comment
            vm?.insertOrder(order!!)
            update()
            confDialog.dismiss()
            Toast.makeText(requireActivity(),"Ordine Confermato",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireActivity(),"Inserisci un commento di almeno 10 caratteri",Toast.LENGTH_SHORT).show()
        }
    }

    fun isOrientationLandscape(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}