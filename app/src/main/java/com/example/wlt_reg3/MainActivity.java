package com.example.wlt_reg3;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements WRInterface {
    public DBManager DB_WR = null;
    public double balance = 23.12;
    public int testVar = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init DB
        this.DB_WR = new DBManager(getApplicationContext()); // Crear l'interface amb la DB
        this.DB_WR.open();
        this.DB_WR.ini_db(false);
        this.balance = this.DB_WR.get_saldo();
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

    public double insertNewMov(String str_import, String sign_import, String descripcio) {
       this.balance = this.DB_WR.insert_new_mov(str_import, sign_import, descripcio);
       return this.balance;
    }


    // Converts double to a format String with 2 fixed decimal
    public String formatImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }

    public void setVal(int num) {
        this.testVar = num;
    }
}
