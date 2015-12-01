package barba.joel.wlt_reg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBManager {

    private DataBaseSQLiteHelper mHelper = null;
    private SQLiteDatabase db = null;

    public DBManager(final Context context) {
        this.mHelper = new DataBaseSQLiteHelper(context);
    }

    // Obre la base de dades i retorna la instància
    public DBManager open() {
        db = this.mHelper.getWritableDatabase();
        return this;
    }

    // Tanca la base de dades i allibera els recursos
    public void close() {
        if(db != null) { db.close(); }
    }

    public void ini_db(boolean reload) {
        if ((reload) || (db.rawQuery("select 1 from SALDO_ACT", null).getCount() == 0)) {
            db.execSQL("delete from SALDO_ACT");
            db.execSQL("delete from MOVIMENTS");
            db.execSQL("insert into SALDO_ACT (saldo) values (0)");
        }
    }






    // Retorna el saldo actual
    public double get_saldo() {
        double saldo = 0;
        Cursor F_cursor = db.rawQuery("select saldo from SALDO_ACT", null);
        if (F_cursor.moveToFirst()) {   saldo = F_cursor.getDouble(0); }
        F_cursor.close();
        return saldo;
    }


    // Retorna info del últim moviment editada. 0=>Import editat, 1=>Data editada, 2=>Signe
    public String[] get_last_mov_info() {
        double import_mov = 0;
        String date_mov = "";

        Cursor F_cursor = db.rawQuery("select import, data_mov from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT)", null);
        if (F_cursor.moveToFirst()) {
            import_mov = F_cursor.getDouble(0);
            date_mov = F_cursor.getString(1);
        }
        F_cursor.close();

        String signe = "";
        if (import_mov < 0) { signe = "-"; }
        return new String[] { editarImport(import_mov), date_mov, signe};
    }


    // Inserir nou moviment, i retornar el saldo actual
    public double insert_new_mov(double mov_import, String descripcio) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        double saldo_act = get_saldo();
        double saldo_post = (double) Math.round((saldo_act + mov_import) * 100) / 100;

        ContentValues newRow = new ContentValues();
        newRow.put("import",        String.valueOf(mov_import));
        newRow.put("descripcio",    descripcio);
        newRow.put("data_mov",      dateFormat.format(date));
        // newRow.put("geoposicio",    "xxxxxx");
        newRow.put("saldo_post",    String.valueOf(saldo_post)); //
        db.insert("MOVIMENTS", null, newRow);

        return get_saldo();

    }


    // Eliminar l'últim moviment
    public boolean eliminar_ult_mov() {

        db.execSQL("delete from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT) ");

        return true;
    }




    // Convertir double a format String amb 2 decimals fixos
    String editarImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }

    // Retorna totes les llistes
    public Cursor get_moviments() {
        return db.rawQuery("select id_mov, saldo, descripcio from MOVIMENTS", null);
    }


}