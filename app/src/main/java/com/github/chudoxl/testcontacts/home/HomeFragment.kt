package com.github.chudoxl.testcontacts.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.github.chudoxl.testcontacts.R
import com.github.chudoxl.testcontacts.moxy.MvpAppCompatFragment
import com.github.chudoxl.testcontacts.tools.di.injector
import com.github.chudoxl.testcontacts.tools.showToast
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : MvpAppCompatFragment(), IHomeView {

    @InjectPresenter
    lateinit var presenter: HomePresenter

    @ProvidePresenter
    fun providesPresenter(): HomePresenter {
        val p = HomePresenter()
        injector.inject(p)
        return p
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnCreateContacts.setOnClickListener { presenter.onCreateClick() }
        btnRemoveContacts.setOnClickListener { presenter.onRemoveClick() }
    }

    override fun showLoading(loading: Boolean) {
        pgsLoading.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        btnCreateContacts.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        btnRemoveContacts.visibility = if (loading) View.INVISIBLE else View.VISIBLE
    }

    override fun showError(errMsg: String) {
        showToast(errMsg)
    }

    override fun showOpDone(msg: String) {
        showToast(msg)
    }
}

