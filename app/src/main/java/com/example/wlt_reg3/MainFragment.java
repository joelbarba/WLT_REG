package com.example.wlt_reg3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import static com.example.wlt_reg3.R.color.colorImpNeg;

public class MainFragment extends Fragment {

//    private DBManager DB_WR = null;
    private String sign = "-";
    private WRInterface ac;
    private View vm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reference to main activity
        this.ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        this.vm = view;

        final EditText edit_new_input = (EditText) view.findViewById(R.id.edit_new_input);
        final TextView signText = (TextView) view.findViewById(R.id.signText);
        final Button button_sign = (Button) view.findViewById(R.id.button_sign);
        final Button button_list = (Button) view.findViewById(R.id.button_list);
        final Button button_add = (Button) view.findViewById(R.id.button_add);
        final Button button_op1 = (Button) view.findViewById(R.id.button_op1);
        final Button button_op2 = (Button) view.findViewById(R.id.button_op2);
        final Button button_op6 = (Button) view.findViewById(R.id.button_op6);
        final Button button_op3 = (Button) view.findViewById(R.id.button_op3);
        final Button button_op4 = (Button) view.findViewById(R.id.button_op4);
        final Button button_op5 = (Button) view.findViewById(R.id.button_op5);

        this.showBalance();

        edit_new_input.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) button_add.callOnClick();
                return false;
            }
        });


        button_op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp("6", "-", "Pint");
            }
        });
        button_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp("2,9", "-", "Latte");
            }
        });
        button_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createNewOp("5,65", "-", "Bagel");
            }
        });
        button_op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createNewOp("2,15", "-", "Espresso");
            }
        });
        button_op5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp("8,5", "-", "Sushi");
            }
        });
        button_op6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createNewOp("40", "+", "Cash AIB");
            }
        });


        // LIST button
        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_Main_to_List);
            }
        });

        // ADD button
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewOp(edit_new_input.getText().toString(), sign, "");
            }
        });

        // SIGN button
        button_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign.equals("-")) {
                    sign = "+";
                    edit_new_input.setTextColor(Color.rgb(00, 187, 2));
                    signText.setTextColor(Color.rgb(00, 187, 2));
                } else {
                    sign = "-";
                    edit_new_input.setTextColor(Color.RED);
                    signText.setTextColor(Color.RED);
                }
                button_sign.setText(sign);
            }
        });

    }

    // Show current balance
    private void showBalance() {
        TextView etq_saldo = (TextView) this.vm.findViewById(R.id.label_saldo);
        etq_saldo.setText(String.format("€ %s", ac.formatImport(ac.getBalance())));
    }

    // Add new movement
    private void createNewOp(String str_import, String sign_import, String descripcio) {
        double balance = ac.insertNewMov(str_import, sign_import, descripcio);
        double num_import = Double.parseDouble(str_import.replace(",", "."));
        showBalance();

        if (num_import != 0) {
            if (sign_import.equals("+")) {
                ac.growl("Cash in: € " + ac.formatImport(num_import));
            } else {
                ac.growl("New payment: € " + ac.formatImport(num_import) + " - " + descripcio);
            }

            // Clear input
            final EditText edit_new_input = (EditText) vm.findViewById(R.id.edit_new_input);
            edit_new_input.setText("");

//            saltar_ult_mov();   // Enllaçar pantalla detall moviment
//            NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_Main_to_List);
        } else {
            EditText edit_new_input = (EditText) this.vm.findViewById(R.id.edit_new_input);
            edit_new_input.requestFocus();
        }
    }
}
