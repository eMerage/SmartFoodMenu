package emarge.project.smartfoodmenu.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.databinding.ActivityMenuBinding
import emarge.project.smartfoodmenu.databinding.DialogMenuTitleBinding
import emarge.project.smartfoodmenu.viewModels.MenuViewModel
import kotlinx.android.synthetic.main.activity_menu.*
import androidx.lifecycle.ViewModel
import java.security.AccessController.getContext
import emarge.project.smartfoodmenu.model.UserModel
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.*
import android.widget.AdapterView
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide

import emarge.project.smartfoodmenu.model.MenuTitles
import emarge.project.smartfoodmenu.model.Outlet
import emarge.project.smartfoodmenu.model.OutletImages
import emarge.project.smartfoodmenu.ui.adaptor.AddedOutletImagesAdaptor

import emarge.project.smartfoodmenu.ui.adaptor.MenuTitlesAdapter
import emarge.project.smartfoodmenu.ui.adaptor.OutletAdapter
import kotlinx.android.synthetic.main.activity_menu.imageView3
import kotlinx.android.synthetic.main.activity_menu.imageView4
import kotlinx.android.synthetic.main.activity_menu.progressBar
import kotlinx.android.synthetic.main.activity_outlet.*

import java.io.File
import java.io.FileNotFoundException


