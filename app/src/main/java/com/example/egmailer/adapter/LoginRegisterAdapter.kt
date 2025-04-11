package com.example.egmailer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.egmailer.fragment.LoginFragment
import com.example.egmailer.fragment.RegisterFragment

class LoginRegisterAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) LoginFragment() else RegisterFragment()
    }
}