package com.neura.sfmc.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.neura.sfmc.R
import com.neura.sfmc.ui.fragments.AuthenticateFragment
import com.neura.sfmc.ui.fragments.ConnectedFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        loadFragment()
    }

    /**
     * Load the Fragment according to Neura login state
     */
    fun loadFragment(){
        val isLoggedIn = getNeuraManager().isLoggedIn()
        if(isLoggedIn){
            replaceFragment(ConnectedFragment.newInstance())
        } else{
            replaceFragment(AuthenticateFragment.newInstance())
        }
    }

    private fun replaceFragment(frag : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
    }
}
