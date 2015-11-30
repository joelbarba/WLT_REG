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

    // Inserir nou moviment, i retornar el saldo actual
    public double insert_new_mov(String mov_import, String descripcio) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String mov_import_ok = mov_import.replace(",", ".");
        double saldo_act = get_saldo();
        double import_mov = Double.parseDouble(mov_import_ok);
        double saldo_post = roundTwoDecimals(saldo_act + import_mov);

        ContentValues newRow = new ContentValues();
        newRow.put("import",        mov_import_ok);
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
*/




        db.execSQL( "update SALDO_ACT set saldo = saldo + " + mov_import_ok);

        return get_saldo();

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