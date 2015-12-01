package barba.joel.wlt_reg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private DBManager DB_WR = null;
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
        mostrar_avis("INI APP");
        Log.i(LOGTAG, "ini appppp");



        // Iniciar DB
        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();
        DB_WR.ini_db(true);

/*
        // Obrir la base de dades 'DBUsuarios' en mode escriptura
        DataBaseSQLiteHelper usdbh = new DataBaseSQLiteHelper(this);
        final SQLiteDatabase db = usdbh.getWritableDatabase();

        // Si s'ha obert correctament
        if(db != null) {
            // Per exemple, insertar 5 registres
            for(int i=1; i<=5; i++) {
                db.execSQL("INSERT INTO moviments (id_mov, descripcio, saldo) " +
                        "VALUES (" + i + ", 'aaaa', " + (i * 43) + ")");
            }
            db.close();
        }
*/


        // Instanciar els controls
        final EditText edit_import_operacio = (EditText)findViewById(R.id.edit_new_input);
        final Button boto_in = (Button)findViewById(R.id.button_add_in);
        final Button boto_out = (Button)findViewById(R.id.button_add_out);
        final TextView etq_saldo = (TextView)findViewById(R.id.label_saldo);


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


        final Button boto_del_last = (Button)findViewById(R.id.button_del_last);
        boto_del_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrar_avis("DDDEEL");
            }
        });

    }

    // Afegir nova operació
    boolean createNewOp(String str_import, String sign_import, String descripcio) {

        double import_num = convertImportStr(str_import, sign_import);

        if (import_num != 0) {
            double saldo_act = DB_WR.insert_new_mov(import_num, descripcio);
            mostrarSaldoAct(saldo_act);

            TextView etq_info_last = (TextView)findViewById(R.id.id_label_info_last);
            etq_info_last.setText(DB_WR.get_last_mov_info());


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
    boolean mostrarSaldoAct(double saldo) {
        TextView etq_saldo = (TextView)findViewById(R.id.label_saldo);
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(saldo));
        etq_saldo.setText(import_num_ok.replace(".", ",") + " €");
        // etq_saldo.setText(editarImport(saldo));
        return true;
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
