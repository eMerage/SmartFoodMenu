package emarge.project.smartfoodmenu.ui.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.material.navigation.NavigationView
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.databinding.ActivitySaleBinding
import emarge.project.smartfoodmenu.databinding.DialogSalesFilterBinding
import emarge.project.smartfoodmenu.model.Outlet
import emarge.project.smartfoodmenu.model.Sales
import emarge.project.smartfoodmenu.model.SalesList
import emarge.project.smartfoodmenu.ui.adaptor.OutletAdapter
import emarge.project.smartfoodmenu.ui.adaptor.OutletFilterAdapter
import emarge.project.smartfoodmenu.ui.adaptor.SalesAdapter
import emarge.project.smartfoodmenu.viewModels.SalesViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_sale.*
import kotlinx.android.synthetic.main.dialog_sales_filter.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivitySale : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OutletFilterAdapter.ClickListener {


    lateinit var salesAdapter: SalesAdapter

    private lateinit var binding: ActivitySaleBinding

    lateinit var dialogFilter: Dialog
    lateinit var outletAdapter: OutletAdapter

    lateinit var outletFilterAdapter: OutletFilterAdapter

    lateinit var req: ArrayList<SalesList>
    var selectedOutlet: ArrayList<SalesList> = ArrayList<SalesList>()

    lateinit var outletList: ArrayList<SalesList>

    lateinit var list: Sales

    var isSalesLoade: Boolean = false

    var isAppLoade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)



        binding = DataBindingUtil.setContentView<ActivitySaleBinding>(this, R.layout.activity_sale)
        binding.sales = ViewModelProviders.of(this).get(SalesViewModel::class.java)
        binding.sales!!.setViewListener(applicationContext)

        setSupportActionBar(toolbar_sale)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_sale, toolbar_sale, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_sale.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_sale.setNavigationItemSelectedListener(this)


        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview_sales!!.layoutManager = layoutManager
        recyclerview_sales!!.itemAnimator = DefaultItemAnimator()





        binding.sales!!.showProgressbar.observe(this, Observer<Boolean> {
            it?.let { result ->
                if (result) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE

                }
            }
        })




        binding.sales!!.outletListForFilter.observe(this, Observer<ArrayList<SalesList>> {
            it?.let { result ->
                outletList = result
            }
        })


        binding.sales!!.gettingSalesDetails.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })


        binding.sales!!.filterSalesList.observe(this, Observer<ArrayList<SalesList>> {
            it?.let { result ->
                if (result.isEmpty()) {
                    salesAdapter = SalesAdapter(null)
                    recyclerview_sales!!.adapter = salesAdapter
                } else {
                    salesAdapter = SalesAdapter(result)
                    recyclerview_sales!!.adapter = salesAdapter
                }
            }
        })






        binding.sales!!.salesList.observe(this, Observer<Sales> {
            it?.let { result ->
                if (result == null) {
                    Toast.makeText(this, "No SalesList Available Yet", Toast.LENGTH_LONG).show()
                    req = ArrayList<SalesList>()
                    salesAdapter = SalesAdapter(req)
                    recyclerview_sales!!.adapter = salesAdapter
                } else {

                    isSalesLoade = true
                    binding.sales!!.addOutletsToFilter()

                    card_view.visibility = View.VISIBLE

                    textView_tot_mo_sales.text = result.monthlyTotalSale
                    textView_tot_mo_qty.text = result.monthlyTotalSaleQty


                    textView_tot_day_sale.text = result.todayTotalSale
                    textView_tot_day_qty.text = result.todayTotalSaleQty

                    textView_tot_commission.text = result.monthlyRepComission
                    textView_day_com.text = result.todayRepComission

                    req = ArrayList<SalesList>()
                    req = result.salesList
                    list = result

                    salesAdapter = SalesAdapter(req)
                    recyclerview_sales!!.adapter = salesAdapter
                }

            }
        })


    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.nav_menu -> {
                val intenttop = Intent(this, ActivityMenu::class.java)
                startActivity(intenttop)
                super.onBackPressed()
            }
            R.id.nav_outlet -> {
                val intenttop = Intent(this, ActivityOutlet::class.java)
                startActivity(intenttop)
                super.onBackPressed()
            }
            R.id.nav_sales -> {

            }


        }

        drawer_layout_sale.closeDrawer(GravityCompat.START)
        return true

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_filter -> {
                if (isSalesLoade) {
                    filterDialog()
                } else {
                }

                return true
            }

        }
        return super.onOptionsItemSelected(item)

    }

    fun filterDialog() {

        dialogFilter = Dialog(this)
        dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogFilter.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogFilter.setCancelable(true)

        var bindingDialog: DialogSalesFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_sales_filter, null, false)
        dialogFilter.setContentView(bindingDialog.root)
        bindingDialog.salesfilter = ViewModelProviders.of(this).get(SalesViewModel::class.java)


        var recyclerViewFilterOutlet: RecyclerView = dialogFilter.findViewById<RecyclerView>(R.id.recyclerView_filter_outlet)
        var calendarView: DateRangeCalendarView = dialogFilter.findViewById<DateRangeCalendarView>(R.id.calendarView)
        var filterBtn: Button = dialogFilter.findViewById<Button>(R.id.button5)


        val layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewFilterOutlet.layoutManager = layoutManager
        recyclerViewFilterOutlet.itemAnimator = DefaultItemAnimator()





        outletFilterAdapter = OutletFilterAdapter(outletList, this, this)
        recyclerViewFilterOutlet.adapter = outletFilterAdapter


        val targetFormat = SimpleDateFormat("MM/dd/yyyy")

        var sDate: String = ""
        var eDate: String = ""

        calendarView.setCalendarListener(object : DateRangeCalendarView.CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {
                sDate = targetFormat.format(startDate.time)
                eDate = targetFormat.format(startDate.time)
            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                sDate = targetFormat.format(startDate.time)
                eDate = targetFormat.format(endDate.time)
            }
        })


        filterBtn.setOnClickListener {
            bindingDialog.salesfilter!!.salesFilterValidation(req, selectedOutlet, sDate, eDate)
            dialogFilter.dismiss()

        }

        dialogFilter.show()


    }

    override fun onOutletItemClick(v: View?, position: SalesList) {
        selectedOutlet.add(position)
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
