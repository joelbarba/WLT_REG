package com.example.wlt_reg3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class CommonsFragment extends Fragment {
    private WRInterface ac;
    private View vm;
    public C_Common_Item[] commons;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commons, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reference to main activity
        this.ac = (WRInterface) getActivity();
        if (ac == null) { return; }
        this.vm = view;

        this.commons = ac.getCommons();

        ((TextView) view.findViewById(R.id.c1_desc_editText)).setText(commons[0].desc);
        ((TextView) view.findViewById(R.id.c2_desc_editText)).setText(commons[1].desc);
        ((TextView) view.findViewById(R.id.c3_desc_editText)).setText(commons[2].desc);
        ((TextView) view.findViewById(R.id.c4_desc_editText)).setText(commons[3].desc);
        ((TextView) view.findViewById(R.id.c5_desc_editText)).setText(commons[4].desc);
        ((TextView) view.findViewById(R.id.c6_desc_editText)).setText(commons[5].desc);

        ((TextView) view.findViewById(R.id.c1_import_editText)).setText(commons[0].import_str);
        ((TextView) view.findViewById(R.id.c2_import_editText)).setText(commons[1].import_str);
        ((TextView) view.findViewById(R.id.c3_import_editText)).setText(commons[2].import_str);
        ((TextView) view.findViewById(R.id.c4_import_editText)).setText(commons[3].import_str);
        ((TextView) view.findViewById(R.id.c5_import_editText)).setText(commons[4].import_str);
        ((TextView) view.findViewById(R.id.c6_import_editText)).setText(commons[5].import_str);

        setBtnSign(0, commons[0].signe);
        setBtnSign(1, commons[1].signe);
        setBtnSign(2, commons[2].signe);
        setBtnSign(3, commons[3].signe);
        setBtnSign(4, commons[4].signe);
        setBtnSign(5, commons[5].signe);

        ((Button) view.findViewById(R.id.c1_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[0].signe.equals("-")) { setBtnSign(0, "+"); } else { setBtnSign(0, "-"); }
            }
        });
        ((Button) view.findViewById(R.id.c2_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[1].signe.equals("-")) { setBtnSign(1, "+"); } else { setBtnSign(1, "-"); }
            }
        });
        ((Button) view.findViewById(R.id.c3_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[2].signe.equals("-")) { setBtnSign(2, "+"); } else { setBtnSign(2, "-"); }
            }
        });
        ((Button) view.findViewById(R.id.c4_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[3].signe.equals("-")) { setBtnSign(3, "+"); } else { setBtnSign(3, "-"); }
            }
        });
        ((Button) view.findViewById(R.id.c5_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[4].signe.equals("-")) { setBtnSign(4, "+"); } else { setBtnSign(4, "-"); }
            }
        });
        ((Button) view.findViewById(R.id.c6_sign_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commons[5].signe.equals("-")) { setBtnSign(5, "+"); } else { setBtnSign(5, "-"); }
            }
        });



        ((Button) view.findViewById(R.id.common_save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[0].desc = ((TextView) vm.findViewById(R.id.c1_desc_editText)).getText().toString();
                commons[1].desc = ((TextView) vm.findViewById(R.id.c2_desc_editText)).getText().toString();
                commons[2].desc = ((TextView) vm.findViewById(R.id.c3_desc_editText)).getText().toString();
                commons[3].desc = ((TextView) vm.findViewById(R.id.c4_desc_editText)).getText().toString();
                commons[4].desc = ((TextView) vm.findViewById(R.id.c5_desc_editText)).getText().toString();
                commons[5].desc = ((TextView) vm.findViewById(R.id.c6_desc_editText)).getText().toString();
                commons[0].import_str = ((TextView) vm.findViewById(R.id.c1_import_editText)).getText().toString();
                commons[1].import_str = ((TextView) vm.findViewById(R.id.c2_import_editText)).getText().toString();
                commons[2].import_str = ((TextView) vm.findViewById(R.id.c3_import_editText)).getText().toString();
                commons[3].import_str = ((TextView) vm.findViewById(R.id.c4_import_editText)).getText().toString();
                commons[4].import_str = ((TextView) vm.findViewById(R.id.c5_import_editText)).getText().toString();
                commons[5].import_str = ((TextView) vm.findViewById(R.id.c6_import_editText)).getText().toString();
                ac.saveCommons(commons);
            }
        });
    }

    private void setBtnSign(int ind, String signe) {
        commons[ind].signe = signe;

        Button btn = vm.findViewById(R.id.c1_sign_button);
        TextView tv = vm.findViewById(R.id.c1_import_editText);
        if (ind == 1) { btn = vm.findViewById(R.id.c2_sign_button); tv = vm.findViewById(R.id.c2_import_editText); }
        if (ind == 2) { btn = vm.findViewById(R.id.c3_sign_button); tv = vm.findViewById(R.id.c3_import_editText); }
        if (ind == 3) { btn = vm.findViewById(R.id.c4_sign_button); tv = vm.findViewById(R.id.c4_import_editText); }
        if (ind == 4) { btn = vm.findViewById(R.id.c5_sign_button); tv = vm.findViewById(R.id.c5_import_editText); }
        if (ind == 5) { btn = vm.findViewById(R.id.c6_sign_button); tv = vm.findViewById(R.id.c6_import_editText); }
        btn.setText(commons[ind].signe);
        if (signe.equals("-")) {
            tv.setTextColor(getResources().getColor(R.color.colorImpNeg));
        } else {
            tv.setTextColor(getResources().getColor(R.color.colorImpPos));
        }
    }

}

