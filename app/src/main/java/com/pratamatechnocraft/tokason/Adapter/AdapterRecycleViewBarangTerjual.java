package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.Model.ListItemBarangTerjual;
import com.pratamatechnocraft.tokason.R;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterRecycleViewBarangTerjual extends RecyclerView.Adapter<AdapterRecycleViewBarangTerjual.ViewHolder> {

    private List<ListItemBarangTerjual> listItemBarangTerjuals;
    private Context context;


    public AdapterRecycleViewBarangTerjual(List<ListItemBarangTerjual> listItemBarangTerjuals, Context context) {
        this.listItemBarangTerjuals = listItemBarangTerjuals;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_barang_terjual,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        final ListItemBarangTerjual listItemBarangTerjual = listItemBarangTerjuals.get(position);

        int subTotal = Integer.parseInt(listItemBarangTerjual.getJmlTerjual()) * Integer.parseInt(listItemBarangTerjual.getHargaJual());
        holder.txtNoBarangTerjual.setText(String.valueOf(position+1));
        holder.txtNamaBarangTerjual.setText(listItemBarangTerjual.getNamaBarang());
        holder.txtJmlBarangTerjual.setText(listItemBarangTerjual.getJmlTerjual());
        holder.txtHargaBarangTerjual.setText("Rp. "+formatter.format(Double.parseDouble(listItemBarangTerjual.getHargaJual())));
        holder.txtSubTotalTerjual.setText("Rp. "+formatter.format(Double.parseDouble( String.valueOf(subTotal))));

    }

    @Override
    public int getItemCount() {
        return listItemBarangTerjuals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNoBarangTerjual,txtNamaBarangTerjual, txtJmlBarangTerjual, txtHargaBarangTerjual, txtSubTotalTerjual;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNoBarangTerjual = (TextView) itemView.findViewById(R.id.txtNoBarangTerjual);
            txtNamaBarangTerjual = (TextView) itemView.findViewById(R.id.txtNamaBarangTerjual);
            txtJmlBarangTerjual = (TextView) itemView.findViewById(R.id.txtJmlBarangTerjual);
            txtHargaBarangTerjual = (TextView) itemView.findViewById(R.id.txtHargaBarangTerjual);
            txtSubTotalTerjual = (TextView) itemView.findViewById(R.id.txtSubTotalTerjual);
        }
    }
}
