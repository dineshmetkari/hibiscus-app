package com.googlecode.hibiscusapp.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.hibiscusapp.R;

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem>
{
    public DrawerItemAdapter(Context context)
    {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_item, null);
        }

        DrawerItem item = getItem(position);

        ImageView icon = (ImageView)convertView.findViewById(R.id.drawer_item_icon);
        icon.setImageResource(item.getIconRes());

        TextView title = (TextView)convertView.findViewById(R.id.drawer_item_title);
        title.setText(getContext().getString(item.getTitleRes()));

        return convertView;
    }
}