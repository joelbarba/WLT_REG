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

    // Retorna el saldo actual
    public String get_last_mov_info() {
        double import_mov = 0;
        String date_mov = "";

        Cursor F_cursor = db.rawQuery("select import, data_mov from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT)", null);
        if (F_cursor.moveToFirst()) {
            import_mov = F_cursor.getDouble(0);
            date_mov = F_cursor.getString(1);
        }
        F_cursor.close();

        return "Last mov: " + editarImport(import_mov) + " € \n" + date_mov;
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


/*
        db.execSQL( "insert into MOVIMENTS " +
                    "select ifnull(max(id_mov), 0) + 1, " +
                    "       replace('" + saldo + "', ',', '.')," +
                            "'" + descripcio + "', " +
                            "DATETIME('now') " +
                    "  from MOVIMENTS");
        db.execSQL( "update SALDO_ACT set saldo = saldo + " + String.valueOf(mov_import));
*/

        return get_saldo();

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

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return Double.valueOf(twoDForm.format(d));
    }

}