package com.example.wlt_reg3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class C_List_Mov_Adapter extends ArrayAdapter {
    public Context context;
    public List<C_Moviment> listMovs;

    public C_List_Mov_Adapter(Context context, int resource, ArrayList<C_Moviment> list) {
        super(context, resource, list);
        this.context = context;
        this.listMovs = new ArrayList<C_Moviment>(list);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        C_Moviment mov = listMovs.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.item_mov_list, null);

        TextView id_mov_list_desc = (TextView) item.findViewById(R.id.id_mov_list_desc);
        TextView id_mov_list_imp = (TextView) item.findViewById(R.id.id_mov_list_imp);
        TextView id_mov_list_date = (TextView) item.findViewById(R.id.id_mov_list_date);
        TextView id_mov_list_saldo_post = (TextView) item.findViewById(R.id.id_mov_list_saldo_post);
        id_mov_list_desc.setText(listMovs.get(position).desc_mov);
        id_mov_list_imp.setText(listMovs.get(position).signe + listMovs.get(position).import_editat + " €");
        id_mov_list_date.setText(listMovs.get(position).data_editada);
        id_mov_list_saldo_post.setText("(" + listMovs.get(position).saldo_post_editat + " €)");

        if (listMovs.get(position).signe == "-") {
            id_mov_list_imp.setTextColor(ContextCompat.getColor(context, R.color.colorImpNeg));
        } else {
            id_mov_list_imp.setTextColor(ContextCompat.getColor(context, R.color.colorImpPos));
        }
        return item;
    }

    public long getItemId(int position) {
        return listMovs.get(position).id_mov;
    }

}
