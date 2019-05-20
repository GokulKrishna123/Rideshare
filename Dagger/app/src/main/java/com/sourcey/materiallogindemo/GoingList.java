package com.sourcey.materiallogindemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GoingList extends ArrayAdapter<GoingModel> {
private Activity context;

        List<GoingModel> artists;

public GoingList(Activity context, List<GoingModel> artists) {
        super(context, R.layout.layout_join_list, artists);
        this.context = context;
        this.artists = artists;
        }


@Override
public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_join_list, null, true);

        TextView Email = (TextView) listViewItem.findViewById(R.id.name);
        TextView froms = (TextView) listViewItem.findViewById(R.id.from);
        TextView tos = (TextView) listViewItem.findViewById(R.id.to);
        TextView vias = (TextView) listViewItem.findViewById(R.id.via);
        TextView types  = (TextView) listViewItem.findViewById(R.id.type);
        TextView number  = (TextView) listViewItem.findViewById(R.id.number);
        TextView time  = (TextView) listViewItem.findViewById(R.id.time);


        GoingModel artist = artists.get(position);
        Email.setText(artist.getPersonName());
        froms.setText("Going From : "+artist.getPersonFrom());
        tos.setText("Going To :  "+artist.getPersonTo());
        vias.setText("Via :  "+artist.getPersonVia());
        types.setText("Type Of Person : "+artist.getPersonType());
        number.setText("Phone :  "+artist.getPhone());
        time.setText("Time :  "+artist.getPersonTime());

        return listViewItem;
        }
        }