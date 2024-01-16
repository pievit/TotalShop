package it.torino.totalshop.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import it.torino.totalshop.R
import it.torino.totalshop.utente.UtenteActivity
import it.torino.totalshop.venditore.VenditoreActivity

class HomeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home, container, false)
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
        val arg : Bundle = Bundle()

        view.findViewById<Button>(R.id.loginutentebtn)?.setOnClickListener {
            arg.putInt("UserType", 0)
//            Navigation.createNavigateOnClickListener(R.id.next_action, arg)
            findNavController().navigate(R.id.next_action,arg)
        }

        view.findViewById<Button>(R.id.loginvenditorebtn)?.setOnClickListener {
            arg.putInt("UserType", 1)
//            Navigation.createNavigateOnClickListener(R.id.next_action, arg)
            findNavController().navigate(R.id.next_action,arg)
        }

//        val sharedPref = requireActivity().getSharedPreferences("USER",Context.MODE_PRIVATE)
//        with(sharedPref){
//            if(contains("USER_EMAIL")&& contains("USER_PASSWORD") && contains("USER_TYPE")){
//                var intent: Intent
//                if(getBoolean("USER_TYPE",false)){
//                    intent = Intent(activity, VenditoreActivity::class.java)
//                }else{
//                    intent = Intent(activity, UtenteActivity::class.java)
//                }
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("email",getString("USER_EMAIL",null))
//                startActivity(intent)
//            }
//        }
    }

}