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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
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
        final EditText desc_operacio = (EditText)findViewById(R.id.input_desc_moviment);
        final Button boto_in = (Button)findViewById(R.id.button_add_in);
        final Button boto_out = (Button)findViewById(R.id.button_add_out);
        final TextView etq_saldo = (TextView)findViewById(R.id.label_saldo);

        boto_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar el saldo de la nova operació
                String saldo = edit_import_operacio.getText().toString();

                if (saldo != "0" && saldo != "0,00") {
                    double saldo_act = DB_WR.insert_new_mov(saldo, desc_operacio.getText().toString());
                    double saldo_act_ok = roundTwoDecimals(saldo_act);
                    etq_saldo.setText(String.valueOf(saldo_act_ok).replace(".", ",") + " €");
                    mostrar_avis("S'han inserit correctament " + saldo + " €");
                    edit_import_operacio.setText("");
                    desc_operacio.setText("xxxxxxxx");
                }
            }
        });

        boto_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sumar el saldo de la nova operació
                String saldo = edit_import_operacio.getText().toString();

                if (saldo != "0" && saldo != "0,00") {
                    double saldo_act = DB_WR.insert_new_mov("-" + saldo, desc_operacio.getText().toString());
                    double saldo_act_ok = roundTwoDecimals(saldo_act);
                    etq_saldo.setText(String.valueOf(saldo_act_ok).replace(".", ",") + " €");
                    mostrar_avis("S'han pagat " + saldo + " €");
                    edit_import_operacio.setText("");
                    desc_operacio.setText("xxxxxxxx");
                }
            }
        });
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return Double.valueOf(twoDForm.format(d));
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
