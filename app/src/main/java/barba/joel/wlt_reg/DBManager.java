package barba.joel.wlt_reg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    // Retorna el id del últim moviment
    public int get_id_ult_mov() {
        int ult_mov = 0;
        Cursor F_cursor = db.rawQuery("select id_ult_mov from SALDO_ACT", null);
        if (F_cursor.moveToFirst()) {   ult_mov = F_cursor.getInt(0); }
        F_cursor.close();
        return ult_mov;
    }

    // Retorna info del últim moviment editada. 0=>Import editat, 1=>Data editada, 2=>Signe
    public String[] get_last_mov_info() {
        double import_mov = 0;
        String date_mov = "";

        Cursor F_cursor = db.rawQuery("select import, data_mov from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT)", null);
        if (F_cursor.moveToFirst()) {
            import_mov  = F_cursor.getDouble(0);
            date_mov    = F_cursor.getString(1);
        }
        F_cursor.close();

        String signe = "";
        if (import_mov < 0) { signe = "-"; }
        return new String[] { editarImport(import_mov), date_mov, signe };
    }

    // Retorna info del moviment.
    public C_Moviment get_mov_info(int id_mov) {
        C_Moviment mov = new C_Moviment();

        Cursor F_cursor = db.rawQuery("select id_mov, import, descripcio, data_mov, geoposicio, saldo_post from MOVIMENTS where id_mov = " + String.valueOf(id_mov), null);
        if (F_cursor.moveToFirst()) {
            mov.id_mov          = id_mov;
            mov.import_mov      = F_cursor.getDouble(1);
            mov.desc_mov        = F_cursor.getString(2);
            mov.data_editada    = F_cursor.getString(3);
            mov.geopos_mov      = F_cursor.getString(4);
            mov.saldo_post      = F_cursor.getDouble(5);

            mov.import_editat       = editarImport(mov.import_mov).replace("-", "");
            mov.saldo_post_editat   = editarImport(mov.saldo_post);
            if (mov.import_mov < 0) { mov.signe = "-"; }

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                mov.data_mov = format.parse(mov.data_editada);
            } catch (ParseException e) {
                mov.data_mov = null;
            }
        }
        F_cursor.close();
        return mov;
    }


    // Retorna info del moviment.
    public boolean set_mov_DB(C_Moviment mov) {

        double mov_import = convertImportStr(mov.import_editat, mov.signe);


        db.execSQL("update MOVIMENTS set " +
                " import = " + String.valueOf(mov_import) + ", " +
                " descripcio = '" + mov.desc_mov + "' " + // ", " +
//                " data_mov = null, " +
//                " geoposicio = '' " +
                " where id_mov = " + mov.id_mov);

        return true;
    }


    // Inserir nou moviment, i retornar el saldo actual
    public double insert_new_mov(String str_import, String sign_import, String descripcio) {

        double mov_import = convertImportStr(str_import, sign_import);

        if (mov_import != 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            double saldo_act = get_saldo();
            double saldo_post = (double) Math.round((saldo_act + mov_import) * 100) / 100;

            ContentValues newRow = new ContentValues();
            newRow.put("import", String.valueOf(mov_import));
            newRow.put("descripcio", descripcio);
            newRow.put("data_mov", dateFormat.format(date));
            // newRow.put("geoposicio",    "xxxxxx");
            newRow.put("saldo_post", String.valueOf(saldo_post)); //
            db.insert("MOVIMENTS", null, newRow);
        }
        return mov_import;

    }


    // Eliminar l'últim moviment
    public boolean eliminar_ult_mov() {

        db.execSQL("delete from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT) ");

        return true;
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

        double import_mov = Double.parseDouble(str_import_ok);
        double import_mov_ok = (double) Math.round(import_mov * 100) / 100;
        if (sign_import == "-") { import_mov_ok = -import_mov_ok; }

        return import_mov_ok;
    }

    // Convertir double a format String amb 2 decimals fixos
    String editarImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }

    // Retorna tots els moviments
    public Cursor get_llista_moviments() {
        return db.rawQuery("select id_mov as _id, " +
                "import || ' € : ' || descripcio    as info1, " +
                "data_mov                           as info2 " +
                " from MOVIMENTS order by id_mov desc", null);
    }


}