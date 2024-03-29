package it.torino.totalshop.views.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.RoomViewModel

class RegisterFragment: Fragment() {
    var vm: RoomViewModel? = null
    var ins: Boolean = false
    var userType: Int = arguments?.getInt("UserType") ?: 0
    var button: Button? = null
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.register, container, false)
        val safeArgs: RegisterFragmentArgs by navArgs()
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        if(safeArgs.UserType == 1) {
            val constraintLayout = view.findViewById<ConstraintLayout>(R.id.cont2)
            val layoutInflater = LayoutInflater.from(context)
            val inflatedView = layoutInflater.inflate(R.layout.vendor_inputs, constraintLayout, false)
            constraintLayout.addView(inflatedView)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view?.findViewById<Button>(R.id.regbtn)

        vm?.user?.observe(viewLifecycleOwner){
            res ->
            if(res != null){
                if(ins){
                    if(arguments?.getInt("UserType")==1){
                        var storeName = view?.findViewById<TextView>(R.id.businessname)?.text.toString()
                        var storeAddress = view?.findViewById<TextView>(R.id.businsessaddress)?.text.toString()
                        var storeCategory = view?.findViewById<TextView>(R.id.businesscategory)?.text.toString()
                        var storeData: StoreData = StoreData(storeName,storeAddress,storeCategory,res.email,null,null)
                        vm?.insertStore(storeData)

                    }else{
                        Toast.makeText(context,"Registrazione utente effettuata", Toast.LENGTH_SHORT).show()
                        button?.isEnabled = true
                        findNavController().popBackStack().also { vm!!.user?.value = null }
                    }
                }else{
                    Toast.makeText(context,"Email già utilizzata, effettuare il login", Toast.LENGTH_SHORT).show()
                }
            }
        }


        vm?.store?.observe(viewLifecycleOwner){
            res ->
            if(res!=null){

                Toast.makeText(context,"Registrazione venditore effettuata", Toast.LENGTH_SHORT).show()
                button?.isEnabled = true
                findNavController().popBackStack().also{
                    vm?.store?.value = null
                    vm?.user?.value = null
                }
            }
        }


        vm?.inserito?.observe(viewLifecycleOwner){
            res -> ins = res
        }

        view.findViewById<View>(R.id.regbtn)?.setOnClickListener {
            if(regexData())
                register()
            else
                Toast.makeText(context, "Dati inseriti non validi. Riprova",Toast.LENGTH_SHORT).show()
        }
    }

    fun regexData() : Boolean {
        var email = view?.findViewById<TextView>(R.id.reguser)?.text.toString()
        var pass = view?.findViewById<TextView>(R.id.regpass)?.text.toString()
        var phone = view?.findViewById<TextView>(R.id.regphone)?.text.toString()

        val phonereg = Regex("\\A(\\+39)?[0-9]{9,10}\$")
        val emailreg = Regex("\\A([a-zA-Z0-9\\.\\-])+(@)?([a-zA-Z\\-]{2,})(\\.{1}[a-zA-Z]{2,}){1,2}\$")
        return pass.length>=4 && phone.matches(phonereg) && email.matches(emailreg)
    }
    fun register(){

        button?.isEnabled = false
        var email = view?.findViewById<TextView>(R.id.reguser)?.text.toString()
        var pass = view?.findViewById<TextView>(R.id.regpass)?.text.toString()
        var ut: Boolean = if(arguments?.getInt("UserType")==1) true else false
        var phone = view?.findViewById<TextView>(R.id.regphone)?.text.toString()
        var name = view?.findViewById<TextView>(R.id.regname)?.text.toString()
        var ud = UsersData(email,pass,name,phone,ut)
        vm?.insertUser(ud)

    }


}