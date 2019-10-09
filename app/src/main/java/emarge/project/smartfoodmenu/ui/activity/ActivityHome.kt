package emarge.project.smartfoodmenu.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import emarge.project.smartfoodmenu.R
import kotlinx.android.synthetic.main.activity_home.*

class ActivityHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        setSupportActionBar(toolbar_home)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_home, toolbar_home, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_home.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_home.setNavigationItemSelectedListener(this)


        button6.setOnClickListener {
            val intenttop = Intent(this, ActivityMenu::class.java)
            startActivity(intenttop)
           this.finish()
        }


        button4.setOnClickListener {
            val intenttop = Intent(this, ActivityOutlet::class.java)
            startActivity(intenttop)
            this.finish()
        }

    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.nav_menu -> {
                val intenttop = Intent(this, ActivityMenu::class.java)
                startActivity(intenttop)
                this.finish()
            }
            R.id.nav_outlet -> {
                val intenttop = Intent(this, ActivityOutlet::class.java)
                startActivity(intenttop)
                this.finish()
            }
            R.id.nav_sales -> {
                val intenttop = Intent(this, ActivitySale::class.java)
                startActivity(intenttop)
                this.finish()
            }

        }

        drawer_layout_home.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onBackPressed() {
        if (drawer_layout_home.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_home.closeDrawer(GravityCompat.START)
        } else {

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Exit!")
            alertDialogBuilder.setMessage("Do you really want to exit ?")
            alertDialogBuilder.setPositiveButton("YES"
            ) { _, _ -> super.onBackPressed() }
            alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
            alertDialogBuilder.show()
        }
    }
}
