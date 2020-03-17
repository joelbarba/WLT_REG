package com.example.wlt_reg3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DB_WALLET_REG";
    private static final int DATABASE_VERSION = 24;

    // Definició de la base de dades
    String sent_create_saldo_act = "create table SALDO_ACT (" +
            "saldo          long, " +
            "id_ult_mov     long  null)";

    String sent_create_moviments = "create table MOVIMENTS (" +
            "id_mov       integer primary key autoincrement," +
            "import       long," +
            "descripcio   text," +
            "data_mov     date," +
            "geoposicio   text," +
            "saldo_post   long," +
            "id_ordre     long)";

    String sent_body_trigger = "   update MOVIMENTS set id_ordre = " // Actualitzar el id_ordre correctament de cada moviment
            + "        (select count(*) from MOVIMENTS t2 "
            + "          where ((MOVIMENTS.data_mov > t2.data_mov)"
            + "                 or "
            + "                 ((MOVIMENTS.data_mov = t2.data_mov) and (MOVIMENTS.id_mov > t2.id_mov))"
            + "                )"
            + "        ) + 1;"
            // Actualitzar el saldo posterior segons el nou id_ordre"
            + "   update MOVIMENTS set saldo_post = "
            // + "     -- import + ifnull(round((select saldo_post from MOVIMENTS as t2 where t2.id_ordre < MOVIMENTS.id_ordre order by id_ordre desc limit 1), 2), 0);"
            + "     import + ifnull(round((select sum(import) from MOVIMENTS as t2 where t2.id_ordre < MOVIMENTS.id_ordre), 2), 0);"
            // Actualitzar la taula SALDO_ACT"
            + "   delete from SALDO_ACT; "
            + "   insert into SALDO_ACT (id_ult_mov, saldo) "
            + "     select id_mov, saldo_post from MOVIMENTS order by id_ordre desc limit 1; ";



    public DataBaseSQLiteHelper(Context contexto) {
        super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void resetDB(SQLiteDatabase db) {
        db.execSQL("drop table if exists MOVIMENTS;");
        db.execSQL("drop table if exists SALDO_ACT;");

        db.execSQL(sent_create_saldo_act);
        db.execSQL(sent_create_moviments);
        db.execSQL("create trigger REBUILD_MOVS_SEQ1 after insert on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ2 after delete on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ3 after update on MOVIMENTS begin " + sent_body_trigger + " end;");

        db.execSQL("insert into SALDO_ACT (saldo) values (0)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sent_create_saldo_act);
        db.execSQL(sent_create_moviments);
        db.execSQL("create trigger REBUILD_MOVS_SEQ1 after insert on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ2 after delete on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ3 after update on MOVIMENTS begin " + sent_body_trigger + " end;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        db.execSQL("drop trigger if exists SALDO_ACT_TRIGGER;");
        db.execSQL("drop trigger if exists SALDO_ACT_TRIGGER2;");
        db.execSQL("drop trigger if exists REBUILD_MOVS_SEQ1;");
        db.execSQL("drop trigger if exists REBUILD_MOVS_SEQ2;");
        db.execSQL("drop trigger if exists REBUILD_MOVS_SEQ3;");

        if (versionNueva >= 14) {
            try {
                db.execSQL("PRAGMA recursive_triggers = OFF;"); // Disable recursive triggers (It should have set this way by default)
                db.execSQL("alter table MOVIMENTS add id_ordre long;");
            } catch (Exception e) {}
        }


        db.execSQL("create trigger REBUILD_MOVS_SEQ1 after insert on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ2 after delete on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("create trigger REBUILD_MOVS_SEQ3 after update on MOVIMENTS begin " + sent_body_trigger + " end;");
        db.execSQL("update MOVIMENTS set id_mov = id_mov where id_mov = (select id_mov from MOVIMENTS limit 1);"); // To launch the trigger and order the seq

/*
        // Si s'ha canviat de versió, actualitza i exporta les dades

        db.execSQL("drop table if exists SALDO_ACT");
        db.execSQL("drop table if exists MOVIMENTS");
        db.execSQL("drop trigger if exists SALDO_ACT_TRIGGER");
        db.execSQL("drop trigger if exists SALDO_ACT_TRIGGER2");

        db.execSQL(sent_create_saldo_act);
        db.execSQL("insert into SALDO_ACT (saldo, id_ult_mov) values (0, null)");
        db.execSQL(sent_create_moviments);
        db.execSQL(sent_create_trigger1);
        db.execSQL(sent_create_trigger2);
*/
    }




}