package com.example.wlt_reg3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.wlt_reg3.R.color.colorImpNeg;


public class DetailFragment extends Fragment {
    private WRInterface ac;
    private View vm;
    private C_Moviment mov = new C_Moviment();
    Calendar mov_date;
    DecimalFormat twoDForm = new DecimalFormat("00");
    DecimalFormat fourDForm = new DecimalFormat("0000");


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Reference to main activity
        ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        vm = view;

        final EditText      input_import_mov    = (EditText)     vm.findViewById(R.id.input_import_mov);
        final ToggleButton  toggle_import_sign  = (ToggleButton) vm.findViewById(R.id.toggle_import_sign);
        final EditText      input_desc_mov      = (EditText)     vm.findViewById(R.id.input_desc_mov);
        Button button_del_mov             = (Button) vm.findViewById(R.id.button_del_mov);
        Button button_mod_mov             = (Button) vm.findViewById(R.id.button_mod_mov);
        Button id_button_year_up          = (Button) vm.findViewById(R.id.id_button_year_up);
        Button id_button_year_down        = (Button) vm.findViewById(R.id.id_button_year_down);
        Button id_button_month_up         = (Button) vm.findViewById(R.id.id_button_month_up);
        Button id_button_month_down       = (Button) vm.findViewById(R.id.id_button_month_down);
        Button id_button_day_up           = (Button) vm.findViewById(R.id.id_button_day_up);
        Button id_button_day_down         = (Button) vm.findViewById(R.id.id_button_day_down);
        Button id_button_hour_up          = (Button) vm.findViewById(R.id.id_button_hour_up);
        Button id_button_hour_down        = (Button) vm.findViewById(R.id.id_button_hour_down);
        Button id_button_min_up           = (Button) vm.findViewById(R.id.id_button_min_up);
        Button id_button_min_down         = (Button) vm.findViewById(R.id.id_button_min_down);

        // Load mov details
        mov = ac.loadMov();

        input_desc_mov.setText(this.mov.desc_mov);

        input_import_mov.setText(this.mov.import_editat);
        if (this.mov.signe.equals("-"))  {
            toggle_import_sign.setChecked(true);
            input_import_mov.setTextColor(getResources().getColor(colorImpNeg));
        } else {
            toggle_import_sign.setChecked(false);
            input_import_mov.setTextColor(getResources().getColor(R.color.colorImpPos));
        }

        mov_date = Calendar.getInstance();
        mov_date.set(Calendar.YEAR, Integer.valueOf(mov.data_editada.substring(0, 4)));
        mov_date.set(Calendar.MONTH, Integer.valueOf(mov.data_editada.substring(5, 7)) - 1);
        mov_date.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mov.data_editada.substring(8, 10)));
        mov_date.set(Calendar.HOUR_OF_DAY,  Integer.valueOf(mov.data_editada.substring(11,13)));
        mov_date.set(Calendar.MINUTE,       Integer.valueOf(mov.data_editada.substring(14,16)));

        actualitzar_datepicker();



        toggle_import_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle_import_sign.isChecked()) {
                    input_import_mov.setTextColor(getResources().getColor(R.color.colorImpNeg));
                } else {
                    input_import_mov.setTextColor(getResources().getColor(R.color.colorImpPos));
                }
            }
        });



        // BTN: Delete Mov
        button_del_mov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(vm.getContext());
                alertDialogBuilder.setTitle("Atenció");

                alertDialogBuilder
                        .setMessage("Estas segur que vols eliminar aquesta operació?")
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ac.delMov(mov); // Delete the mov
                                ac.growl("Deleted: " + mov.desc_mov);
                                NavHostFragment.findNavController(DetailFragment.this).popBackStack();
//                                NavHostFragment.findNavController(DetailFragment.this).navigate(R.id.action_Details_to_List);
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

        // BTN: Update Mov
        button_mod_mov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                C_Moviment mov_tmp = new C_Moviment();
                mov_tmp.id_mov = mov.id_mov;
                mov_tmp.import_editat = input_import_mov.getText().toString();
                mov_tmp.desc_mov = input_desc_mov.getText().toString();

                mov_tmp.data_editada =
                        String.valueOf(fourDForm.format(mov_date.get(Calendar.YEAR))) + "-" +
                                String.valueOf(twoDForm.format(mov_date.get(Calendar.MONTH) + 1)) + "-" +
                                String.valueOf(twoDForm.format(mov_date.get(Calendar.DAY_OF_MONTH))) + " " +
                                String.valueOf(twoDForm.format(mov_date.get(Calendar.HOUR_OF_DAY))) + ":" +
                                String.valueOf(twoDForm.format(mov_date.get(Calendar.MINUTE))) + ":00";

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    mov_tmp.data_mov = format.parse(mov_tmp.data_editada);
                } catch (ParseException e) {
                    mov_tmp.data_mov = null;
                }

                if (toggle_import_sign.isChecked()) {
                    mov_tmp.signe = "-";
                } else {
                    mov_tmp.signe = "";
                }

                ac.saveMov(mov_tmp);
                NavHostFragment.findNavController(DetailFragment.this).navigate(R.id.action_Details_to_List);
