package com.seucpss.contact_detection.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.seucpss.contact_detection.Bean.Position;
import com.seucpss.contact_detection.DBOpenHelper;
import com.seucpss.contact_detection.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Level2Adapter extends ArrayAdapter {
    private final int resourceId;
    private Context context;
    private DBOpenHelper dbOpenHelper;
    private List<Position> p;

    public Level2Adapter(@NonNull Context context, int resourceId, List<Position> object, DBOpenHelper db) {
        super(context, resourceId, object);
        this.resourceId = resourceId;
        this.context = context;
        this.dbOpenHelper = db;
        this.p = object;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Position position1 = (Position) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView pname = (TextView) view.findViewById(R.id.pname);
        TextView time = view.findViewById(R.id.time);
        Button button = (Button) view.findViewById(R.id.delbtn);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd/ HH:mm");
        String start = sDateFormat.format(new Date(position1.getStarttime() + 0));
        String end = sDateFormat.format(new Date(position1.getEndtime() + 0));
        String t = start + "到" + end;
        pname.setText(position1.getPname());
        time.setText(t);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dbOpenHelper.delete(position1.getId());
                Iterator<Position> iterator = p.iterator();
                while (iterator.hasNext()) {
                    Position value = iterator.next();
                    if (value.getId() == position1.getId()) {
                        iterator.remove();
                    }
                }
                notifyDataSetChanged();

            }
        });
        return view;
    }
}