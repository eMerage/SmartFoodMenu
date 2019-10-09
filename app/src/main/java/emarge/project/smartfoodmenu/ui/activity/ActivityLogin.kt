package emarge.project.smartfoodmenu.ui.activity

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.databinding.ActivityLoginBinding
import emarge.project.smartfoodmenu.viewModels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class ActivityLogin : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        binding.login = ViewModelProviders.of(this).get(LoginViewModel::class.java)
            binding.login?.setViewListener(application)

        if(binding.login?.isUserLog()!!){
            val intenttop = Intent(this, ActivityHome::class.java)
            startActivity(intenttop)
            super.onBackPressed()
        }else{

        }

        binding.login!!.showProgressbar.observe(this, Observer<Boolean> {
            it?.let { result ->
                if(result){
                    progressBar.visibility= View.VISIBLE
                    button.isEnabled= false
                }else{
                    progressBar.visibility= View.GONE
                    button.isEnabled= true

                }
            }
        })

        binding.login!!.loginScucces.observe(this, Observer<Boolean> {
            it?.let {
                val intenttop = Intent(this, ActivityHome::class.java)
                startActivity(intenttop)
                super.onBackPressed()

            }
        })


        binding.login!!.loginError.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })

    }



    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Exit!")
        alertDialogBuilder.setMessage("Do you really want to exit ?")
        alertDialogBuilder.setPositiveButton("YES"
        ) { dialog, which -> super.onBackPressed() }
        alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i -> return@OnClickListener })
        alertDialogBuilder.show()

    }

}
