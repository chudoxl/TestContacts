package com.github.chudoxl.testcontacts.home

import android.content.ContentProviderOperation
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.github.chudoxl.testcontacts.moxy.BaseMvpPresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject



@StateStrategyType(AddToEndSingleStrategy::class)
interface IHomeView : MvpView {
    fun showLoading(loading: Boolean)
    fun showError(errMsg: String)
    fun showOpDone(msg: String)
}

@InjectViewState
class HomePresenter : BaseMvpPresenter<IHomeView>() {

    @Inject
    lateinit var appContext: Context

    private val prefix = "+7"
    private val phoneLength = 10
    private val amount = 1000

    fun onCreateClick() {
        viewState.showLoading(true)
        val d = createContacts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableCompletableObserver(){
                override fun onComplete() {
                    viewState.showLoading(false)
                    viewState.showOpDone("Creating contacts done")
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                    viewState.showLoading(false)
                    viewState.showError(e.message ?: "Error creating contacts")
                }
            })
        disposeOnDestroy(d)
    }

    fun onRemoveClick() {
        viewState.showLoading(true)
        val d = removeContacts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableCompletableObserver(){
                override fun onComplete() {
                    viewState.showLoading(false)
                    viewState.showOpDone("Deleting contacts done")
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                    viewState.showLoading(false)
                    viewState.showError(e.message ?: "Error deleting contacts")
                }
            })
        disposeOnDestroy(d)
    }

    private fun createContacts(): Completable {
        return Completable.fromRunnable {
            val namePadding = amount.toString().length

            val ops = ArrayList<ContentProviderOperation>()
            for (i in 1..amount){
                val num= i.toString().padStart(phoneLength, '0')
                val nameNum = i.toString().padStart(namePadding, '0')
                createContact(ops, "test $nameNum" , "$prefix$num")

                if (ops.size > 450){  //500 points limit
                    appContext.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                    ops.clear()
                }
            }

            if (ops.isNotEmpty()) {
                appContext.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            }
        }
    }

    private fun removeContacts(): Completable {
        return Completable.fromRunnable {

            val ops = ArrayList<ContentProviderOperation>()
            for (i in 1..amount){
                val num= i.toString().padStart(phoneLength, '0')
                removeContactByPhone(/*ops,*/"$prefix$num")
            }
            appContext.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        }
    }

    private fun createContact(ops:ArrayList<ContentProviderOperation>, displayName: String, mobileNumber: String, homeNumber: String? = null, workNumber: String? = null,
                              emailID: String? = null, company: String? = null, jobTitle: String? = null) {

        val backRef = ops.size

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())

        //Names
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build())

        //Mobile Number
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build())

        //Home Numbers
        if (!homeNumber.isNullOrEmpty()) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, homeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build())
        }

        //Work Numbers
        if (!workNumber.isNullOrEmpty()) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build())
        }

        //Email
        if (!emailID.isNullOrEmpty()) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build())
        }

        //Organization
        if (!company.isNullOrEmpty() && !jobTitle.isNullOrEmpty()) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRef)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build())
        }
    }

//    private fun removeContactByPhone(ops:ArrayList<ContentProviderOperation>, phone: String){
//        ops.add(
//            ContentProviderOperation.newDelete(ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//                .withSelection(ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", arrayOf(phone))
//                .build()
//        )
//    }

    private fun removeContactByPhone(phone: String) {

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
        val selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?"

        val uris = ArrayList<Uri>()

        appContext.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, arrayOf(phone), null).use {
            if (it == null)
                return

            while (it.moveToNext()) {
                val lookupKey = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY))
                val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
                uris.add(uri)
            }
        }

        for(uri in uris)
            appContext.contentResolver.delete(uri,null, null)

    }
}