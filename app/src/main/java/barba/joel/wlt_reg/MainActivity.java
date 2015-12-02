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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        // mostrar_avis("INI APP");
        // Log.i(LOGTAG, "ini appppp");



        // Iniciar DB
        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();
        DB_WR.ini_db(false);
        mostrarSaldoAct(DB_WR.get_saldo());


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
                            public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
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
                // DB_WR.close();
                Intent i = new Intent(context, ListMovsActivity.class);
                startActivity(i);
            }
        });

    }

    // Afegir nova operació
    boolean createNewOp(String str_import, String sign_import, String descripcio) {

        double import_num = convertImportStr(str_import, sign_import);

        if (import_num != 0) {
            double saldo_act = DB_WR.insert_new_mov(import_num, descripcio);
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
        else {                      id_label_info_last_imp.setTextColor(getResources().getColor(R.color.colorImpPos)); }
    }


    // Convertir double a format String amb 2 decimals fixos
    String editarImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }


    // Test corret import string
    double convertImportStr(String str_import, String sign_import) {

        String str_import_ok = str_import.replace(",", ".");
        if (str_import_ok == "") { str_import_ok = "0"; }

        // Comprovar que el string no conté caràcters invàlids
        if (str_import_ok.replace(".", "")
                .replace("1", "")
                .replace("2", "")
                .replace("3", "")
                .replace("4", "")
                .replace("5", "")
                .replace("6", "")
                .replace("7", "")
                .replace("8", "")
                .replace("9", "")
                .replace("0", "") != "") { return 0; }
        Log.i(LOGTAG, "str_import_ok = " + str_import_ok);

        double import_mov = Double.parseDouble(str_import_ok);
        double import_mov_ok = (double) Math.round(import_mov * 100) / 100;
        if (sign_import == "-") { import_mov_ok = -import_mov_ok; }
        Log.i(LOGTAG, "import_mov_ok = " + String.valueOf(import_mov_ok));

        return import_mov_ok;
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
