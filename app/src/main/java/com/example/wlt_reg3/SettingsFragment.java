package com.example.wlt_reg3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;


public class SettingsFragment extends Fragment {
    private WRInterface ac;
    private View vm;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reference to main activity
        this.ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        this.vm = view;



        // Import DB
        final Button button_import_db = (Button) vm.findViewById(R.id.button_import_db);
        button_import_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(vm.getContext());
                alertDialogBuilder.setTitle("Atenció");
                alertDialogBuilder
                        .setMessage("Are you sure you want to import the whole DB? (from file: " + Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db)" )
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                ac.importDB();
                                ac.growl("New DB ready");
                                NavHostFragment.findNavController(SettingsFragment.this).popBackStack();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();  // create alert dialog
                alertDialog.show();     // show it
            }
        });


        // Export DB
        final Button button_export_db = (Button) vm.findViewById(R.id.button_export_db);
        button_export_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.exportDB();
                ac.exportCSV();
            }
        });


        // Reset DB
        final Button button_reset = (Button) vm.findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(vm.getContext());
                alertDialogBuilder.setTitle("Atenció");
                alertDialogBuilder
                        .setMessage("Estas segur que vols eliminar tot el contingut de la DB?")
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ac.resetDB();
                                NavHostFragment.findNavController(SettingsFragment.this).popBackStack();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();  // create alert dialog
                alertDialog.show(); // show it
            }
        });

    }
}
