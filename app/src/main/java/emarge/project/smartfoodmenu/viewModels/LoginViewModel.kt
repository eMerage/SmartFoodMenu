package emarge.project.smartfoodmenu.viewModels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import emarge.project.smartfoodmenu.db.UserTB
import emarge.project.smartfoodmenu.model.UserModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm


class LoginViewModel : ViewModel() {

    val editTextPassword = MutableLiveData<String>()
    var showProgressbar = MutableLiveData<Boolean>()

    val loginError = MutableLiveData<String>()
    val loginScucces = MutableLiveData<Boolean>()

    var cm: ConnectivityManager? = null
    var contx: Application? = null
    lateinit var realm: Realm
    var apiInterface: APIInterface? = null


    fun setViewListener(con : Application) {
        contx = con
        cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        realm = Realm.getDefaultInstance()
        apiInterface = ApiClient.client()
    }

    fun onLoginClick() {
        val activeNetwork: NetworkInfo? = cm!!.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        when {
            !isConnected -> loginError.value = "No internet connection !"
            editTextPassword.value.isNullOrEmpty() -> loginError.value = "Password empty!"
            else -> {
                loginWithServer()
            }
        }


    }


    fun loginWithServer() {
        showProgressbar.value = true

        apiInterface!!.validateRep(editTextPassword.value.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UserModel> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(log: UserModel) {
                    if(!log.isActive){
                        loginError.value = "Login Fail, Please try again !"
                    }else{
                        saveUser(log.id,log.userName)
                        loginScucces.value = true
                    }

                }

                override fun onError(e: Throwable) {
                    loginError.value = "Login Fail, Please try again !$e"
                    showProgressbar.value = false
                }

                override fun onComplete() {
                    showProgressbar.value = false
                }
            })
    }

    private fun saveUser(id : Int,name : String) {

        val userdb = realm.where(UserTB::class.java).count()

        when {
            userdb.toInt() == 0 -> realm.executeTransaction(Realm.Transaction { bgRealm ->
                val userTB = bgRealm.createObject(UserTB::class.java, 1)
                userTB.userID = id
                userTB.userName = name
            })
            else -> {

            }
        }


    }

    fun isUserLog():Boolean{
        val user = realm.where(UserTB::class.java).count()
       return user.toInt()!= 0
    }

}