package com.example.wlt_reg3;

import android.database.Cursor;

public interface WRInterface {
    public double balance = 0;
    public DBManager DB_WR = null;

    public double getBalance();
    public double insertNewMov(String str_import, String sign_import, String descripcio);

    public int getSelMovId();
    public void setSelMovId(int movId);
    public C_Moviment loadMov();
    public void saveMov(C_Moviment mov);
    public void delMov(C_Moviment mov);

    public Cursor getMovs(int id_ord_offset, int window_count);
    public int getLastOffset(int id_ord_offset, int window_count);
    public int getNextOffset(int id_ord_offset, int window_count);
    public int getPrevOffset(int id_ord_offset, int window_count);
    public int[] getPagInfo(int id_ord_offset, int window_count);


    public String formatImport(double import_num);
    public void growl(String text);

}
