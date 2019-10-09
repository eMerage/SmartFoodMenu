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
import emarge.project.smartfoodmenu.model.MenuTitles;

public class MenuTitlesAdapter extends ArrayAdapter<MenuTitles> {

    Context context;
    int resource, textViewResourceId;
    List<MenuTitles> items, tempItems, suggestions;

    public MenuTitlesAdapter(Context context, int resource, int textViewResourceId, List<MenuTitles> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<MenuTitles>(items); // this makes the difference.
        suggestions = new ArrayList<MenuTitles>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, parent, false);
        }
        MenuTitles people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getName());
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
            String str = ((MenuTitles) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (MenuTitles people : tempItems) {
                    if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            List<MenuTitles> filterList = (ArrayList<MenuTitles>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (MenuTitles people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
