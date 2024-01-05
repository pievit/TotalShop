package it.torino.totalshop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class RegisterFragment: Fragment() {
    var vm: viewModel? = null
    var ins: Boolean = false
    var userType: Int = arguments?.getInt("UserType") ?: 0
    var button: Button? = null
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.register, container, false)
        val safeArgs: RegisterFragmentArgs by navArgs()
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
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
                Log.d("debug","enterObs")
                if(ins){
                    if(arguments?.getInt("UserType")==1){
                        Log.d("Debug","enterVendor")
                        var storeName = view?.findViewById<TextView>(R.id.businessname)?.text.toString()
                        var storeAddress = view?.findViewById<TextView>(R.id.businsessaddress)?.text.toString()
                        var storeCategory = view?.findViewById<TextView>(R.id.businesscategory)?.text.toString()
                        var storeData: StoreData = StoreData(storeName,storeAddress,storeCategory,res.email)
                        vm?.insertStore(storeData)

                    }else{
                        Log.d("debug","enterUser")
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
            Log.d("debug","store: "+res.toString())
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
            register()
        }
    }

    fun register(){

        button?.isEnabled = false
        var email = view?.findViewById<TextView>(R.id.reguser)?.text.toString()
        var pass = view?.findViewById<TextView>(R.id.regpass)?.text.toString()
        var ut: Boolean = if(arguments?.getInt("UserType")==1) true else false
        var phone = view?.findViewById<TextView>(R.id.regphone)?.text.toString()
        var ud = UsersData(email,pass,phone,ut)
        vm?.insertUser(ud)

    }


}