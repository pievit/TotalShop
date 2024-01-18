package it.torino.totalshop.views.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.torino.totalshop.NotificationService
import it.torino.totalshop.R
import it.torino.totalshop.adapter.UserSettingsAdapter
import it.torino.totalshop.views.login.LoginActivity
import it.torino.totalshop.roomdb.entities.StoreData
import it.torino.totalshop.roomdb.entities.UsersData
import it.torino.totalshop.RoomViewModel

class SettingsListFragment : Fragment() {
    private var vm : RoomViewModel? = null
    private var store : StoreData? = null
    private lateinit var user : UsersData
    private lateinit var recview : RecyclerView
    private lateinit var adapter: UserSettingsAdapter
    private val frag2 : SettingsItemFragment = SettingsItemFragment()
    private lateinit var logoutDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        return inflater.inflate(R.layout.user_settings_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var toolbartitle = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
        toolbartitle.text = "Settings"
        var backbtn = requireActivity().findViewById<ImageButton>(R.id.backButton)
        backbtn.visibility = View.INVISIBLE
        vm?.getUser(requireActivity().intent.getStringExtra("email")!!,requireActivity().intent.getBooleanExtra("userType",false))
        vm?.user?.observe(viewLifecycleOwner){
                uservm ->
            user = uservm
            view.findViewById<TextView>(R.id.user_settings_email).text = user.email
            view.findViewById<TextView>(R.id.user_settings_name).text = user.name
            if(user.userType){
                vm?.getStore(user.email)
            }
        }
        vm?.store?.observe(viewLifecycleOwner){
                storevm ->
            store = storevm
        }
        var items : ArrayList<Pair<String,String>> = arrayListOf()

        var logoutBuilder = AlertDialog.Builder(requireActivity())
        logoutBuilder.setMessage("Vuoi effettuare il logout?")
            .setPositiveButton("Logout"){
                    _,_ ->
                var sp = requireActivity().getSharedPreferences("USER",Context.MODE_PRIVATE)
                sp.edit().clear().apply()
                val intentstopNot = Intent(requireActivity().applicationContext, NotificationService::class.java)
                requireActivity().stopService(intentstopNot)
                vm?.clearBeforeLogout()
                var intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            .setNegativeButton("Annulla"){
                    _,_ -> logoutDialog.dismiss()
            }
        logoutDialog = logoutBuilder.create()

        items.add(Pair("person_24px","Account"))
        if(requireActivity().intent.getBooleanExtra("userType",false))
            items.add(Pair("store_logo_24px","Gestisci Store"))
        items.add(Pair("lock_24px","Modifica Password"))
        items.add(Pair("notifications_24px","Gestisci Notifiche"))
        items.add(Pair("logout_24px","Logout"))

        recview = view.findViewById(R.id.user_settings_recview)
        recview.setHasFixedSize(true)
        recview.layoutManager = LinearLayoutManager(requireActivity())
        adapter = UserSettingsAdapter(requireContext(),items){
            selectedItem ->
                if(selectedItem == "Logout"){
                    logoutDialog.show()

                }else{
                    frag2.settname = selectedItem
                    var fragMan = parentFragmentManager.beginTransaction()
                    fragMan.replace(R.id.user_settings_fragmentview,frag2)
                    fragMan.addToBackStack("settings")
                    fragMan.commit()
                }
        }

        recview.adapter = adapter
    }
}