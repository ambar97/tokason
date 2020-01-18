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

import com.pratamatechnocraft.tokason.DetailBiayaActivity;
import com.pratamatechnocraft.tokason.Model.ListItemBiaya;
import com.pratamatechnocraft.tokason.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecycleViewDataBiaya extends RecyclerView.Adapter<AdapterRecycleViewDataBiaya.ViewHolder> implements Filterable {

    private List<ListItemBiaya> listItemBiayas;
    private List<ListItemBiaya> listItemBiayaFull;
    private Context context;
    private Integer jenisBiaya;

    public AdapterRecycleViewDataBiaya(List<ListItemBiaya> listItemBiayas, Context context, Integer jenisBiaya) {
        this.listItemBiayas = listItemBiayas;
        listItemBiayaFull = new ArrayList<>( listItemBiayas );
        this.context = context;
        this.jenisBiaya = jenisBiaya;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_biaya,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemBiaya listItemBiaya = listItemBiayas.get(position);

        holder.txtNamaBiaya.setText(listItemBiaya.getNamaBiaya());
        holder.txtTanggalBiaya.setText(listItemBiaya.getTanggalBiaya());
        holder.txtJmlBiaya.setText(listItemBiaya.getJmlBiaya());

        holder.cardViewDataBiaya.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailBiayaActivity.class);
                i.putExtra("kdBiaya", listItemBiaya.getKdBiaya());
                if (jenisBiaya==0){
                    i.putExtra("jenisBiaya", "0");
                }else{
                    i.putExtra("jenisBiaya", "1");
                }
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemBiayas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaBiaya, txtTanggalBiaya, txtJmlBiaya;
        public CardView cardViewDataBiaya;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaBiaya = (TextView) itemView.findViewById(R.id.txtNamaBiaya);
            txtTanggalBiaya = (TextView) itemView.findViewById(R.id.txtTanggalBiaya);
            txtJmlBiaya = (TextView) itemView.findViewById(R.id.txtJmlBiaya);
            cardViewDataBiaya = (CardView) itemView.findViewById(R.id.cardViewDataBiaya);
        }
    }

    @Override
    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ListItemBiaya> filteredList = new ArrayList<>(  );

            if (charSequence == null || charSequence.length()==0){
                filteredList.addAll( listItemBiayaFull );
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ListItemBiaya itemBiaya : listItemBiayaFull){
                    if (itemBiaya.getNamaBiaya().toLowerCase().contains( filterPattern )){
                        filteredList.add( itemBiaya );
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemBiayas.clear();
            listItemBiayas.addAll((List) filterResults.values );
            notifyDataSetChanged();
        }
    };

}
