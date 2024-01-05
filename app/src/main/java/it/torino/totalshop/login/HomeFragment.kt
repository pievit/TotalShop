package it.torino.totalshop.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import it.torino.totalshop.R

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
            Navigation.createNavigateOnClickListener(R.id.next_action, arg)
        }

        view.findViewById<Button>(R.id.loginvenditorebtn)?.setOnClickListener {
            arg.putInt("UserType", 1)
            Navigation.createNavigateOnClickListener(R.id.next_action, arg)
        }

    }

}