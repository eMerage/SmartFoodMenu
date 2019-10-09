package emarge.project.smartfoodmenu.ui.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import emarge.project.smartfoodmenu.R;
import emarge.project.smartfoodmenu.model.UserModel;

public class RepAdapter extends ArrayAdapter<UserModel> {

    Context context;
    int resource, textViewResourceId;
    List<UserModel> items, tempItems, suggestions;

    public RepAdapter(Context context, int resource, int textViewResourceId, List<UserModel> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<UserModel>(items); // this makes the difference.
        suggestions = new ArrayList<UserModel>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, parent, false);
        }
        UserModel people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getUserName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((UserModel) resultValue).getUserName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (UserModel people : tempItems) {
                    if (people.getUserName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<UserModel> filterList = (ArrayList<UserModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (UserModel people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
