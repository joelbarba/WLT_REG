package com.example.wlt_reg3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment {
    private WRInterface ac;
    private View vm;

    public int id_ord_offset = 0;
    public int id_next_offset = 0;
    public int id_prev_offset = 0;
    public int window_count = 100;

    public C_List_Mov_Adapter adp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reference to main activity
        this.ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        this.vm = view;

        loadPage();

        ListView listView = (ListView) vm.findViewById(R.id.llista_moviments_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Link to mov detail fragment
                ac.setSelMovId((int) id);
                NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_List_to_Detail);
//                Intent i = new Intent(context, DetailMovActivity.class);
//                Bundle b = new Bundle();
//                b.putString("ID_ULT_MOV", String.valueOf(id));
//                i.putExtras(b);
//                startActivity(i);
            }
        });

        // BUTTON: Next page
        vm.findViewById(R.id.id_button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePage(id_next_offset);
            }
        });

        // BUTTON: Prev page
        vm.findViewById(R.id.id_button_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePage(id_prev_offset);
            }
        });

        // BUTTON: Goto Main
        vm.findViewById(R.id.id_button_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_List_to_Main);
            }
        });
    }



    private void loadPage() {
        ArrayList<C_Moviment> listMovs = new ArrayList<C_Moviment>();
        adp = new C_List_Mov_Adapter(getActivity(), 0, listMovs);

        ListView listView = (ListView) vm.findViewById(R.id.llista_moviments_view);
        listView.setAdapter(adp);

        Cursor F_cursor = this.ac.getMovs(id_ord_offset, window_count);
        if (F_cursor.moveToFirst()) {
            do {
                C_Moviment mov = new C_Moviment();

                mov.id_mov          = F_cursor.getInt(0);;
                mov.import_mov      = F_cursor.getDouble(1);
                mov.desc_mov        = F_cursor.getString(2);

                mov.data_editada    = F_cursor.getString(3);
                mov.geopos_mov      = F_cursor.getString(4);
                mov.saldo_post      = F_cursor.getDouble(5);
                mov.id_ordre        = F_cursor.getInt(6);

                mov.import_editat       = ac.formatImport(mov.import_mov).replace("-", "");
                mov.saldo_post_editat   = ac.formatImport(mov.saldo_post);
                if (mov.import_mov < 0) { mov.signe = "-"; }

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    mov.data_mov = format.parse(mov.data_editada);
                } catch (ParseException e) {
                    mov.data_mov = null;
                }

                listMovs.add(mov);
            } while(F_cursor.moveToNext());
        }

        F_cursor.close();
        adp.listMovs = new ArrayList<C_Moviment>(listMovs);

        // Update pagination buttons
        Button id_button_next = (Button) vm.findViewById(R.id.id_button_next);
        Button id_button_prev = (Button) vm.findViewById(R.id.id_button_prev);
        TextView id_titol_llista_movs = (TextView) vm.findViewById(R.id.id_titol_llista_movs);
        id_ord_offset = listMovs.get(0).id_ordre;
        id_next_offset = ac.getNextOffset(id_ord_offset, window_count);
        id_prev_offset = ac.getPrevOffset(id_ord_offset, window_count);

        if ((id_next_offset == -1) && (id_prev_offset == -1)) {
            id_button_next.setVisibility(View.INVISIBLE);
            id_button_prev.setVisibility(View.INVISIBLE);

        } else {
            id_button_next.setVisibility(View.VISIBLE);
            id_button_prev.setVisibility(View.VISIBLE);

            id_button_next.setEnabled((id_next_offset != -1));
            id_button_prev.setEnabled((id_prev_offset != -1));

            int[] info_pag = ac.getPagInfo(id_ord_offset, window_count);
            id_titol_llista_movs.setText(String.valueOf(info_pag[0]) + "/" + String.valueOf(info_pag[1]));

        }

//        ac.growl("id_ord_offset = " + id_ord_offset + ", id_next_offset = " + id_next_offset + ", id_prev_offset = " + id_prev_offset);
    }

    private void changePage(int new_offset) {
        this.id_ord_offset = new_offset;
        loadPage();

        // Redraw the view
//        adp.notifyDataSetChanged();
//        ListView listView = (ListView) vm.findViewById(R.id.llista_moviments_view);
//        listView.invalidateViews();
//        listView.refreshDrawableState();
    }

}
