package barba.joel.wlt_reg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private DBManager DB_WR = null;
    final Context context = this;
    private static final String LOGTAG = "MainActivity";


    @Override
    protected void onResume() {
        super.onResume();
        mostrarSaldoAct(DB_WR.get_saldo());
        mostrarUltMov();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DB_WR.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Configurar menu superior
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    // Pantalla de configuració
                    case R.id.action_settings:  startActivity(new Intent(context, SettingsActivity.class)); break;
                    case R.id.mov_list_show:    saltar_llista_movs();  break;      // Pantalla llista de moviments
                }
                return true;
            }
        });

        // mostrar_avis("INI APP");
        // Log.i(LOGTAG, "ini appppp");



        // Iniciar DB
        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();
        DB_WR.ini_db(false);
        mostrarSaldoAct(DB_WR.get_saldo());
        mostrarUltMov();


        // Instanciar els controls
        final EditText edit_import_operacio = (EditText)findViewById(R.id.edit_new_input);
        final Button boto_in = (Button)findViewById(R.id.button_add_in);
        final Button boto_out = (Button)findViewById(R.id.button_add_out);




        boto_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(edit_import_operacio.getText().toString(), "", "");
                // double import_num = convertImportStr(edit_import_operacio.getText().toString(), "");
                // mostrarSaldoAct(import_num);
            }
        });

        boto_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOp(edit_import_operacio.getText().toString(), "-", "");
            }
        });



        // Clickar a sobre l'import del últim moviment
        final TextView id_label_info_last_imp = (TextView) findViewById(R.id.id_label_info_last_imp);
        id_label_info_last_imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar_ult_mov();
            }
        });



        // Boto eliminar últim moviment
        final Button boto_del_last = (Button)findViewById(R.id.button_del_last);
        boto_del_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Atenció");

                alertDialogBuilder
                        .setMessage("Estas segur que vols eliminar la última operació?")
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Confirmació possitiva
                                DB_WR.eliminar_ult_mov();
                                mostrarUltMov();
                                mostrarSaldoAct(DB_WR.get_saldo());
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

        // Boto modificar últim moviment
        final Button boto_mod_last = (Button)findViewById(R.id.boto_mod_last);
        boto_mod_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar_ult_mov();
            }
        });

        // Label saldo
        final TextView label_saldo = (TextView) findViewById(R.id.label_saldo);
        label_saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar_ult_mov();
            }
        });

        // Boto modificar últim moviment
        final Button button_llista = (Button)findViewById(R.id.button_llista);
        button_llista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar_llista_movs();
            }
        });


    }

    private void saltar_llista_movs() {
        Intent i = new Intent(context, ListMovsActivity.class);
        startActivity(i);
    }

    private void saltar_ult_mov() {
        Intent i = new Intent(context, DetailMovActivity.class);
        Bundle b = new Bundle();
        b.putString("ID_ULT_MOV", String.valueOf(DB_WR.get_id_ult_mov()));
        i.putExtras(b);
        startActivity(i);
    }


    // Afegir nova operació
    boolean createNewOp(String str_import, String sign_import, String descripcio) {

        double import_num = DB_WR.insert_new_mov(str_import, sign_import, descripcio);

        if (import_num != 0) {
            double saldo_act = DB_WR.get_saldo();
            mostrarSaldoAct(saldo_act);
            mostrarUltMov();
            mostrar_avis("S'han pagat " + editarImport(import_num) + " €");
            iniInputsOp();
        }
        return true;
    }


    // Inicialitzar valors dels inputs
    boolean iniInputsOp() {
        final EditText edit_import_operacio = (EditText)findViewById(R.id.edit_new_input);
        edit_import_operacio.setText("");

        return true;
    }


    // Mostrar el saldo actual editat al Text View principal
    private void mostrarSaldoAct(double saldo) {
        TextView etq_saldo = (TextView)findViewById(R.id.label_saldo);
        // DecimalFormat twoDForm = new DecimalFormat("0.00");
        // String import_num_ok = String.valueOf(twoDForm.format(saldo));
        // etq_saldo.setText(import_num_ok.replace(".", ",") + " €");
        etq_saldo.setText(editarImport(saldo));
    }

    private void mostrarUltMov() {
        String ult_mov[] = DB_WR.get_last_mov_info();
        TextView id_label_info_last_imp = (TextView)findViewById(R.id.id_label_info_last_imp);
        TextView id_label_info_last_date = (TextView)findViewById(R.id.id_label_info_last_date);

        id_label_info_last_imp.setText(ult_mov[0]);
        id_label_info_last_date.setText(ult_mov[1]);
        if (ult_mov[2] == "-") {   id_label_info_last_imp.setTextColor(getResources().getColor(R.color.colorImpNeg)); }
        else {                     id_label_info_last_imp.setTextColor(getResources().getColor(R.color.colorImpPos)); }
    }


    // Convertir double a format String amb 2 decimals fixos
    String editarImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void mostrar_avis(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
