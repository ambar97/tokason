package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.InvoiceActivity;
import com.pratamatechnocraft.tokason.Model.ListItemTransaksi;
import com.pratamatechnocraft.tokason.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterRecycleViewDataTransaksi extends RecyclerView.Adapter<AdapterRecycleViewDataTransaksi.ViewHolder> implements Filterable {

    private List<ListItemTransaksi> listItemTransaksis;
    private List<ListItemTransaksi> listItemTransaksiFull;
    private Context context;
    private int type;
    private int totalHargaSemua;
    private TextView txtTotalPiutangAtauHutang;
    DecimalFormat formatter = new DecimalFormat("#,###,###");

    public AdapterRecycleViewDataTransaksi(List<ListItemTransaksi> listItemTransaksis, Context context, int type, TextView txtTotalPiutangAtauHutang) {
        this.listItemTransaksis = listItemTransaksis;
        listItemTransaksiFull = new ArrayList<>( listItemTransaksis );
        this.context = context;
        this.type=type;
        this.txtTotalPiutangAtauHutang=txtTotalPiutangAtauHutang;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_transaksi,parent,false);
        tes();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemTransaksi listItemTransaksi = listItemTransaksis.get(position);

        holder.txtNoInvoiceTransaksi.setText(listItemTransaksi.getNoInvoice());
        holder.txtTanggalTransaksi.setText(listItemTransaksi.getTanggalTransaksi());
        holder.txtTotalHargaTransaksi.setText("Rp. "+formatter.format(Double.parseDouble(listItemTransaksi.getTotalHarga())));

        holder.cardViewDataTransaksi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replace;
                if (type==0){
                    replace ="#PL";
                }else{
                    replace ="#PB";
                }
                Intent i = new Intent(context, InvoiceActivity.class);
                i.putExtra("done", false);
                i.putExtra("noInvoice", listItemTransaksi.getNoInvoice().replace( replace,"" ));
                i.putExtra("kdTransaksi", listItemTransaksi.getKdTransaksi().replace( replace,"" ));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemTransaksis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNoInvoiceTransaksi, txtTanggalTransaksi, txtTotalHargaTransaksi;
        public CardView cardViewDataTransaksi;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNoInvoiceTransaksi = (TextView) itemView.findViewById(R.id.txtNoInvoiceTransaksi);
            txtTanggalTransaksi = (TextView) itemView.findViewById(R.id.txtTanggalTransaksi);
            txtTotalHargaTransaksi = (TextView) itemView.findViewById(R.id.txtTotalHargaTransaksi);
            cardViewDataTransaksi = (CardView) itemView.findViewById(R.id.cardViewDataTransaksi);
        }
    }

    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ListItemTransaksi> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll( listItemTransaksiFull );
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ListItemTransaksi itemTransaksi : listItemTransaksiFull) {
                    if (itemTransaksi.getNoInvoice().toLowerCase().contains( filterPattern ) || itemTransaksi.getTanggalTransaksi().toLowerCase().contains( filterPattern )) {
                        filteredList.add( itemTransaksi );
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemTransaksis.clear();
            listItemTransaksis.addAll((List) filterResults.values );
            notifyDataSetChanged();
            if (listItemTransaksis.size()==0){
                totalHargaSemua=0;
                txtTotalPiutangAtauHutang.setText("Rp. " + formatter.format(Double.parseDouble(String.valueOf(totalHargaSemua))));
            }else {
                tes();
            }
        }
    };

    private void tes(){
        totalHargaSemua=0;
        for (ListItemTransaksi listItemTransaksi : listItemTransaksis) {
            totalHargaSemua = totalHargaSemua + Integer.parseInt(listItemTransaksi.getTotalHarga());

            txtTotalPiutangAtauHutang.setText("Rp. " + formatter.format(Double.parseDouble(String.valueOf(totalHargaSemua))));
        }
    }

}
