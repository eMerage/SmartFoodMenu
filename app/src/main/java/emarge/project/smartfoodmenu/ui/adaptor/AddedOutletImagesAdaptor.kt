package emarge.project.smartfoodmenu.ui.adaptor

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import emarge.project.smartfoodmenu.R
import emarge.project.smartfoodmenu.model.OutletImages
import kotlinx.android.synthetic.main.listview_outletimages.view.*


class AddedOutletImagesAdaptor(val items: ArrayList<OutletImages>, val context: Context)
    : RecyclerView.Adapter<AddedOutletImagesAdaptor.ViewHolderAddedPayments>() {


    lateinit var mClickListener: ClickListener

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddedPayments {
        return ViewHolderAddedPayments(LayoutInflater.from(context).inflate(R.layout.listview_outletimages, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderAddedPayments, position: Int) {
        var itemPostion = items[position]



        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.noimage)
        requestOptions.error(R.drawable.noimage)

        val requestListener = object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
            override fun onResourceReady(
                resource: Bitmap,
                model: Any,
                target: Target<Bitmap>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }


        Glide.with(context)
            .asBitmap()
            .load(itemPostion.imageUrl)
            .apply(requestOptions)
            .listener(requestListener)
            .into(holder.imageViewCover)

    }


    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(outletImages: OutletImages, aView: View)
    }


    inner class ViewHolderAddedPayments(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val imageViewCover = view.imageview_outlet

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mClickListener.onClick( items[adapterPosition], v!!)
        }



    }
}
