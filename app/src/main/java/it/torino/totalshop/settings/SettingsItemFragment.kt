package it.torino.totalshop.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import it.torino.totalshop.LocationViewModel
import it.torino.totalshop.NotificationService
import it.torino.totalshop.R
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.viewModel

class SettingsItemFragment : Fragment() {

    private var vm : viewModel? = null
    var settname : String = ""
    private var userType: Boolean = false
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

//        settname = requireArguments().getString("settname")!!
        userType = requireActivity().intent.getBooleanExtra("userType",false)
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
        lateinit var name : EditText
        lateinit var email : TextView
        lateinit var numero : EditText
        lateinit var storename : EditText
        lateinit var categoria : EditText
        lateinit var indirizzo : EditText
        lateinit var alert : TextView
        lateinit var btnpos : Button
        if(settname == "")
        {
            var frag1 = SettingsListFragment()
            var fragMan = parentFragmentManager.beginTransaction()
            fragMan.replace(R.id.user_settings_fragmentview,frag1)
            fragMan.commit()
        }
        when(settname){
            "Account" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_account_btnsave)
                name = view.findViewById(R.id.user_settings_item_account_name)
                email = view.findViewById(R.id.user_settings_item_account_email)
                numero = view.findViewById(R.id.user_settings_item_account_phone)

                name.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }

                numero.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    user.name = name.text.toString()
                    user.phone = numero.text.toString()
                    updateuser = true
                    btn.setEnabled(false)
                    vm?.modifyUser(user)
                }
            }
            "Modifica Password" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_password_btnsave)
                var oldpsw = view.findViewById<EditText>(R.id.user_settings_item_password_oldpass)

                var newpsw1 = view.findViewById<EditText>(R.id.user_settings_item_password_newpass1)

                var newpsw2 = view.findViewById<EditText>(R.id.user_settings_item_password_newpass2)
                newpsw2.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    if(newpsw1.text.toString().length >=4 &&
                        newpsw1.text.toString() != oldpsw.text.toString() &&
                        newpsw2.text.toString() == newpsw1.text.toString()){
                        user.password = newpsw1.text.toString()
                        updateuser = true
                        btn.setEnabled(false)
                        vm?.modifyUser(user)
                    } else {
                        Toast.makeText(context,"La nuova password non rispetta i prerequisiti",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "Gestisci Store" -> {
                var btn = view.findViewById<Button>(R.id.user_settings_item_store_btnsave)

                storename = view.findViewById(R.id.user_settings_item_store_name)
                categoria = view.findViewById(R.id.user_settings_item_store_categoria)
                indirizzo = view.findViewById(R.id.user_settings_item_store_indirizzo)

                storename.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }
                categoria.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }
                indirizzo.addTextChangedListener {
                        _ ->
                    btn.setEnabled(true)
                }
                btn.setOnClickListener(){
                    store?.storeName = storename.text.toString()
                    store?.storeCategory = categoria.text.toString()
                    store?.storeAddress = indirizzo.text.toString()
                    updatestore = true
                    btn.setEnabled(false)
                    vm?.insertStore(store!!)
                }

                btnpos = view.findViewById(R.id.user_settings_item_store_btnpos)
                btnpos.setOnClickListener(){
                    locationVM?.startLocationService()
                }
                alert = view.findViewById(R.id.user_settings_item_store_locNotFound)

            }
            "Gestisci Notifiche" -> {
                var notswitch = view.findViewById<SwitchMaterial>(R.id.user_settings_item_notification_notswitch)

                var sp = requireActivity().getSharedPreferences("NOTIFY", Context.MODE_PRIVATE)

                notswitch.isChecked = sp.getBoolean("NOTIFICATIONS",false)

                notswitch.setOnCheckedChangeListener(){
                        _, isChecked ->
                    if(isChecked){
                        sp.edit().putBoolean("NOTIFICATIONS",isChecked)

                        val intent = Intent(requireActivity().applicationContext,NotificationService::class.java)
                        requireActivity().startService(intent)
                    }else{
                        sp.edit().putBoolean("NOTIFICATIONS",isChecked)
                        val intent = Intent(requireActivity().applicationContext,NotificationService::class.java)
                        requireActivity().stopService(intent)
                    }
                }

            }
            else -> 0
        }





        val toolbartitle = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
        toolbartitle.text = settname
        val backbtn = requireActivity().findViewById<AppCompatImageButton>(R.id.backButton)
        backbtn.visibility = View.VISIBLE
        backbtn.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        vm?.getUser(requireActivity().intent.getStringExtra("email")!!,userType)
        vm?.user?.observe(viewLifecycleOwner){
            uservm ->
            user = uservm

            if(settname == "Account"){
                name.setText(user.name)
                email.setText(user.email)
                numero.setText(user.phone)
            }

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
            if(settname == "Gestisci Store"){
                storename.setText(store?.storeName)
                categoria.setText(store?.storeCategory)
                indirizzo.setText(store?.storeAddress)
                if (store?.lat == null && store?.lon == null){
                    alert.visibility = View.VISIBLE
                } else {
                    btnpos.text = "Aggiorna Posizione Store"
                }
            }
            if(updatestore){
                updatestore = false
                Toast.makeText(context,"Modifiche dello Store apportate con successo",Toast.LENGTH_SHORT).show()
            }
            if(updatelocation){
                updatelocation = false
                Toast.makeText(context,"Posizione store aggiornata con successo",Toast.LENGTH_SHORT).show()
            }
        }
        if(userType){
            locationVM?.locationData?.observe(viewLifecycleOwner){
                    locationdata ->
                if(locationdata != null && !(locationdata.latitude == 0.0 && locationdata.longitude == 0.0)){
                    store?.lat = locationdata.latitude
                    store?.lon = locationdata.longitude
                    locationVM?.stopLocationUpdates()
                    vm?.insertStore(store!!)
                    updatelocation = true
                    alert.visibility = View.GONE
                    btnpos.text = "Aggiorna Posizione Store"
                    locationVM?.locationData?.value = null
                }
            }
        }


    }


}