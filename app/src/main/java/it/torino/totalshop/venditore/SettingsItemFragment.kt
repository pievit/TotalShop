package it.torino.totalshop.venditore

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.viewModel

class SettingsItemFragment : Fragment() {

    private var vm : viewModel? = null
    private val settname : String = requireArguments().getString("settname")!!
    private val userType : Boolean = requireActivity().intent.getBooleanExtra("userType",false)
    private lateinit var user : UsersData
    private var store : StoreData? = null
    var updateuser : Boolean = false
    var updatestore : Boolean = false
    var updatelocation : Boolean = false
    var locationVM: LocationViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
        locationVM = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        val layout : Int = when(settname){
            "Account" -> R.layout.user_settings_item_account
            "Modifica Password" -> R.layout.user_settings_item_password
            "Gestisci Store" -> R.layout.user_settings_item_store
            "Gestisci Notifiche" -> R.layout.user_settings_item_notification
            else -> 0
        }
        if(layout != 0){
            return inflater.inflate(layout,container,false)
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var toolbartitle = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
        toolbartitle.text = settname
        var backbtn = requireActivity().findViewById<Button>(R.id.backButtonVendor)
        backbtn.visibility = View.VISIBLE
        vm?.getUser(requireActivity().intent.getStringExtra("email")!!,userType)
        vm?.user?.observe(viewLifecycleOwner){
            uservm ->
            user = uservm
            if(user.userType){
                vm?.getStore(user.email)
            }
            if(updateuser){
                updateuser = false
                Toast.makeText(context,"Modifiche Account apportate con successo",Toast.LENGTH_SHORT).show()
            }
        }
        vm?.store?.observe(viewLifecycleOwner){
                storevm ->
            store = storevm
            if(updatestore){
                updatestore = false
                Toast.makeText(context,"Modifiche dello Store apportate con successo",Toast.LENGTH_SHORT).show()
            }
            if(updatelocation){
                updatelocation = false
                Toast.makeText(context,"Posizione store aggiornata con successo",Toast.LENGTH_SHORT).show()
            }
        }

        locationVM?.locationData?.observe(viewLifecycleOwner){
            locationdata ->
            if(!(locationdata.latitude == 0.0 && locationdata.longitude == 0.0)){
                store?.lat = locationdata.latitude
                store?.lon = locationdata.longitude
                locationVM?.stopLocationUpdates()
                vm?.insertStore(store!!)
                updatelocation = true
                view.findViewById<TextView>(R.id.user_settings_item_store_locNotFound).visibility = View.GONE
                view.findViewById<Button>(R.id.user_settings_item_store_btnpos).text = "Aggiorna Posizione Store"
            }
        }

        when(settname){
            "Account" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_account_btnsave)
                var name = view.findViewById<EditText>(R.id.user_settings_item_account_name)
                    name.setText(user.name)
                name.addTextChangedListener {
                    text ->
                        btn.setEnabled(true)
                }
                var email = view.findViewById<EditText>(R.id.user_settings_item_account_email)
                    email.setText(user.email)
                email.addTextChangedListener {
                    text ->
                        btn.setEnabled(true)
                }
                var numero = view.findViewById<EditText>(R.id.user_settings_item_account_phone)
                    numero.setText(user.phone)
                numero.addTextChangedListener {
                    text ->
                        btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    user.name = name.text.toString()
                    user.email = email.text.toString()
                    user.phone = numero.text.toString()
                    updateuser = true
                    btn.setEnabled(false)
                    vm?.insertUser(user)
                }
            }
            "Modifica Password" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_password_btnsave)
                var oldpsw = view.findViewById<EditText>(R.id.user_settings_item_password_oldpass)

                var newpsw1 = view.findViewById<EditText>(R.id.user_settings_item_password_newpass1)

                var newpsw2 = view.findViewById<EditText>(R.id.user_settings_item_password_newpass2)
                newpsw2.addTextChangedListener {
                        text ->
                    btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    if(newpsw1.text.toString().length >=4 &&
                        newpsw1.text.toString() != oldpsw.text.toString() &&
                        newpsw2.text.toString() == newpsw1.text.toString()){
                        user.password = newpsw1.text.toString()
                        updateuser = true
                        btn.setEnabled(false)
                        vm?.insertUser(user)
                    } else {
                        Toast.makeText(context,"La nuova password non rispetta i prerequisiti",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "Gestisci Store" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_store_btnsave)
                var name = view.findViewById<EditText>(R.id.user_settings_item_store_name)
                name.setText(store?.storeName)
                name.addTextChangedListener {
                        text ->
                    btn.setEnabled(true)
                }
                var categoria = view.findViewById<EditText>(R.id.user_settings_item_store_categoria)
                categoria.setText(store?.storeCategory)
                categoria.addTextChangedListener {
                        text ->
                    btn.setEnabled(true)
                }
                var indirizzo = view.findViewById<EditText>(R.id.user_settings_item_store_indirizzo)
                indirizzo.setText(store?.storeAddress)
                indirizzo.addTextChangedListener {
                        text ->
                    btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    store?.storeName = name.text.toString()
                    store?.storeCategory = categoria.text.toString()
                    store?.storeAddress = indirizzo.text.toString()
                    updatestore = true
                    btn.setEnabled(false)
                    vm?.insertStore(store!!)
                }

                var btnpos = view.findViewById<Button>(R.id.user_settings_item_store_btnpos)
                btnpos.setOnClickListener(){
                    location()
                }
                var alert = view.findViewById<TextView>(R.id.user_settings_item_store_locNotFound)
                if (store?.lat == null && store?.lon == null){
                    alert.visibility = View.VISIBLE
                } else {
                    btnpos.text = "Aggiorna Posizione Store"
                }
            }
            "Gestisci Notifiche" -> {
                var notswitch = view.findViewById<SwitchMaterial>(R.id.user_settings_item_notification_notswitch)
                var check = false //TODO fare controllo se notifiche attivate
                notswitch.isChecked = check

                notswitch.setOnCheckedChangeListener(){
                    _, isChecked ->
                    if(isChecked){
                        //TODO attiva le notifiche in caso non lo siano
                    }else{
                        //TODO disattiva le notifiche
                    }
                }

            }
            else -> 0
        }
    }

    private fun location(){

        if (ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Necessaria autorizzazione posizione esatta")
                    .setMessage("Per funzionare correttamente, l'applicazione necessita l'autorizzazione per accedere alla posizione precisa")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }

        }else{
            Log.d("Test","start ls" )
            locationVM?.startLocationService()
        }
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            locationVM!!.MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
}