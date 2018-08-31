package com.kedrad.selftherapyball;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class MenuListAdapter extends BaseAdapter{
    Context context;
    String[] names;
    String[] durations;
    String[] images;

    private static LayoutInflater inflater = null;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public MenuListAdapter(Context context, String[] images, String[] names, String[] durations) {
        this.context = context;
        this.images = images;
        this.names = names;
        this.durations = durations;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.menu_row, null);
        AppCompatImageView menuItemImage = vi.findViewById(R.id.list_image);
        TextView bodyPartName = vi.findViewById(R.id.name);
        TextView duration = vi.findViewById(R.id.duration);
        //Log.i(Integer.toString(images[0]), "test");

        //Drawable id obtained using string array
        int drawableId = vi.getResources().getIdentifier(images[position], "drawable", vi.getContext().getPackageName());
        menuItemImage.setImageDrawable(VectorDrawableCompat.create(context.getResources(), drawableId, null));

        bodyPartName.setText(names[position]);
        duration.setText(durations[position]);
        return vi;
    }

}
