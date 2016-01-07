package barba.joel.wlt_reg;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class C_List_Mov_Adapter extends ArrayAdapter {

    public Activity context;
    public List<C_Moviment> llista_movs;

    C_List_Mov_Adapter(Activity context, List<C_Moviment> llista) {
        super(context, R.layout.item_mov_list, llista);
        this.context = context;
        this.llista_movs = new ArrayList<C_Moviment>(llista);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.item_mov_list, null);


        TextView id_mov_list_desc = (TextView) item.findViewById(R.id.id_mov_list_desc);
        TextView id_mov_list_imp  = (TextView) item.findViewById(R.id.id_mov_list_imp);
        TextView id_mov_list_date = (TextView) item.findViewById(R.id.id_mov_list_date);
        id_mov_list_desc.setText(llista_movs.get(position).desc_mov);
        id_mov_list_imp.setText(llista_movs.get(position).signe + llista_movs.get(position).import_editat + " €");
        id_mov_list_date.setText(llista_movs.get(position).data_editada);

        if (llista_movs.get(position).signe == "-") {   id_mov_list_imp.setTextColor(ContextCompat.getColor(context, R.color.colorImpNeg)); }
        else {                                          id_mov_list_imp.setTextColor(ContextCompat.getColor(context, R.color.colorImpPos)); }

        return(item);
    }

    public long getItemId(int position) {
        return llista_movs.get(position).id_mov;
    }

}
