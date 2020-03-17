package com.example.wlt_reg3;

public interface WRInterface {
    public double balance = 0;
    public int testVar = 10;

    public double getBalance();
    public double insertNewMov(String str_import, String sign_import, String descripcio);
    public String formatImport(double import_num);

    public void setVal(int num);
}
