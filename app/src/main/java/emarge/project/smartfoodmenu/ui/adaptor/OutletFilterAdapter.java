package emarge.project.smartfoodmenu.ui.adaptor;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


import emarge.project.smartfoodmenu.R;
import emarge.project.smartfoodmenu.model.Outlet;
import emarge.project.smartfoodmenu.model.SalesList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class OutletFilterAdapter extends RecyclerView.Adapter<OutletFilterAdapter.MyViewHolder> {


    ArrayList<SalesList> outletItems;
    private ClickListener clickListener;
    Context mContext;

    public OutletFilterAdapter(ArrayList<SalesList> item, ClickListener clic,Context mContext) {
        this.outletItems = item;
        this.mContext = mContext;
        clickListener = clic;
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_filter_outlets, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        SalesList outlet = outletItems.get(position);
        if(outletItems.isEmpty()){
        }else {
            if (outlet.isSelect()) {
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorloghtwhite));
            } else {
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            }


            holder.textOutlet.setText(outlet.getName());
            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(outlet.isSelect()){
                        outletItems.get(position).setSelect(false);
                    }else {
                        outletItems.get(position).setSelect(true);
                    }
                    notifyDataSetChanged();
                    clickListener.onOutletItemClick(v, outletItems.get(position));
                }
            });

        }



    }

    @Override
    public int getItemCount() {
        return outletItems.size();
    }

    public interface ClickListener {
        void onOutletItemClick(View v, SalesList position);

    }


    class MyViewHolder extends RecyclerView.ViewHolder{


       RelativeLayout mainLayout;
       TextView textOutlet;
       CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainLayout= (RelativeLayout) itemView.findViewById(R.id.relativeLayout_header_main);
            textOutlet= (TextView) itemView.findViewById(R.id.textview_outlet);
            cardView= (CardView) itemView.findViewById(R.id.card_view);


        }

    }



}
