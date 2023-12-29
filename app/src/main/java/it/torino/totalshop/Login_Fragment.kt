package it.torino.totalshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions

class Login_Fragment: Fragment() {
    val userType : Int = arguments?.getInt("UserType") ?: 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// valutare se mettere le animazioni
//        val options = navOptions {
//            anim {
//                enter = R.anim.slide_in_right
//                exit = R.anim.slide_out_left
//                popEnter = R.anim.slide_in_left
//                popExit = R.anim.slide_out_right
//            }
//        }

        view.findViewById<Button>(R.id.loginuserbtn)?.setOnClickListener {
            login()
        }

        view.findViewById<Button>(R.id.btnregistra)?.setOnClickListener {
            findNavController().navigate(R.id.register_page,arguments)
        }
    }
    fun login(){

        //TODO gestione login
        /*
        val intent = if(userType == 0){
            Intent(activity, UtenteActivity::class.java)
        } else {
            Intent(activity, VenditoreActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        */
    }
}