package emarge.project.smartfoodmenu.ui.adaptor;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


import emarge.project.smartfoodmenu.R;
import emarge.project.smartfoodmenu.model.FoodItem;



/**
 * Created by Himanshu on 4/10/2015.
 */
public class FoodItemsAdapter extends RecyclerView.Adapter<FoodItemsAdapter.MyViewHolder> {


    ArrayList<FoodItem> foodItems;
    private ClickListener clickListener;
    Context mContext;

    public FoodItemsAdapter(ArrayList<FoodItem> item, ClickListener clic,Context mtext) {
        this.foodItems = item;
        clickListener = clic;
        mContext = mtext;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu_two_added_food, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        FoodItem foodItem = foodItems.get(position);


        holder.textViewName.setText(foodItem.foodItemtitle);
        holder.textViewPrice.setText(String.valueOf(foodItem.getFoodItemPrice()));
        holder.textViewSize.setText(foodItem.foodItemSize+" "+foodItem.foodItemType);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_center_focus);
        requestOptions.error(R.drawable.ic_center_focus);

        RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) { return false; }
            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) { return false; }
        };


        Glide.with(mContext)
                .asBitmap()
                .load(foodItem.foodItemimage)
                .apply(requestOptions)
                .listener(requestListener)
                .into(holder.imageViewFood);


        holder.imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCancelClick(v,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public interface ClickListener {
        void onCancelClick(View v, int position);

    }


    class MyViewHolder extends RecyclerView.ViewHolder{



        TextView textViewName,textViewPrice,textViewSize;
        ImageView imageViewFood,imageViewCancel;


        public MyViewHolder(View itemView) {
            super(itemView);

            textViewName= (TextView) itemView.findViewById(R.id.textView_name);
            textViewPrice= (TextView) itemView.findViewById(R.id.textView_price);
            textViewSize= (TextView) itemView.findViewById(R.id.textView_size);

            imageViewFood= (ImageView) itemView.findViewById(R.id.imageView6);
            imageViewCancel= (ImageView) itemView.findViewById(R.id.imageView7);


        }

    }



}
