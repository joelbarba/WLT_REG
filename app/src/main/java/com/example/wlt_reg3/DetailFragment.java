package com.example.wlt_reg3;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class DetailFragment extends Fragment {

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

//        final EditText input_import_mov    = (EditText)        findViewById(R.id.input_import_mov);
//        final ToggleButton toggle_import_sign  = (ToggleButton)    findViewById(R.id.toggle_import_sign);
//        final EditText      input_desc_mov      = (EditText)        findViewById(R.id.input_desc_mov);
////        final DatePicker    mov_det_datePicker  = (DatePicker)      findViewById(R.id.mov_det_datePicker);
////        final TimePicker    mov_det_timePicker  = (TimePicker)      findViewById(R.id.mov_det_timePicker);
//
//        Button button_del_mov           = (Button) findViewById(R.id.button_del_mov);
//        Button button_mod_mov           = (Button) findViewById(R.id.button_mod_mov);
//
//        Button id_button_year_up          = (Button) findViewById(R.id.id_button_year_up);
//        Button id_button_year_down        = (Button) findViewById(R.id.id_button_year_down);
//        Button id_button_month_up         = (Button) findViewById(R.id.id_button_month_up);
//        Button id_button_month_down       = (Button) findViewById(R.id.id_button_month_down);
//        Button id_button_day_up           = (Button) findViewById(R.id.id_button_day_up);
//        Button id_button_day_down         = (Button) findViewById(R.id.id_button_day_down);
//        Button id_button_hour_up          = (Button) findViewById(R.id.id_button_hour_up);
//        Button id_button_hour_down        = (Button) findViewById(R.id.id_button_hour_down);
//        Button id_button_min_up           = (Button) findViewById(R.id.id_button_min_up);
//        Button id_button_min_down         = (Button) findViewById(R.id.id_button_min_down);

        view.findViewById(R.id.button_mod_mov).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WRInterface) getActivity()).setVal(900);

                int currVal = 0; // ((WRInterface) getActivity()).getVal();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "hieeeeeeeeeeeee ---> " + currVal, Toast.LENGTH_SHORT);
                toast.show();
            }
        });


//        int currVal = ((WRInterface) getActivity()).getVal();
//        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "hieeeeeeeeeeeee ---> " + currVal, Toast.LENGTH_SHORT);
//        toast.show();

    }
}

