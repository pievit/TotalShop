package it.torino.totalshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs

class RegisterFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register, container, false)

        val safeArgs: RegisterFragmentArgs by navArgs()

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

        view.findViewById<View>(R.id.regbtn)?.setOnClickListener {
            register()
        }
    }

    fun register(){
        //TODO
    }

}