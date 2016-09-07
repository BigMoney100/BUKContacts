package com.example.bukcontactlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemArrayAdapter extends ArrayAdapter<String[]> implements Filterable {
    private List<String[]> scoreList = new ArrayList<String[]>();
    private List<String[]> filteredList = new ArrayList<String[]>();
    private boolean[] isCheckedConfrim;

    static class ItemViewHolder {
        CheckBox select;
        TextView name;
        TextView tel;
        TextView hospital;
        TextView etc;
    }

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(String[] object) {
        scoreList.add(object);
        filteredList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.filteredList.size();
    }

    @Override
    public String[] getItem(int index) {
        return this.filteredList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_layout, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.select = (CheckBox) row.findViewById(R.id.select);
            viewHolder.name = (TextView) row.findViewById(R.id.name);
            viewHolder.tel = (TextView) row.findViewById(R.id.tel);
            viewHolder.hospital = (TextView) row.findViewById(R.id.hospital);
            viewHolder.etc = (TextView) row.findViewById(R.id.etc);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) row.getTag();
        }

        viewHolder.select.setClickable(false);
        viewHolder.select.setFocusable(false);

        viewHolder.select.setChecked(isCheckedConfrim[position]);

        String[] stat = getItem(position);
        viewHolder.name.setText(stat[0]);
        viewHolder.tel.setText(stat[1]);
        viewHolder.hospital.setText(stat[2]);
        viewHolder.etc.setText(stat[3]);
        return row;
    }

    public void initChecked(int size) {
        this.isCheckedConfrim = new boolean[size];
    }

    public void setChecked(int position) {
        isCheckedConfrim[position] = !isCheckedConfrim[position];
    }

    public boolean getChecked(int position) {
        return isCheckedConfrim[position];
    }

    public void setAllChecked(boolean ischeked) {
        int tempSize = isCheckedConfrim.length;
        for (int a = 0; a < tempSize; a++) {
            isCheckedConfrim[a] = ischeked;
        }
    }

    @Override
    public Filter getFilter() {
        return (myFilter);
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String[]> tempList = new ArrayList<String[]>();
            String filterString = constraint.toString().toLowerCase();

            if (constraint != null && scoreList != null) {
                int length = scoreList.size();
                int i = 0;

                while (i < length) {
                    String[] item = scoreList.get(i);

                    if (item[0].toString().toLowerCase().contains(filterString)) {
                        tempList.add(item);
                    } else if (item[1].toString().toLowerCase().contains(filterString)) {
                        tempList.add(item);
                    } else if (item[2].toString().toLowerCase().contains(filterString)) {
                        tempList.add(item);
                    }

                    i++;
                }

                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<String[]>) filterResults.values;

            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}