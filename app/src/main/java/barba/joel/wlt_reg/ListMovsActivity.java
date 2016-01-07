package barba.joel.wlt_reg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ListMovsActivity extends AppCompatActivity {

    private DBManager DB_WR = null;
    final Context context = this;
    private static final String LOGTAG = "ListMovsActivity";
    public List<C_Moviment> llista_movs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movs);

        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();

        carregar_llista();

        final ListView llista = (ListView) findViewById(R.id.llista_moviments_view);
        C_List_Mov_Adapter adp = new C_List_Mov_Adapter(this, llista_movs);
        llista.setAdapter(adp);

        llista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mostrar_avis("position = "+ position + ", id = " + id);

                // Enllaçar amb la pantalla de detall del moviment
                Intent i = new Intent(context, DetailMovActivity.class);
                Bundle b = new Bundle();
                b.putString("ID_ULT_MOV", String.valueOf(id));
                i.putExtras(b);

                startActivity(i);
            }
        });

    }

    void carregar_llista() {

        this.llista_movs = new ArrayList<C_Moviment>();

        Cursor F_cursor = DB_WR.get_llista_moviments();
        if (F_cursor.moveToFirst()) {
            do {
                C_Moviment mov = new C_Moviment();

                mov.id_mov          = F_cursor.getInt(0);;
                mov.import_mov      = F_cursor.getDouble(1);
                mov.desc_mov        = F_cursor.getString(2);

                mov.data_editada    = F_cursor.getString(3);
                mov.geopos_mov      = F_cursor.getString(4);
                mov.saldo_post      = F_cursor.getDouble(5);

                mov.import_editat       = DB_WR.editarImport(mov.import_mov).replace("-", "");
                mov.saldo_post_editat   = DB_WR.editarImport(mov.saldo_post);
                if (mov.import_mov < 0) { mov.signe = "-"; }

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    mov.data_mov = format.parse(mov.data_editada);
                } catch (ParseException e) {
                    mov.data_mov = null;
                }

                llista_movs.add(mov);
            } while(F_cursor.moveToNext());
        }

        F_cursor.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
        carregar_llista();
        final ListView llista = (ListView) findViewById(R.id.llista_moviments_view);
        C_List_Mov_Adapter adp = new C_List_Mov_Adapter(this, llista_movs);
        llista.setAdapter(adp);
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
