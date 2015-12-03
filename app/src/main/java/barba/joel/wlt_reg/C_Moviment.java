package barba.joel.wlt_reg;

import java.util.Date;

/**
 * Created by barba on 3/12/15.
 */
public class C_Moviment {

    public int id_mov;
    public double import_mov;
    public String desc_mov;
    public Date data_mov;
    public String geopos_mov;
    public double saldo_post;

    public String import_editat;
    public String saldo_post_editat;
    public String data_editada;
    public String signe;

    public C_Moviment() {
        this.id_mov = 0;
        this.import_mov = 0;
        this.desc_mov = "";
        this.data_mov = null;
        this.geopos_mov = "";
        this.saldo_post = 0;
        this.import_editat = "0,00";
        this.saldo_post_editat = "0,00";
        this.data_editada = "";
        this.signe = "";
    }

    public void loadMovDB(int id_mov) {

    }


}
