package com.github.chudoxl.testcontacts.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chudoxl.testcontacts.R
import com.github.chudoxl.testcontacts.moxy.MvpAppCompatFragment

class ContactsFragment : MvpAppCompatFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }
}

