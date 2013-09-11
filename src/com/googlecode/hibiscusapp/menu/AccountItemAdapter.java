package com.googlecode.hibiscusapp.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.hibiscusapp.R;

public class AccountItemAdapter extends ArrayAdapter<AccountItem>
{
    public AccountItemAdapter(Context context)
    {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.account_item, null);
        }

        AccountItem item = getItem(position);

        ImageView icon = (ImageView)convertView.findViewById(R.id.account_item_icon);
        icon.setImageResource(item.getIconRes());

        TextView title = (TextView)convertView.findViewById(R.id.account_item_title);
        title.setText("Konto " + item.getAccount().getAccountNumber());

        return convertView;
    }
}