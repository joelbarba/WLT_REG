package barba.joel.wlt_reg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DataBaseSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DB_WALLET_REG";
    private static final int DATABASE_VERSION = 4;

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
            "saldo_post   long)";


    public DataBaseSQLiteHelper(Context contexto) {
        super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sent_create_saldo_act);
        db.execSQL(sent_create_moviments);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        // Si s'ha canviat de versió, actualitza i exporta les dades
        db.execSQL("drop table if exists SALDO_ACT");
        db.execSQL("drop table if exists MOVIMENTS");
        db.execSQL(sent_create_saldo_act);
        db.execSQL("insert into SALDO_ACT (saldo, id_ult_mov) values (0, null)");
        db.execSQL(sent_create_moviments);

    }
}