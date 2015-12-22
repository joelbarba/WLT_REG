package barba.joel.wlt_reg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private DBManager DB_WR = null;
    final Context context = this;
    private static final String LOGTAG = "SettingsActivity";

    public String currentDBPath  = "/data/data/barba.joel.wlt_reg/databases/DB_WALLET_REG";
    public String backupDBPath   = Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db";
    public String backupFilePath = Environment.getExternalStorageDirectory() + File.separator + "FILE_WLT_REG.csv";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        DB_WR.open();

        // Boto modificar últim moviment
        final Button button_reset = (Button) findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB_WR.ini_db(true);
                mostrar_avis("S'ha reinicialitzat la DB");
                finish();
            }
        });

        // Boto modificar últim moviment
        final Button button_export_db = (Button) findViewById(R.id.button_export_db);
        button_export_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                backup_db_file();
                exportDB();
                backup_db_plain_file();
                finish();
            }
        });

        // Boto modificar últim moviment
        final Button button_import_db = (Button) findViewById(R.id.button_import_db);
        button_import_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Atenció");

                alertDialogBuilder
                        .setMessage("Are you sure you want to import the whole DB? (from file: " + Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db)" )
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Confirmació possitiva
                                importDB();
                                mostrar_avis("New DB ready");
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

    }


    private void backup_db_file() {

        mostrar_avis("exportant DB");

        File f = new File("/data/data/barba.joel.wlt_reg/databases/DB_WALLET_REG");
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {

            fis = new FileInputStream(f);
            fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db");

            while (true) {
                int i = fis.read();
                if (i!=-1) { fos.write(i); }
                else { break; }
            }
            fos.flush();
            fos.close();
            fis.close();
            mostrar_avis("OK: " + Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db");

        } catch(Exception e) {
            e.printStackTrace();
            mostrar_avis("DB dump ERROR");

        } finally {
            try {
                fos.close();
                fis.close();
            } catch(IOException ioe) {}
        }

    }


    private void backup_db_plain_file() {

        try {
            FileOutputStream fos = new FileOutputStream(backupFilePath);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append("id_mov;import;descripcio;data_mov;geoposicio;saldo_post\n");

            Cursor F_cursor = DB_WR.get_llista_moviments_cvs();
            if (F_cursor.moveToFirst()) {
                do {
                    myOutWriter.append(F_cursor.getString(0) + "\n");
                } while(F_cursor.moveToNext());
            }
            F_cursor.close();
            myOutWriter.close();

            mostrar_avis("CSV file ready on: " + backupFilePath);

        } catch(Exception e) {
            e.printStackTrace();
            mostrar_avis("File csv export error");
        }

    }

    private void exportDB() {
        try {
            if (Environment.getExternalStorageDirectory().canWrite()) {
                File currentDB = new File(currentDBPath);
                File backupDB  = new File(backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                mostrar_avis("backup ready on: " + backupDBPath);
            }
        } catch (Exception e) {
            mostrar_avis("error");
        }
    }


    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                File backupDB  = new File(currentDBPath);
                File currentDB = new File(backupDBPath);  // <--- Importar des del fitxer de BKP

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }



    @Override
    protected  void onDestroy() {
        super.onDestroy();
        DB_WR.close();
    }

    private void mostrar_avis(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