//                NavHostFragment.findNavController(DetailFragment.this).popBackStack();
            }
        });


        // Botons del datapicker custom:
        id_button_year_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.YEAR, 1);
                actualitzar_datepicker();
            }
        });
        id_button_year_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.YEAR, -1);
                actualitzar_datepicker();
            }
        });
        id_button_month_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.MONTH, 1);
                actualitzar_datepicker();
            }
        });
        id_button_month_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.MONTH, -1);
                actualitzar_datepicker();
            }
        });
        id_button_day_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.DAY_OF_MONTH, 1);
                actualitzar_datepicker();
            }
        });
        id_button_day_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.DAY_OF_MONTH, -1);
                actualitzar_datepicker();
            }
        });

        id_button_hour_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.HOUR_OF_DAY, 1);
                actualitzar_datepicker();
            }
        });
        id_button_hour_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.HOUR_OF_DAY, -1);
                actualitzar_datepicker();
            }
        });
        id_button_min_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = 0;
                a = mov_date.get(Calendar.MINUTE);
                if (a < 5) { a = 0;
                } else if (a < 10) { a = 5;
                } else if (a < 15) { a = 10;
                } else if (a < 20) { a = 15;
                } else if (a < 25) { a = 20;
                } else if (a < 30) { a = 25;
                } else if (a < 35) { a = 30;
                } else if (a < 40) { a = 35;
                } else if (a < 45) { a = 40;
                } else if (a < 50) { a = 45;
                } else if (a < 55) { a = 50;
                } else { a = 55; }

                mov_date.set(Calendar.MINUTE, a + 5);
//                mov_date.add(Calendar.MINUTE, 5);
                actualitzar_datepicker();
            }
        });
        id_button_min_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.MINUTE, -5);
                actualitzar_datepicker();
            }
        });


        input_desc_mov.requestFocus();    // Focus al input text de la descripció

    }


    private void actualitzar_datepicker() {
        TextView id_label_year          = (TextView) vm.findViewById(R.id.id_label_year);
        TextView id_label_month         = (TextView) vm.findViewById(R.id.id_label_month);
        TextView id_label_day           = (TextView) vm.findViewById(R.id.id_label_day);
        TextView id_label_hour          = (TextView) vm.findViewById(R.id.id_label_hour);
        TextView id_label_min           = (TextView) vm.findViewById(R.id.id_label_min);
        TextView id_label_wday          = (TextView) vm.findViewById(R.id.id_label_wday);

        id_label_year.setText(  String.valueOf(fourDForm.format(mov_date.get(Calendar.YEAR))));
        id_label_month.setText( String.valueOf(twoDForm.format(mov_date.get(Calendar.MONTH) + 1)));
        id_label_day.setText(   String.valueOf(twoDForm.format(mov_date.get(Calendar.DAY_OF_MONTH))));
        id_label_hour.setText(  String.valueOf(twoDForm.format(mov_date.get(Calendar.HOUR_OF_DAY))));
        id_label_min.setText(   String.valueOf(twoDForm.format(mov_date.get(Calendar.MINUTE))));
        int wDay = mov_date.get(Calendar.DAY_OF_WEEK);
//        ac.growl("DAY " + wDay);

        if (wDay == 1) { id_label_wday.setText("Sunday"); }
        else if (wDay == 2) { id_label_wday.setText("Monday"); }
        else if (wDay == 3) { id_label_wday.setText("Tuesday"); }
        else if (wDay == 4) { id_label_wday.setText("Wednesday"); }
        else if (wDay == 5) { id_label_wday.setText("Thursday"); }
        else if (wDay == 6) { id_label_wday.setText("Friday"); }
        else if (wDay == 7) { id_label_wday.setText("Saturday"); }
    }


}

