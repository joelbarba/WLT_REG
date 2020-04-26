package com.example.wlt_reg3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.text.DecimalFormat;

import static com.example.wlt_reg3.R.color.colorImpNeg;

public class MainFragment extends Fragment {
    private String sign = "-";
    private WRInterface ac;
    private View vm;
    public C_Common_Item[] commons;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)  {
        super.onViewCreated(view, savedInstanceState);

        // Reference to main activity
        this.ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        this.vm = view;



        final EditText edit_new_input = (EditText) view.findViewById(R.id.edit_new_input);
        final TextView signText = (TextView) view.findViewById(R.id.signText);
        final Button button_sign = (Button) view.findViewById(R.id.button_sign);
        final Button button_list = (Button) view.findViewById(R.id.button_list);
        final Button button_list2 = (Button) view.findViewById(R.id.button_list2);
        final Button button_add = (Button) view.findViewById(R.id.button_add);
        final Button button_op1 = (Button) view.findViewById(R.id.button_op1);
        final Button button_op2 = (Button) view.findViewById(R.id.button_op2);
        final Button button_op6 = (Button) view.findViewById(R.id.button_op6);
        final Button button_op3 = (Button) view.findViewById(R.id.button_op3);
        final Button button_op4 = (Button) view.findViewById(R.id.button_op4);
        final Button button_op5 = (Button) view.findViewById(R.id.button_op5);

        this.commons = ac.getCommons();
        button_op1.setText(this.commons[0].desc);
        button_op2.setText(this.commons[1].desc);
        button_op3.setText(this.commons[2].desc);
        button_op4.setText(this.commons[3].desc);
        button_op5.setText(this.commons[4].desc);
        button_op6.setText(this.commons[5].desc);

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
                createNewOp(ac.formatImport(commons[0].import_mov), commons[0].signe, commons[0].desc, commons[0].custom_time);
            }
        });
        button_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(ac.formatImport(commons[1].import_mov), commons[1].signe, commons[1].desc, commons[1].custom_time);
            }
        });
        button_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(ac.formatImport(commons[2].import_mov), commons[2].signe, commons[2].desc, commons[2].custom_time);
            }
        });
        button_op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(ac.formatImport(commons[3].import_mov), commons[3].signe, commons[3].desc, commons[3].custom_time);
            }
        });
        button_op5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(ac.formatImport(commons[4].import_mov), commons[4].signe, commons[4].desc, commons[4].custom_time);
            }
        });
        button_op6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(ac.formatImport(commons[5].import_mov), commons[5].signe, commons[5].desc, commons[5].custom_time);
            }
        });


        // LIST button
        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_Main_to_List);
            }
        });
        button_list2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_Main_to_List);
            }
        });

        // ADD button
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewOp(edit_new_input.getText().toString(), sign, "", (long) -1);
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
    private void createNewOp(String str_import, String sign_import, String descripcio, Long time) {
        EditText edit_new_input = (EditText) this.vm.findViewById(R.id.edit_new_input);
        if (str_import.equals("")) {
            edit_new_input.requestFocus();
            return;
        }
        double balance = ac.insertNewMov(str_import.replace("-", ""), sign_import, descripcio, time);
        double num_import = Double.parseDouble(str_import.replace(",", "."));
        showBalance();

        if (num_import != 0) {
            if (sign_import.equals("+")) {
                ac.growl("Cash in: € " + ac.formatImport(num_import));
            } else {
                ac.growl("New payment: € " + ac.formatImport(num_import) + " - " + descripcio);
            }

            // Clear input
            edit_new_input.setText("");
            NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_Main_to_Details);
        } else {
            edit_new_input.requestFocus();
        }
    }
}
