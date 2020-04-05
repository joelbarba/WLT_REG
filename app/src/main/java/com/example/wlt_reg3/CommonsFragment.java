package com.example.wlt_reg3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

        // Set / unset checkboxes depending on time value
        ((CheckBox) view.findViewById(R.id.c1_time_checkBox)).setChecked(commons[0].custom_time >= 0);
        ((CheckBox) view.findViewById(R.id.c2_time_checkBox)).setChecked(commons[1].custom_time >= 0);
        ((CheckBox) view.findViewById(R.id.c3_time_checkBox)).setChecked(commons[2].custom_time >= 0);
        ((CheckBox) view.findViewById(R.id.c4_time_checkBox)).setChecked(commons[3].custom_time >= 0);
        ((CheckBox) view.findViewById(R.id.c5_time_checkBox)).setChecked(commons[4].custom_time >= 0);
        ((CheckBox) view.findViewById(R.id.c6_time_checkBox)).setChecked(commons[5].custom_time >= 0);


        // Format time values
        switchTime(0);
        switchTime(1);
        switchTime(2);
        switchTime(3);
        switchTime(4);
        switchTime(5);

        // Sign button switches
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


        // Save button
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
                NavHostFragment.findNavController(CommonsFragment.this).popBackStack();
            }
        });



        // Turn custom time on / off
        ((CheckBox) view.findViewById(R.id.c1_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[0].custom_time = -1 * commons[0].custom_time;
                switchTime(0);
            }
        });
        ((CheckBox) view.findViewById(R.id.c2_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[1].custom_time = -1 * commons[1].custom_time;
                switchTime(1);
            }
        });
        ((CheckBox) view.findViewById(R.id.c3_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[2].custom_time = -1 * commons[2].custom_time;
                switchTime(2);
            }
        });
        ((CheckBox) view.findViewById(R.id.c4_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[3].custom_time = -1 * commons[3].custom_time;
                switchTime(3);
            }
        });
        ((CheckBox) view.findViewById(R.id.c5_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[4].custom_time = -1 * commons[4].custom_time;
                switchTime(4);
            }
        });
        ((CheckBox) view.findViewById(R.id.c6_time_checkBox)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[5].custom_time = -1 * commons[5].custom_time;
                switchTime(5);
            }
        });


        // Change hour time
        ((Button) view.findViewById(R.id.c1_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[0].custom_time += 60;
                showTime(0);
            }
        });
        ((Button) view.findViewById(R.id.c2_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[1].custom_time += 60;
                showTime(1);
            }
        });
        ((Button) view.findViewById(R.id.c3_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[2].custom_time += 60;
                showTime(2);
            }
        });
        ((Button) view.findViewById(R.id.c4_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[3].custom_time += 60;
                showTime(3);
            }
        });
        ((Button) view.findViewById(R.id.c5_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[4].custom_time += 60;
                showTime(4);
            }
        });
        ((Button) view.findViewById(R.id.c6_hh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[5].custom_time += 60;
                showTime(5);
            }
        });


        // Change minute time
        ((Button) view.findViewById(R.id.c1_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[0].custom_time += 5;
                commons[0].custom_time -= commons[0].custom_time % 5;
                if (commons[0].custom_time % 60 < 5) { commons[0].custom_time -= 60; }
                showTime(0);
            }
        });
        ((Button) view.findViewById(R.id.c2_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[1].custom_time += 5;
                commons[1].custom_time -= commons[1].custom_time % 5;
                if (commons[1].custom_time % 60 < 5) { commons[1].custom_time -= 60; }
                showTime(1);
            }
        });
        ((Button) view.findViewById(R.id.c3_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[2].custom_time += 5;
                commons[2].custom_time -= commons[2].custom_time % 5;
                if (commons[2].custom_time % 60 < 5) { commons[2].custom_time -= 60; }
                showTime(2);
            }
        });
        ((Button) view.findViewById(R.id.c4_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[3].custom_time += 5;
                commons[3].custom_time -= commons[3].custom_time % 5;
                if (commons[3].custom_time % 60 < 5) { commons[3].custom_time -= 60; }
                showTime(3);
            }
        });
        ((Button) view.findViewById(R.id.c5_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[4].custom_time += 5;
                commons[4].custom_time -= commons[4].custom_time % 5;
                if (commons[4].custom_time % 60 < 5) { commons[4].custom_time -= 60; }
                showTime(4);
            }
        });
        ((Button) view.findViewById(R.id.c6_mm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commons[5].custom_time += 5;
                commons[5].custom_time -= commons[5].custom_time % 5;
                if (commons[5].custom_time % 60 < 5) { commons[5].custom_time -= 60; }
                showTime(5);
            }
        });


    }

    private void switchTime(int num) {
        TextView txt = vm.findViewById(R.id.c1_time_textView);
        Button hBtn = vm.findViewById(R.id.c1_hh_button);
        Button mBtn = vm.findViewById(R.id.c1_mm_button);
        if (num == 1) { txt = vm.findViewById(R.id.c2_time_textView); hBtn = vm.findViewById(R.id.c2_hh_button); mBtn = vm.findViewById(R.id.c2_mm_button); }
        if (num == 2) { txt = vm.findViewById(R.id.c3_time_textView); hBtn = vm.findViewById(R.id.c3_hh_button); mBtn = vm.findViewById(R.id.c3_mm_button); }
        if (num == 3) { txt = vm.findViewById(R.id.c4_time_textView); hBtn = vm.findViewById(R.id.c4_hh_button); mBtn = vm.findViewById(R.id.c4_mm_button); }
        if (num == 4) { txt = vm.findViewById(R.id.c5_time_textView); hBtn = vm.findViewById(R.id.c5_hh_button); mBtn = vm.findViewById(R.id.c5_mm_button); }
        if (num == 5) { txt = vm.findViewById(R.id.c6_time_textView); hBtn = vm.findViewById(R.id.c6_hh_button); mBtn = vm.findViewById(R.id.c6_mm_button); }
        if (commons[num].custom_time >= 0) {
            txt.setVisibility(View.VISIBLE);
            hBtn.setVisibility(View.VISIBLE);
            mBtn.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.GONE);
            hBtn.setVisibility(View.GONE);
            mBtn.setVisibility(View.GONE);
        }
        showTime(num);
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

    private void showTime(int num) {
        if (commons[num].custom_time > 1440) { commons[num].custom_time -= 1440; }
        long min = commons[num].custom_time % 60;
        long hour = (commons[num].custom_time - min) / 60;
        String formattedTime = ac.padTwo(Long.toString(hour)) + ":" + ac.padTwo(Long.toString(min));
        if (num == 0) { ((TextView) vm.findViewById(R.id.c1_time_textView)).setText(formattedTime); }
        if (num == 1) { ((TextView) vm.findViewById(R.id.c2_time_textView)).setText(formattedTime); }
        if (num == 2) { ((TextView) vm.findViewById(R.id.c3_time_textView)).setText(formattedTime); }
        if (num == 3) { ((TextView) vm.findViewById(R.id.c4_time_textView)).setText(formattedTime); }
        if (num == 4) { ((TextView) vm.findViewById(R.id.c5_time_textView)).setText(formattedTime); }
        if (num == 5) { ((TextView) vm.findViewById(R.id.c6_time_textView)).setText(formattedTime); }
    }

}

