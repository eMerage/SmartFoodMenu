package emarge.project.smartfoodmenu.ui.adaptor;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import emarge.project.smartfoodmenu.R;
import emarge.project.smartfoodmenu.model.SalesList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {


    ArrayList<SalesList> salesListItems;


    public SalesAdapter(ArrayList<SalesList> item) {
        this.salesListItems = item;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_outletsales, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        SalesList salesList = salesListItems.get(position);


        holder.textViewOutlet.setText(salesList.getName());
        holder.textViewTown.setText(salesList.getCity());
        holder.textViewSales.setText(String.valueOf(salesList.getOutletSale()));
        holder.textViewQty.setText(String.valueOf(salesList.getOutletSaleQty()));
        holder.textViewCommission.setText(salesList.getOutletSaleCommission());


    }

    @Override
    public int getItemCount() {
        return salesListItems.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOutlet, textViewTown, textViewSales, textViewQty, textViewCommission;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewOutlet = (TextView) itemView.findViewById(R.id.textView_outlet);
            textViewTown = (TextView) itemView.findViewById(R.id.textView_town);
            textViewSales = (TextView) itemView.findViewById(R.id.textView_sale);

            textViewQty = (TextView) itemView.findViewById(R.id.textView_qty);
            textViewCommission = (TextView) itemView.findViewById(R.id.textView_commission);


        }

    }


}
