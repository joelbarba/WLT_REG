package barba.joel.wlt_reg;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailMovActivity extends AppCompatActivity {

    final Context context = this;
    private static final String LOGTAG = "DetailMovActivity";
    private DBManager DB_WR = null;

    private C_Moviment mov = new C_Moviment();

    Calendar mov_date;
    DecimalFormat twoDForm = new DecimalFormat("00");
    DecimalFormat fourDForm = new DecimalFormat("0000");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mov);

        final EditText      input_import_mov    = (EditText)        findViewById(R.id.input_import_mov);
        final ToggleButton  toggle_import_sign  = (ToggleButton)    findViewById(R.id.toggle_import_sign);
        final EditText      input_desc_mov      = (EditText)        findViewById(R.id.input_desc_mov);
//        final DatePicker    mov_det_datePicker  = (DatePicker)      findViewById(R.id.mov_det_datePicker);
//        final TimePicker    mov_det_timePicker  = (TimePicker)      findViewById(R.id.mov_det_timePicker);

        Button button_del_mov           = (Button) findViewById(R.id.button_del_mov);
        Button button_mod_mov           = (Button) findViewById(R.id.button_mod_mov);

        Button id_button_year_up          = (Button) findViewById(R.id.id_button_year_up);
        Button id_button_month_up         = (Button) findViewById(R.id.id_button_month_up);
        Button id_button_day_up           = (Button) findViewById(R.id.id_button_day_up);
        Button id_button_hour_up          = (Button) findViewById(R.id.id_button_hour_up);
        Button id_button_min_up           = (Button) findViewById(R.id.id_button_min_up);
        Button id_button_year_down        = (Button) findViewById(R.id.id_button_year_down);
        Button id_button_month_down       = (Button) findViewById(R.id.id_button_month_down);
        Button id_button_day_down         = (Button) findViewById(R.id.id_button_day_down);
        Button id_button_hour_down        = (Button) findViewById(R.id.id_button_hour_down);
        Button id_button_min_down         = (Button) findViewById(R.id.id_button_min_down);


        // Recuperar el ID a mostrar
        Bundle b = this.getIntent().getExtras();
        this.mov.id_mov = Integer.parseInt(b.getString("ID_ULT_MOV"));

        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();

        // Carregar info del moviment i mostrar-la
        this.mov = DB_WR.get_mov_info(this.mov.id_mov);
        input_desc_mov.setText(this.mov.desc_mov);

        input_import_mov.setText(this.mov.import_editat);
        if (this.mov.signe == "-")  {
            toggle_import_sign.setChecked(true);
            input_import_mov.setTextColor(ContextCompat.getColor(context, R.color.colorImpNeg));
        } else {
            toggle_import_sign.setChecked(false);
            input_import_mov.setTextColor(ContextCompat.getColor(context, R.color.colorImpPos));
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
                    input_import_mov.setTextColor(ContextCompat.getColor(context, R.color.colorImpNeg));
                } else {
                    input_import_mov.setTextColor(ContextCompat.getColor(context, R.color.colorImpPos));
                }
            }
        });



        // Boto eliminar moviment
        button_del_mov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Atenció");

                alertDialogBuilder
                        .setMessage("Estas segur que vols eliminar aquesta operació?")
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Confirmació possitiva
                                DB_WR.eliminar_mov(mov);
                                finish();
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

        // Boto modificar moviment
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

                if (DB_WR.set_mov_DB(mov_tmp)) {
                    finish();
                } else {
                    mostrar_avis("Error. Try again");
                }
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
                mov_date.add(Calendar.MINUTE, 1);
                actualitzar_datepicker();
            }
        });
        id_button_min_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mov_date.add(Calendar.MINUTE, -1);
                actualitzar_datepicker();
            }
        });


        input_desc_mov.requestFocus();    // Focus al input text de la descripció

    }


    public void actualitzar_datepicker() {
        TextView id_label_year          = (TextView) findViewById(R.id.id_label_year);
        TextView id_label_month         = (TextView) findViewById(R.id.id_label_month);
        TextView id_label_day           = (TextView) findViewById(R.id.id_label_day);
        TextView id_label_hour          = (TextView) findViewById(R.id.id_label_hour);
        TextView id_label_min           = (TextView) findViewById(R.id.id_label_min);

        id_label_year.setText(  String.valueOf(fourDForm.format(mov_date.get(Calendar.YEAR))));
        id_label_month.setText( String.valueOf(twoDForm.format(mov_date.get(Calendar.MONTH) + 1)));
        id_label_day.setText(   String.valueOf(twoDForm.format(mov_date.get(Calendar.DAY_OF_MONTH))));
        id_label_hour.setText(  String.valueOf(twoDForm.format(mov_date.get(Calendar.HOUR_OF_DAY))));
        id_label_min.setText(   String.valueOf(twoDForm.format(mov_date.get(Calendar.MINUTE))));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DB_WR.close();
    }


    private void mostrar_avis(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
