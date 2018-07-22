package com.kedrad.selftherapyball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExercisePlanListAdapter extends BaseAdapter {
    Context context;
    String[] names;

    private static LayoutInflater inflater = null;

    public ExercisePlanListAdapter(Context context, String[] names) {
        this.context = context;
        this.names = names;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.exercise_plan_row, null);

        TextView muscleName = vi.findViewById(R.id.name);
        muscleName.setText(names[position]);

        return vi;
    }
}
