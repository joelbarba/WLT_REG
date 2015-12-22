package barba.joel.wlt_reg;

import android.app.AlertDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DetailMovActivity extends AppCompatActivity {

    final Context context = this;
    private static final String LOGTAG = "DetailMovActivity";
    private DBManager DB_WR = null;

    private C_Moviment mov = new C_Moviment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mov);

        final EditText      input_import_mov    = (EditText)        findViewById(R.id.input_import_mov);
        final ToggleButton  toggle_import_sign  = (ToggleButton)    findViewById(R.id.toggle_import_sign);
        final EditText      input_desc_mov      = (EditText)        findViewById(R.id.input_desc_mov);
        final DatePicker    mov_det_datePicker  = (DatePicker)      findViewById(R.id.mov_det_datePicker);
        final TimePicker    mov_det_timePicker  = (TimePicker)      findViewById(R.id.mov_det_timePicker);

        Button button_del_mov           = (Button) findViewById(R.id.button_del_mov);
        Button button_mod_mov           = (Button) findViewById(R.id.button_mod_mov);


        mov_det_timePicker.setIs24HourView(true);
        mov_det_timePicker.buildLayer();
        mov_det_datePicker.buildLayer();
        mov_det_datePicker.setScaleX((float) 1);

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



        // mov_det_datePicker = ...
        // mov_det_timePicker = ...


        toggle_import_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle_import_sign.isChecked())  {
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
                            public void onClick(DialogInterface dialog,int id) {
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
                mov_tmp.id_mov          = mov.id_mov;
                mov_tmp.import_editat   = input_import_mov.getText().toString();
                mov_tmp.desc_mov        = input_desc_mov.getText().toString();

                if (toggle_import_sign.isChecked()) { mov_tmp.signe = "-"; }
                else                                { mov_tmp.signe = ""; }

                if (DB_WR.set_mov_DB(mov_tmp)) {
                    finish();
                } else {
                    mostrar_avis("Error. Try again");
                }
            }
        });


        input_desc_mov.requestFocus();    // Focus al input text de la descripció

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
