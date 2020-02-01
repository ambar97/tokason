package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.Model.ListItemBiaya;
import com.pratamatechnocraft.tokason.R;

import java.util.List;

public class AdapterRecycleViewBiayaPengeluaran extends RecyclerView.Adapter<AdapterRecycleViewBiayaPengeluaran.ViewHolder> {

    private List<ListItemBiaya> listItemBiayas;
    private Context context;

    public AdapterRecycleViewBiayaPengeluaran(List<ListItemBiaya> listItemBiayas, Context context) {
        this.listItemBiayas = listItemBiayas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_biaya_pengeluaran,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemBiaya listItemBiaya = listItemBiayas.get(position);

        holder.txtNamaBiayaLaba.setText(listItemBiaya.getNamaBiaya());
        holder.txtJumlahBiayaLaba.setText(listItemBiaya.getJmlBiaya());
    }

    @Override
    public int getItemCount() {
        return listItemBiayas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaBiayaLaba, txtJumlahBiayaLaba;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaBiayaLaba = (TextView) itemView.findViewById(R.id.txtNamaBiayaLaba);
            txtJumlahBiayaLaba = (TextView) itemView.findViewById(R.id.txtJumlahBiayaLaba);
        }
    }
}
