package com.example.wlt_reg3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements WRInterface {
    public DBManager DB_WR = null;
    public double balance = 0;
    public int selMovId = 0;
    public C_Moviment selMov = new C_Moviment();
    public C_Common_Item[] commons;
    public String currentDBPath  = "/data/data/barba.joel.wlt_reg/databases/DB_WALLET_REG";
    public String backupDBPath   = Environment.getExternalStorageDirectory() + File.separator + "DB_WLT_REG.db";
    public String backupFilePath = Environment.getExternalStorageDirectory() + File.separator + "FILE_WLT_REG.csv";
    public int STORAGE_WRITE_PERMISSION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        this.gotoSettings();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:  gotoFragment(R.id.action_Main_to_Settings); break;  // Config Fragment
                    case R.id.action_commons:   gotoFragment(R.id.action_Main_to_Commons); break;  // Common items Fragment
//                    case R.id.mov_list_show:
//                        saltar_llista_movs();
//                        break;
                }
                return true;
            }
        });

        // Init DB
        this.DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        this.DB_WR.open();
        this.DB_WR.ini_db(false);
        this.balance = this.DB_WR.get_saldo();
        this.commons = this.DB_WR.get_commons();

//        gotoFragment(R.id.action_Main_to_Commons);
//        exportDB();
//        exportCSV();
    }

    private void gotoFragment(int fId) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(fId);
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

    public double getBalance() { return this.balance; }

    public double insertNewMov(String str_import, String sign_import, String desc, Long time) {
        String descCap = desc;
        if (desc.length() > 1) { // Automatically turn first char Uppercase
            descCap = desc.substring(0, 1).toUpperCase() + desc.substring(1);
        }
        this.selMovId = (int) this.DB_WR.insert_new_mov(str_import, sign_import, descCap, time);
        this.balance = this.DB_WR.get_saldo();
        return this.balance;
    }

    public int getSelMovId() { return selMovId; }
    public void setSelMovId(int id_mov) { this.selMovId = id_mov; }
    public C_Moviment loadMov() { return DB_WR.get_mov_info(selMovId); }
    public void delMov(C_Moviment mov) { DB_WR.eliminar_mov(mov); this.selMovId = 0; }
    public void saveMov(C_Moviment mov) {
        if (mov.desc_mov.length() > 1) { // Automatically turn first char Uppercase
            mov.desc_mov = mov.desc_mov.substring(0, 1).toUpperCase() + mov.desc_mov.substring(1);
        }
        if (!DB_WR.set_mov_DB(mov)) {
            growl("Error saving movement");
        }
    }


    public Cursor getMovs(int id_ord_offset, int window_count) { return this.DB_WR.get_llista_moviments(id_ord_offset, window_count); }
    public int getLastOffset(int id_ord_offset, int window_count) { return DB_WR.get_last_offset(id_ord_offset, window_count); }
    public int getNextOffset(int id_ord_offset, int window_count) { return DB_WR.get_next_offset(id_ord_offset, window_count); }
    public int getPrevOffset(int id_ord_offset, int window_count) { return DB_WR.get_prev_offset(id_ord_offset, window_count); }
    public int[] getPagInfo(int id_ord_offset, int window_count) { return DB_WR.get_pag_info(id_ord_offset, window_count); }

    public C_Common_Item[] getCommons() { return this.commons; };
    public void saveCommons(C_Common_Item[] updatedCommons) {
        commons = updatedCommons;
        DB_WR.save_common(updatedCommons);
        growl("Common items actualitzats");
    };


    // It takes 'DB_WLT_REG.db' file external storage root and imports it to DB
    public void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB  = new File(currentDBPath);
                File currentDB = new File(backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }



    // This is the callback after the permission request prompt
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_WRITE_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                growl("OK, permission granted");
            }
        }
    }

    // Check if no permission, and if so prompt the permission request
    public boolean hasNoPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_WRITE_PERMISSION);
                return true;
            }
        }
        return false;
    }

    // It copies 'DB_WLT_REG.db' file to the root of the external storage
    public void exportDB() {
        try {
            // currentDB path = /data/user/0/com.example.wlt_reg/databases/DB_WALLET_REG
            // backupDB path = /data/user/0/com.example.wlt_reg/files/DB_WALLET_REG.db
            // Accessible in file explorer at: /data/data/com.example.wlt_reg/files/DB_WALLET_REG.db

            if (hasNoPermission()) { return; }

            File currentDB = new File(getBaseContext().getDatabasePath("DB_WALLET_REG"), "");
            File backupDB  = new File(getBaseContext().getFilesDir(),"DB_WLT_REG.db");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            growl("backup ready on: " + backupDB.getAbsolutePath());

        } catch (Exception e) {
            growl("error");
        }
    }

    // It copies 'FILE_WLT_REG.csv' file to the root of the external storage
    public void exportCSV() {
        try {

            // Simple example to write output file and share it
//            File gpxfile = new File(getBaseContext().getFilesDir(), "elMeuFitxer.txt");
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append("Hello There!!!");
//            writer.flush();
//            writer.close();
//            File newFile = new File(getBaseContext().getFilesDir(), "elMeuFitxer.txt");
//            Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.example.wlt_reg3.fileprovider", newFile);
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//            shareIntent.setType("text/plain");
//            startActivity(Intent.createChooser(shareIntent, "Share CSV File"));

            if (hasNoPermission()) { return; }

            // ------------------------------------------------------------
            Calendar currDate = Calendar.getInstance();
            String fileName = "FILE_WLT_REG_";
            fileName += currDate.get(Calendar.YEAR)
                    + padTwo("" + (currDate.get(Calendar.MONTH) + 1))
                    + padTwo("" +  currDate.get(Calendar.DAY_OF_MONTH));

            fileName += "_" + padTwo(String.valueOf(currDate.get(Calendar.HOUR_OF_DAY)))
                    + "" + padTwo(String.valueOf(currDate.get(Calendar.MINUTE)))
                    + "" + padTwo(String.valueOf(currDate.get(Calendar.SECOND)));
            fileName += ".csv";

            File csvFile = new File(getBaseContext().getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(csvFile);

            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append("id_mov;import;descripcio;data_mov;geoposicio;saldo_post;id_ordre\n");

            Cursor F_cursor = DB_WR.get_llista_moviments_cvs();
            if (F_cursor.moveToFirst()) {
                do {
                    myOutWriter.append(F_cursor.getString(0) + "\n");
                } while(F_cursor.moveToNext());
            }
            F_cursor.close();
            myOutWriter.close();

            // growl("CSV file ready on: " + csvFile.getAbsolutePath());
            // ------------------------------------------------------------

            File csvFile2 = new File(getBaseContext().getFilesDir(), fileName);
            Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.example.wlt_reg3.fileprovider", csvFile);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("text/csv");

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "This is a snapshot of the WLT_REG");

            startActivity(Intent.createChooser(shareIntent, "Share CSV File"));

            growl("CSV file exported and ready on: " + csvFile.getAbsolutePath());

        } catch(Exception e) {
            e.printStackTrace();
            growl("File csv export error");
        }

    }

    // Full DB reset
    public void resetDB() {
        DB_WR.ini_db(true);
        growl("S'ha reinicialitzat la DB");
    }


    // Converts double to a format String with 2 fixed decimal
    public String formatImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }

    public String padTwo(String val) {
        return String.format("%2s", val).replace(' ', '0');
    }

    public void growl(String text) {
//        Activity activity = getActivity();
//        assert activity != null;
        Toast toast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