class ActivityMenu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    val IMAGE_PERMISSION_REQUEST = 902
    val PICK_IMAGE_REQUEST = 901

    val  PICK_IMAGE_REQUEST_MENU_TITLE = 801
    val  IMAGE_PERMISSION_REQUEST_MENU_TITLE = 802
    private lateinit var binding: ActivityMenuBinding


    lateinit var bitmap: Bitmap
    lateinit var filePath: Uri


    lateinit var bitmapMenuTitle: Bitmap
    lateinit var filePathMenuTitle: Uri


    lateinit var imageViewMenuTitle : ImageView


    lateinit var outletAdapter: OutletAdapter
    lateinit var menuTitlesAdapter: MenuTitlesAdapter

    lateinit var dialogNewMenuTitle : Dialog

    var selectedOutletID : Int= 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        binding = DataBindingUtil.setContentView<ActivityMenuBinding>(this, R.layout.activity_menu)
        binding.menu = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        binding.menu!!.setViewListener(applicationContext)



        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)




        binding.menu!!.showProgressbar.observe(this, Observer<Boolean> {
            it?.let { result ->
                if(result){
                    progressBar.visibility= View.VISIBLE
                    imageView4.isEnabled= false
                    imageView5.isEnabled= false
                }else{
                    progressBar.visibility= View.GONE
                    imageView4.isEnabled= true
                    imageView5.isEnabled= true
                }
            }
        })



        binding.menu!!.menuTitleAddingMsg.observe(this, Observer<String> {
            it?.let { result ->

                if(result== "Menu Title adding successfully"){
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                    dialogNewMenuTitle.dismiss()
                    binding.menu!!.getMenuTitles(binding.menu!!.selectedMenuCatID.value!!)
                }else{
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }


            }
        })

        binding.menu!!.menuAddingMsg.observe(this, Observer<String> {
            it?.let { result -> Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })




        binding.menu!!.menuAddingResult.observe(this, Observer<Int> {
            it?.let { result ->
                if(result!=0){
                    val intenttop = Intent(this, ActivityMenuTwo::class.java)
                    intenttop.putExtra("OutletMenuTitleID", result)
                    intenttop.putExtra("SelectedOutletID", selectedOutletID)
                    startActivity(intenttop)
                    super.onBackPressed()
                }else{
                    Toast.makeText(this, "Menu adding Fail,Try again", Toast.LENGTH_LONG).show()
                }

            }
        })




        binding.menu!!.outletsList.observe(this, Observer<ArrayList<Outlet>> {
            it?.let { result ->
                outletAdapter = OutletAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                autoCompleteTextView_outlet.setAdapter(outletAdapter)
            }
        })

        autoCompleteTextView_outlet.onItemClickListener = AdapterView.OnItemClickListener{ parent, _, position, _ ->
            var outlet : Outlet = parent.getItemAtPosition(position) as Outlet
            selectedOutletID = outlet.id
            binding.menu!!.setSelectedOutletID(selectedOutletID)
        }

        binding.menu!!.outletsListgettingMsg.observe(this, Observer<String> {
            it?.let { result ->
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })



        binding.menu!!.menuTitlesList.observe(this, Observer<ArrayList<MenuTitles>> {
            it?.let { result ->
                  menuTitlesAdapter = MenuTitlesAdapter(this, R.layout.activity_outlet, R.id.lbl_name, result)
                  autoCompleteTextView_menu_title.setAdapter(menuTitlesAdapter)
            }
        })

        autoCompleteTextView_menu_title.onItemClickListener = AdapterView.OnItemClickListener{ parent, _, position, _ ->
            var menuTitles : MenuTitles = parent.getItemAtPosition(position) as MenuTitles
            binding.menu!!.setSelectedMenuTitleID(menuTitles.menuTitleID)
        }


        binding.menu!!.menuTitlesListgettingMsg.observe(this, Observer<String> {
            it?.let { result -> Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            }
        })








        imageView55.setOnClickListener {

            val intenttop = Intent(this, ActivityMenuTwo::class.java)
            intenttop.putExtra("OutletMenuTitleID", 5260)
            intenttop.putExtra("SelectedOutletID", 4042)

            startActivity(intenttop)

        }


    }

    fun onClickImageMenuTitleAdd(view: View) {

        dialogNewMenuTitle = Dialog(this)
        dialogNewMenuTitle.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogNewMenuTitle.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogNewMenuTitle.setCancelable(true)

        var bindingDialog: DialogMenuTitleBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_menu_title, null, false)
        dialogNewMenuTitle.setContentView(bindingDialog.root)
        bindingDialog.menutitlet = ViewModelProviders.of(this).get(MenuViewModel::class.java)



        imageViewMenuTitle = dialogNewMenuTitle.findViewById<ImageView>(R.id.imageView332)



        dialogNewMenuTitle.show()

    }

    fun onClickImageUploadMenuTitle(view: View) {

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequestImageForMenuTitle()
        } else {
            chooseFileForMenuTitle()
        }

    }


    fun onClickImageUpload(view: View) {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequestImage()
        } else {
            chooseFile()
        }

    }


    private fun chooseFileForMenuTitle() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST_MENU_TITLE)
    }


    private fun chooseFile() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST)
    }

    private fun makeRequestImage() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IMAGE_PERMISSION_REQUEST)
    }

    private fun makeRequestImageForMenuTitle() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IMAGE_PERMISSION_REQUEST_MENU_TITLE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> try {
                    var filePath: String = ""
                    try {
                        filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                addImagesUpKitKat(data!!.data)
                            } else {
                                addImagesUpKitKat(data!!.data)
                            }
                        } else {
                            addImages(data!!.data)
                        }


                    if (filePath == "") {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    } else {

                        val file = File(filePath)
                        binding.menu!!.setSelectedImageBitmap(file.name)

                    }

                    Glide.with(this)
                        .asBitmap()
                        .load(data!!.data)
                        .into(imageView3)

                    } catch (ex: java.lang.Exception) {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(this, "Image not selected properly, Please try again", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }
            PICK_IMAGE_REQUEST_MENU_TITLE -> when (resultCode) {
                Activity.RESULT_OK -> try {

                    var filePath: String = ""
                    try {
                        filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                addImagesUpKitKat(data!!.data)
                            } else {
                                addImagesUpKitKat(data!!.data)
                            }
                        } else {
                            addImages(data!!.data)
                        }

                    if (filePath == "") {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    } else {

                        val file = File(filePath)
                        binding.menu!!.setSelectedImageBitmapMenuTitle(file.name)

                    }

                    Glide.with(this)
                        .asBitmap()
                        .load(data!!.data)
                        .into(imageViewMenuTitle)

                    } catch (ex: java.lang.Exception) {
                        Toast.makeText(this, "Please select image again", Toast.LENGTH_LONG).show()
                    }


                } catch (e: Exception) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Image not selected properly, Please try again$e")
                    alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    alertDialogBuilder.show()
                }
                Activity.RESULT_CANCELED -> Toast.makeText(this, "Image not selected properly, Please try again", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }

        }

    }






    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun addImagesUpKitKat(data: Uri): String {
        println("cccccccccccccccc : addImagesUpKitKat")

        var filep: String = ""
        if (data == null) {
            Toast.makeText(this, "Please select image from gallery 2", Toast.LENGTH_LONG).show()
        } else {
            filep = getPath(this, data)
        }
        return filep
    }


    private fun addImages(data: Uri?): String {
        println("cccccccccccccccc : addImages")
        var filep: String = ""
        if (data == null) {
            Toast.makeText(this, "Please select image from gallery 3", Toast.LENGTH_LONG).show()
        } else {
            filep = getRealPathFromURI(this, data)
        }
        return filep

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            println("cccccccccccccccc : 1")

            if (isExternalStorageDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/" + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                println("cccccccccccccccc : 2")

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri.path.toString()
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )

                    getDataColumn(context, contentUri, null, null)
                }


            } else if (isMediaDocument(uri)) {
                println("cccccccccccccccc : 3")
                var docId = DocumentsContract.getDocumentId(uri)
                var split =
                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var type = split[0]
                var contentUri: Uri = Uri.EMPTY
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }

        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            if (isGooglePhotosUri(uri))
                return uri.lastPathSegment.toString()
            return this.getDataColumn(context, uri, null, null).toString()


        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            println("cccccccccccccccc 6")
            return uri.path.toString()
        }

        return ""
    }


    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {

        lateinit var cursor: Cursor
        val column = "_data"
        val projection = arrayOf<String>(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)!!
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)!!
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            return if (cursor.getString(column_index) == null) {
                ""
            } else {
                cursor.getString(column_index)
            }

        } finally {
            cursor?.close()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFile()
                } else {
                    Toast.makeText(this@ActivityMenu, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
            }

            IMAGE_PERMISSION_REQUEST_MENU_TITLE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFileForMenuTitle()
                } else {
                    Toast.makeText(this@ActivityMenu, "Oops! Permission Denied!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu -> {
                // Handle the camera action
            }
            R.id.nav_outlet -> {
                val intent = Intent(this@ActivityMenu, ActivityOutlet::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_sales -> {
                val intenttop = Intent(this, ActivitySale::class.java)
                startActivity(intenttop)
                super.onBackPressed()
            }


        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



}

