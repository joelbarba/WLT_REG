package com.example.wlt_reg3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
            this.mHelper.resetDB(db);
//            db.execSQL("delete from SALDO_ACT");
//            db.execSQL("delete from MOVIMENTS");
//            db.execSQL("insert into SALDO_ACT (saldo) values (0)");
        }
    }






    // Returns the 6 common items
    public C_Common_Item[] get_commons() {
        C_Common_Item[] items = new C_Common_Item[6];
        Cursor F_cursor = db.rawQuery("select descripcio, import, custom_time from COMMON_ITEMS order by id", null);
        if (F_cursor.moveToFirst()) { items[0] = load_common(F_cursor); }
        if (F_cursor.moveToNext())  { items[1] = load_common(F_cursor); }
        if (F_cursor.moveToNext())  { items[2] = load_common(F_cursor); }
        if (F_cursor.moveToNext())  { items[3] = load_common(F_cursor); }
        if (F_cursor.moveToNext())  { items[4] = load_common(F_cursor); }
        if (F_cursor.moveToNext())  { items[5] = load_common(F_cursor); }
        F_cursor.close();
        return items;
    }

    private C_Common_Item load_common(Cursor cur) {
        C_Common_Item item = new C_Common_Item();
        item.desc = cur.getString(0);
        item.import_mov = cur.getDouble(1);
        item.custom_time = cur.getLong(2);
        if (item.import_mov < 0) { item.signe = "-"; } else { item.signe = "+"; }
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        item.import_str = String.valueOf(twoDForm.format(item.import_mov)).replace(".", ",").replace("-", "");
        return item;
    }

    // Actualitza el moviment.
    public boolean save_common(C_Common_Item[] commons) {
        uCommSent(commons, 0);
        uCommSent(commons, 1);
        uCommSent(commons, 2);
        uCommSent(commons, 3);
        uCommSent(commons, 4);
        uCommSent(commons, 5);
        return true;
    }
    private void uCommSent(C_Common_Item[] commons, Integer num) {
        commons[num].import_mov = convertImportStr(commons[num].import_str, commons[num].signe);
        String sent = "update COMMON_ITEMS " +
                "  set descripcio = '" + commons[num].desc + "', " +
                     " import = " + String.valueOf(commons[num].import_mov)
             + " where id = " + String.valueOf(num + 1);
        db.execSQL(sent);
    };


    // Retorna el saldo actual
    public double get_saldo() {
        double saldo = 0;
        // Cursor F_cursor = db.rawQuery("select saldo from SALDO_ACT", null);
        Cursor F_cursor = db.rawQuery("select saldo_post from MOVIMENTS order by id_ordre desc limit 1", null);
        if (F_cursor.moveToFirst()) {   saldo = F_cursor.getDouble(0); }
        F_cursor.close();
        return saldo;
    }

    // Retorna el id del últim moviment
    public int get_id_ult_mov() {
        int ult_mov = 0;
        // Cursor F_cursor = db.rawQuery("select id_ult_mov from SALDO_ACT", null);
        Cursor F_cursor = db.rawQuery("select id_mov from MOVIMENTS order by id_ordre desc limit 1", null);
        if (F_cursor.moveToFirst()) {   ult_mov = F_cursor.getInt(0); }
        F_cursor.close();
        return ult_mov;
    }

    // Retorna info del últim moviment editada. 0=>Import editat, 1=>Data editada, 2=>Signe,  3=>descripció
    public String[] get_last_mov_info() {
        double import_mov = 0;
        String date_mov = "";
        String descripcio = "";

//        Cursor F_cursor = db.rawQuery("select import, data_mov, descripcio from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT)", null);
        Cursor F_cursor = db.rawQuery("select import, data_mov, descripcio from MOVIMENTS order by id_ordre desc limit 1", null);
        if (F_cursor.moveToFirst()) {
            import_mov  = F_cursor.getDouble(0);
            date_mov    = F_cursor.getString(1);
            descripcio    = F_cursor.getString(2);
        }
        F_cursor.close();

        String signe = "";
        if (import_mov < 0) { signe = "-"; }
        return new String[] { editarImport(import_mov), date_mov, signe, descripcio };
    }

    // Retorna info del moviment.
    public C_Moviment get_mov_info(int id_mov) {
        C_Moviment mov = new C_Moviment();

        Cursor F_cursor = db.rawQuery("select id_mov, import, descripcio, data_mov, geoposicio, saldo_post, id_ordre from MOVIMENTS where id_mov = " + String.valueOf(id_mov), null);
        if (F_cursor.moveToFirst()) {
            mov.id_mov          = id_mov;
            mov.import_mov      = F_cursor.getDouble(1);
            mov.desc_mov        = F_cursor.getString(2);
            mov.data_editada    = F_cursor.getString(3);
            mov.geopos_mov      = F_cursor.getString(4);
            mov.saldo_post      = F_cursor.getDouble(5);
            mov.id_ordre        = F_cursor.getInt(6);

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


    // Actualitza el moviment.
    public boolean set_mov_DB(C_Moviment mov) {

        double mov_import = convertImportStr(mov.import_editat, mov.signe);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        db.execSQL("update MOVIMENTS set " +
                " import = " + String.valueOf(mov_import) + ", " +
                " descripcio = '" + mov.desc_mov + "', " +
                " data_mov = '" + dateFormat.format(mov.data_mov) + "' " +
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

//            int h = date.get(Calendar.HOUR_OF_DAY);
//            mov_date.get(Calendar.HOUR_OF_DAY)

            ContentValues newRow = new ContentValues();
            newRow.put("import", String.valueOf(mov_import));
            newRow.put("descripcio", descripcio);
            newRow.put("data_mov", dateFormat.format(date));
            // newRow.put("geoposicio",    "xxxxxx");
//            newRow.put("saldo_post", String.valueOf(saldo_post));
            return db.insert("MOVIMENTS", null, newRow);
        }

        return -1;

    }

    // Eliminar moviment
    public void eliminar_mov(C_Moviment mov) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        db.execSQL("delete from MOVIMENTS where id_mov = " + String.valueOf(mov.id_mov));
    }


    // Eliminar l'últim moviment
    public void eliminar_ult_mov() {

//        db.execSQL("delete from MOVIMENTS where id_mov = (select id_ult_mov from SALDO_ACT);");
        db.execSQL("delete from MOVIMENTS where id_mov = (select id_mov from MOVIMENTS order by id_ordre desc limit 1);");

    }



    // Retorna tots els moviments
    public Cursor get_llista_moviments(int id_ord_offset, int window_count) {

        String sent =
                "select id_mov as _id, " +
                "       import, " +
                "       id_ordre || ' - ' || descripcio, " +
                "       strftime('%Y-%m-%d', data_mov) || ' ' || " +
                "       (case cast (strftime('%w', data_mov) as integer)" +
                "           when 0 then 'Sun'" +
                "           when 1 then 'Mon'" +
                "           when 2 then 'Tue'" +
                "           when 3 then 'Wed'" +
                "           when 4 then 'Thu'" +
                "           when 5 then 'Fri'" +
                "                  else 'Sat' end), " +
//                "                  else 'SAT' end) || ' ' || strftime('%d-%m-%Y', data_mov), " +
                "        geoposicio, saldo_post, id_ordre " +
                "   from MOVIMENTS ";

        if (id_ord_offset != 0) sent += " where id_ordre <= " + String.valueOf(id_ord_offset);

        sent += " order by id_ordre desc " +
                " limit " + String.valueOf(window_count);

        return db.rawQuery(sent, null);

        /*
        return db.rawQuery("select id_mov as _id, " +
                "import || ' € : ' || descripcio    as info1, " +
                "data_mov                           as info2 " +
                " from MOVIMENTS order by id_mov desc", null);
                */
    }

    // Retorna el ID_MOV del últim moviment de la pàgina
    public int get_last_offset(int id_ord_offset, int window_count) {
        int last_ofset_cur_pag = id_ord_offset;

        String sent = "select id_ordre from (" +
                "           select id_ordre from MOVIMENTS where id_ordre <= " + String.valueOf(id_ord_offset) +
                "           order by id_ordre desc limit " + String.valueOf(window_count) +
                ") order by id_ordre limit 1";

        Cursor F_cursor = db.rawQuery(sent, null);
        if (F_cursor.moveToFirst()) last_ofset_cur_pag  = F_cursor.getInt(0);
        F_cursor.close();
        return last_ofset_cur_pag;
    }

    // Retorna el ID_MOV del primer moviment (ordre invers) per la pàgina anterior
    public int get_next_offset(int id_ord_offset, int window_count) {
        int next_offset = id_ord_offset;
        int last_offset_cur_pag = get_last_offset(id_ord_offset, window_count);

        String sent = "select id_ordre from (" +
                      "         select id_ordre from MOVIMENTS where id_ordre < " + String.valueOf(id_ord_offset) +
                      "         order by id_ordre desc limit " + String.valueOf(window_count) +
                      ") order by id_ordre limit 1";

        Cursor F_cursor = db.rawQuery(sent, null);
        if (F_cursor.moveToFirst()) next_offset  = F_cursor.getInt(0);
        F_cursor.close();

        // Si l'últim id de la pàgina és igual al primer de la pàgina següent, és la última pàgina
        if (last_offset_cur_pag == next_offset) next_offset = -1;

        return next_offset;
    }


    // Retorna el ID_MOV del primer moviment (ordre invers) per la pàgina anterior
    public int get_prev_offset(int id_ord_offset, int window_count) {
        int next_offset = id_ord_offset;

        String sent = "select id_ordre from (" +
                "           select id_ordre from MOVIMENTS where id_ordre > " + String.valueOf(id_ord_offset) +
                "           order by id_ordre limit " + String.valueOf(window_count) +
                ") order by id_ordre desc limit 1";

        Cursor F_cursor = db.rawQuery(sent, null);
        if (F_cursor.moveToFirst()) next_offset  = F_cursor.getInt(0);
        F_cursor.close();

        if (id_ord_offset == next_offset) next_offset = -1;    // primera pàgina

        return next_offset;
    }


    // Retorna el total de pàgines i la pàgina actual
    public int[] get_pag_info(int id_ord_offset, int window_count) {
        int current_pag = 0;
        int total_pags = 0;

        String sent = "select case (val_cur) when trunc_val_cur then trunc_val_cur else trunc_val_cur + 1 end, " +
                "       case (val_tot) when trunc_val_tot then trunc_val_tot else trunc_val_tot + 1 end " +
                " from ( " +
                " select count(*) / round(t2.window) as val_tot, " +
                "       cast(count(*) / round(t2.window) as integer) as trunc_val_tot, " +
                "       count(case when (id_ordre >= t2.cur_id) then 1 else null end) / round(t2.window) as val_cur, " +
                "       cast(count(case when (id_ordre >= t2.cur_id) then 1 else null end) / round(t2.window) as integer) as trunc_val_cur " +
                " from MOVIMENTS, " +
                " (select " + String.valueOf(id_ord_offset) + " as cur_id, " + String.valueOf(window_count) + " as window) as t2)";

        Cursor F_cursor = db.rawQuery(sent, null);
        if (F_cursor.moveToFirst()) {
            current_pag = F_cursor.getInt(0);
            total_pags = F_cursor.getInt(1);
        }
        F_cursor.close();

        return new int[] { current_pag, total_pags };
    }



    // Retorna tots els moviments
    public Cursor get_llista_moviments_cvs() {
        return db.rawQuery("select id_mov " +
                "       || ';' ||  ifnull(import, '') " +
                "       || ';' ||  ifnull(descripcio, '') " +
                "       || ';' ||  ifnull(data_mov, '') " +
                "       || ';' ||  ifnull(geoposicio, '') " +
                "       || ';' ||  ifnull(saldo_post, '') " +
                "       || ';' ||  ifnull(id_ordre, '') " +
                "       || ';;;' || ifnull(strftime('%d/%m/%Y', data_mov), '')" +
                "       || ';'  ||  ifnull(descripcio, '') " +
                "       || ';;' ||  ifnull(import, '') " +
                "       || ';;' ||  '=\"4. Efectiu cartera\"' " +
                " as linia " +
                "  from MOVIMENTS " +
                " order by id_ordre", null);
    }

/*
    public void update_saldo_post_all(int id_mov) {
        db.execSQL("update MOVIMENTS " +
                "      set saldo_post = import  " +
                "                     + ifnull((select t2.saldo_post " +
                "                                 from MOVIMENTS t2 " +
                "                                where t2.id_mov < MOVIMENTS.id_mov " +
                "                                order by t2.data_mov desc, t2.id_mov desc limit 1), 0) " +
                "where data_mov >= (select data_mov from MOVIMENTS where id_mov >= " + String.valueOf(id_mov) + " limit 1)");
        db.execSQL("delete from SALDO_ACT");
        db.execSQL("insert into SALDO_ACT (id_ult_mov, saldo)  select id_mov, saldo_post from MOVIMENTS  where id_mov = (select max(id_mov) from MOVIMENTS)");
    }
*/

    // Test correct import string
    public double convertImportStr(String str_import, String sign_import) {

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
    public String editarImport(double import_num) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        String import_num_ok = String.valueOf(twoDForm.format(import_num));
        return import_num_ok.replace(".", ",");
    }

}