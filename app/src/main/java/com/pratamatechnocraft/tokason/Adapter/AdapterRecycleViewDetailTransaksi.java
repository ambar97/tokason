package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.Model.ListItemDetailTransaksi;
import com.pratamatechnocraft.tokason.R;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterRecycleViewDetailTransaksi extends RecyclerView.Adapter<AdapterRecycleViewDetailTransaksi.ViewHolder> {

    private List<ListItemDetailTransaksi> listItemDetailTransaksis;
    private Context context;


    public AdapterRecycleViewDetailTransaksi(List<ListItemDetailTransaksi> listItemDetailTransaksis, Context context) {
        this.listItemDetailTransaksis = listItemDetailTransaksis;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_detail_transaksi,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        final ListItemDetailTransaksi listItemDetailTransaksi = listItemDetailTransaksis.get(position);

        int subTotal = Integer.parseInt(listItemDetailTransaksi.getQty()) * Integer.parseInt(listItemDetailTransaksi.getHarga());

        holder.txtNamaBarangDetailTransaksi.setText(listItemDetailTransaksi.getNamaBarang());
        holder.txtQtyDetailTransaksi.setText(listItemDetailTransaksi.getQty());
        holder.txtHargaDetailTransaksi.setText(formatter.format(Double.parseDouble(listItemDetailTransaksi.getHarga())));
        holder.txtTotalHargaDetailTransaksi.setText("Rp. "+formatter.format(Double.parseDouble( String.valueOf(subTotal))));

    }

    @Override
    public int getItemCount() {
        return listItemDetailTransaksis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaBarangDetailTransaksi, txtQtyDetailTransaksi, txtHargaDetailTransaksi, txtTotalHargaDetailTransaksi;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaBarangDetailTransaksi = (TextView) itemView.findViewById(R.id.txtNamaBarangDetailTransaksi);
            txtQtyDetailTransaksi = (TextView) itemView.findViewById(R.id.txtQtyDetailTransaksi);
            txtHargaDetailTransaksi = (TextView) itemView.findViewById(R.id.txtHargaDetailTransaksi);
            txtTotalHargaDetailTransaksi = (TextView) itemView.findViewById(R.id.txtTotalHargaDetailTransaksi);
        }
    }
}
