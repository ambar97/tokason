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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratamatechnocraft.tokason.DetailPelangganActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataPelanggan;
import com.pratamatechnocraft.tokason.Model.ModelPelangganPilih;
import com.pratamatechnocraft.tokason.PelangganTransaksiActivity;
import com.pratamatechnocraft.tokason.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycleViewDataPelanggan extends RecyclerView.Adapter<AdapterRecycleViewDataPelanggan.ViewHolder> implements Filterable {

    private List<ListItemDataPelanggan> listItemDataPelanggans;
    private List<ListItemDataPelanggan> listItemDataPelangganFull;
    private Context context;
    private Integer type;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    ModelPelangganPilih modelPelangganPilih=null;

    public AdapterRecycleViewDataPelanggan(List<ListItemDataPelanggan> listItemDataPelanggans, Context context, Integer type) {
        this.listItemDataPelanggans = listItemDataPelanggans;
        this.context = context;
        this.type = type;
        listItemDataPelangganFull = new ArrayList<>( listItemDataPelanggans );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_data_pelanggan,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemDataPelanggan listItemDataPelanggan = listItemDataPelanggans.get(position);

        holder.txtNamaPelanggan.setText(listItemDataPelanggan.getNamaPelanggan());
        holder.txtNoTelp.setText(listItemDataPelanggan.getNoTelp());
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(listItemDataPelanggan.getTgldiTambahkan());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatedDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
        holder.txtTglDitambahkan.setText(formatedDate);



        holder.cardViewDataPelanggan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type==0){
                    Intent i = new Intent(context, DetailPelangganActivity.class);
                    i.putExtra("kdPelanggan", listItemDataPelanggan.getKdPelanggan());
                    context.startActivity(i);
                }else{
                    dbDataSourceKeranjang = new DBDataSourceKeranjang(context);
                    dbDataSourceKeranjang.open();
                    if(dbDataSourceKeranjang.totalPelangganPilih()==false) {
                        modelPelangganPilih = dbDataSourceKeranjang.createModelPelangganPilih(
                                listItemDataPelanggan.getKdPelanggan(),
                                listItemDataPelanggan.getNamaPelanggan(),
                                listItemDataPelanggan.getNoTelp(),
                                listItemDataPelanggan.getAlamat(),
                                listItemDataPelanggan.getTgldiTambahkan()

                        );
                    }else{
                        dbDataSourceKeranjang.updatePelangganPilih(
                                listItemDataPelanggan.getKdPelanggan(),
                                listItemDataPelanggan.getNamaPelanggan(),
                                listItemDataPelanggan.getNoTelp(),
                                listItemDataPelanggan.getAlamat(),
                                listItemDataPelanggan.getTgldiTambahkan()
                        );
                    }
                    ((PelangganTransaksiActivity)context).finish();
                }


            }
        });


        String namaDepan=listItemDataPelanggan.getNamaPelanggan();
        holder.hurufDepanDataPelanggan.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (holder.hurufDepanDataPelanggan.getText().equals( "A" ) || holder.hurufDepanDataPelanggan.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "B" ) || holder.hurufDepanDataPelanggan.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "C" ) || holder.hurufDepanDataPelanggan.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "D" ) || holder.hurufDepanDataPelanggan.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "E" ) || holder.hurufDepanDataPelanggan.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "F" ) || holder.hurufDepanDataPelanggan.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "G" ) || holder.hurufDepanDataPelanggan.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "H" ) || holder.hurufDepanDataPelanggan.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "I" ) || holder.hurufDepanDataPelanggan.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "J" ) || holder.hurufDepanDataPelanggan.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "K" ) || holder.hurufDepanDataPelanggan.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "L" ) || holder.hurufDepanDataPelanggan.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "M" ) || holder.hurufDepanDataPelanggan.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "N" ) || holder.hurufDepanDataPelanggan.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "O" ) || holder.hurufDepanDataPelanggan.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "P" ) || holder.hurufDepanDataPelanggan.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "Q" ) || holder.hurufDepanDataPelanggan.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "R" ) || holder.hurufDepanDataPelanggan.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "S" ) || holder.hurufDepanDataPelanggan.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "T" ) || holder.hurufDepanDataPelanggan.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "U" ) || holder.hurufDepanDataPelanggan.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "V" ) || holder.hurufDepanDataPelanggan.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "W" ) || holder.hurufDepanDataPelanggan.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "X" ) || holder.hurufDepanDataPelanggan.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "Y" ) || holder.hurufDepanDataPelanggan.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(holder.hurufDepanDataPelanggan.getText().equals( "Z" ) || holder.hurufDepanDataPelanggan.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        holder.fotoDataPelanggan.setImageResource(color);
    }

    @Override
    public int getItemCount() {
        return listItemDataPelanggans.size();
    }

    @Override
    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ListItemDataPelanggan> filteredList = new ArrayList<>(  );

            if (charSequence == null || charSequence.length()==0){
                filteredList.addAll( listItemDataPelangganFull );
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ListItemDataPelanggan itemDataPelanggan : listItemDataPelangganFull){
                    if (itemDataPelanggan.getNamaPelanggan().toLowerCase().contains( filterPattern ) || itemDataPelanggan.getNoTelp().toLowerCase().contains( filterPattern )){
                        filteredList.add( itemDataPelanggan );
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemDataPelanggans.clear();
            listItemDataPelanggans.addAll((List) filterResults.values );
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaPelanggan, txtNoTelp, txtTglDitambahkan, hurufDepanDataPelanggan;
        public CardView cardViewDataPelanggan;
        public CircleImageView fotoDataPelanggan;
        public RelativeLayout tidakAdaGambarPelanggan;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaPelanggan = (TextView) itemView.findViewById(R.id.txtNamaPelanggan);
            txtNoTelp = (TextView) itemView.findViewById(R.id.txtNoTelp);
            txtTglDitambahkan = (TextView) itemView.findViewById(R.id.txtTglDitambahkan);
            cardViewDataPelanggan = (CardView) itemView.findViewById(R.id.cardViewDataPelanggan);
            hurufDepanDataPelanggan = (TextView) itemView.findViewById(R.id.hurufDepanPelanggan);
            fotoDataPelanggan = (CircleImageView) itemView.findViewById( R.id.fotoDataPelanggan );
            tidakAdaGambarPelanggan = (RelativeLayout) itemView.findViewById( R.id.tidakAdaGambarPelanggan );
        }
    }



}
