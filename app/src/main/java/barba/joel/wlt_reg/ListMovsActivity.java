package barba.joel.wlt_reg;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListMovsActivity extends AppCompatActivity {

    private DBManager DB_WR = null;
    final Context context = this;
    private static final String LOGTAG = "ListMovsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movs);

        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();


        final ListView llista = (ListView)findViewById(R.id.llista_moviments_view);
/*
        final String[] datos = new String[]{"Elem1","Elem2","Elem3","Elem4","Elem5"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        llista.setAdapter(adaptador);
*/

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                DB_WR.get_llista_moviments(),
                new String[] { "info1", "info2" },
                new int[] { android.R.id.text1, android.R.id.text2 });
        llista.setAdapter(adapter);

        llista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Enllaçar amb la pantalla de detall del moviment
                Intent i = new Intent(context, DetailMovActivity.class);
                Bundle b = new Bundle();
                b.putString("ID_ULT_MOV", String.valueOf(id));
                i.putExtras(b);

                startActivity(i);
            }
        });

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
