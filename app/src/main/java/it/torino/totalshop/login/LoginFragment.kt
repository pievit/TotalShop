package it.torino.totalshop.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.torino.totalshop.login.LoginActivity
import androidx.navigation.navOptions
import it.torino.totalshop.roomdb.entities.UsersData
import androidx.navigation.Navigation
import it.torino.totalshop.R

class LoginFragment: Fragment() {
    var userType : Int? = null
    var vm: viewModel? = null
    var flagLogin: Boolean = false
    var button :Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userType = arguments?.getInt("UserType") ?: 0
        vm = ViewModelProvider(requireActivity())[viewModel::class.java]
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

        button = view?.findViewById<Button>(R.id.loginuserbtn)
        vm?.user?.observe(viewLifecycleOwner){
            res ->
            if(res!=null){
                var pass = view?.findViewById<TextView>(R.id.password)?.text

                if(res.password.equals(pass.toString())){
                    //login
                    Toast.makeText(context,"Utente Loggato con successo.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"La password inserità non è corretta, riprova.",Toast.LENGTH_SHORT).show()
                }

            }else{
                if(flagLogin){
                    val msg = if(userType==0) "L'utente inserito non è presente nel db" else "Il venditore inserito non è presente nel db"
                    Toast.makeText(context,msg ,Toast.LENGTH_SHORT).show()
                }
            }
            flagLogin = false
            button?.isEnabled = true
        }

        view.findViewById<Button>(R.id.loginuserbtn)?.setOnClickListener {
            login()
        }

        view.findViewById<Button>(R.id.btnregistra)?.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.next_action, arguments)
        }
    }
    fun login(){
        button?.isEnabled = false
        flagLogin = true
        var email = view?.findViewById<TextView>(R.id.user)?.text.toString()
        var ut: Boolean = if(userType==1) true else false
        vm?.getUser(email,ut)
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