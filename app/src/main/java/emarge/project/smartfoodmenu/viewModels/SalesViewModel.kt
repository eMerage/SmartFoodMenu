package emarge.project.smartfoodmenu.viewModels


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.MutableLiveData

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import androidx.lifecycle.ViewModel
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.realm.Realm
import emarge.project.smartfoodmenu.db.UserTB
import emarge.project.smartfoodmenu.model.Outlet
import emarge.project.smartfoodmenu.model.Sales
import emarge.project.smartfoodmenu.model.SalesList


class SalesViewModel : ViewModel() {


    var showProgressbar = MutableLiveData<Boolean>()


    var outletListForFilter = MutableLiveData<ArrayList<SalesList>>()

    val gettingSalesDetails = MutableLiveData<String>()
    var salesList = MutableLiveData<Sales>()

    var filterSalesList = MutableLiveData<ArrayList<SalesList>>()


    var list = ArrayList<SalesList>()

    var cm: ConnectivityManager? = null
    var contx: Context? = null
    var apiInterface: APIInterface? = null
    lateinit var realm: Realm

    fun setViewListener(con: Context) {
        contx = con
        cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        realm = Realm.getDefaultInstance()
        apiInterface = ApiClient.client()
        getSales("", "")
    }


    fun salesFilter(req: ArrayList<SalesList>, selectedOutlet: ArrayList<SalesList>) {
        if (selectedOutlet.isNullOrEmpty()) {
        } else {
            var newList: ArrayList<SalesList> = ArrayList<SalesList>()
            newList = req
            newList.retainAll(selectedOutlet)
            filterSalesList.value = newList

        }
    }

    fun salesFilterValidation(req: ArrayList<SalesList>, selectedOutlet: ArrayList<SalesList>, sDate: String, eDate: String) {
        if ((sDate.isNullOrEmpty()).and((eDate.isNullOrEmpty()))) {
            salesFilter(req, selectedOutlet)
        } else {
            val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            if (!isConnected) {
                gettingSalesDetails.value = "No internet connection !"
            } else {
                showProgressbar.value = true
                val user = realm.where(UserTB::class.java!!).findFirst()
                if (user != null) {
                    user.userID?.let {
                        apiInterface!!.getOutletStatsByRepNew(it, sDate, eDate)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<Sales> {
                                    override fun onSubscribe(d: Disposable) {}
                                    override fun onNext(log: Sales) {
                                        salesFilter(log.salesList, selectedOutlet)
                                    }

                                    override fun onError(e: Throwable) {
                                        showProgressbar.value = false
                                        gettingSalesDetails.value = "Something went wrong, Please try again $e"
                                    }

                                    override fun onComplete() {
                                        showProgressbar.value = false

                                    }
                                })
                    }
                } else {
                    gettingSalesDetails.value = "Something went wrong, Please try again"
                }
            }
        }
    }

    fun getSales(sDate: String, eDate: String) {

        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
            gettingSalesDetails.value = "No internet connection !"
        } else {
            showProgressbar.value = true
            val user = realm.where(UserTB::class.java!!).findFirst()

            if (user != null) {
                user.userID?.let {
                    apiInterface!!.getOutletStatsByRepNew(it, sDate, eDate)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<Sales> {
                                override fun onSubscribe(d: Disposable) {}
                                override fun onNext(log: Sales) {
                                    list = log.salesList
                                    salesList.value = log
                                }

                                override fun onError(e: Throwable) {
                                    showProgressbar.value = false
                                    gettingSalesDetails.value = "Something went wrong, Please try again $e"
                                }

                                override fun onComplete() {
                                    showProgressbar.value = false

                                }
                            })
                }
            } else {
                gettingSalesDetails.value = "Something went wrong, Please try again"
            }


        }
    }


    fun addOutletsToFilter() {
        outletListForFilter.value = list
    }


}