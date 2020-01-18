package com.pratamatechnocraft.tokason.Adapter;

import android.content.Context;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.pratamatechnocraft.tokason.BarangTransaksiActivity;
import com.pratamatechnocraft.tokason.DetailBarangActivity;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ListItemDataBarang;
import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycleViewDataBarang extends RecyclerView.Adapter<AdapterRecycleViewDataBarang.ViewHolder> implements Filterable {

    private List<ListItemDataBarang> listItemDataBarangs;
    private List<ListItemDataBarang> listItemDataBarangFull;
    private Context context;
    private Integer type, jenis_transaksi;
    private RecyclerView.Adapter adapter;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    ModelKeranjang modelKeranjang=null;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private androidx.appcompat.app.AlertDialog alertDialog1;

    public AdapterRecycleViewDataBarang(List<ListItemDataBarang> listItemDataBarangs, Context context, Integer type, Integer jenis_transaksi) {
        this.listItemDataBarangs = listItemDataBarangs;
        listItemDataBarangFull = new ArrayList<>( listItemDataBarangs );
        this.context = context;
        this.type = type;
        this.jenis_transaksi=jenis_transaksi;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_data_barang,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItemDataBarang listItemDataBarang = listItemDataBarangs.get(position);
        DecimalFormat formatter = new DecimalFormat("#,###,###");

        holder.txtNamaBarang.setText(listItemDataBarang.getNamaBarang());
        holder.txtStokBarang.setText("Stok : "+listItemDataBarang.getStokBarang());
        holder.txtHargaBarang.setText("Rp. "+formatter.format(Double.parseDouble(listItemDataBarang.getHargaBarang())));

        holder.cardViewDataBarang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type==0){
                    Intent i = new Intent(context, DetailBarangActivity.class);
                    i.putExtra("kdBarang", listItemDataBarang.getKdBarang());
                    context.startActivity(i);
                }else{
                    if (jenis_transaksi==0){
                        if (listItemDataBarang.getStokBarang().equals("0")){
                            alert("Peringatan !!", "Stok Kosong !!");
                        }else{
                            dbDataSourceKeranjang = new DBDataSourceKeranjang( context );
                            dbDataSourceKeranjang.open();
                            if(dbDataSourceKeranjang.cekKeranjang( listItemDataBarang.getKdBarang())==false){
                                modelKeranjang = dbDataSourceKeranjang.createModelKeranjang(
                                        listItemDataBarang.getKdBarang(),
                                        listItemDataBarang.getNamaBarang(),
                                        listItemDataBarang.getHargaBarang(),
                                        listItemDataBarang.getGambarBarang(),
                                        "1",
                                        listItemDataBarang.getStokBarang(),
                                        ""
                                );
                                ((BarangTransaksiActivity)context).finish();
                            }else{
                                ModelKeranjang modelKeranjang1 = dbDataSourceKeranjang.getKeranjang(listItemDataBarang.getKdBarang());
                                if (listItemDataBarang.getStokBarang().equals(String.valueOf(modelKeranjang1.getQty()))){
                                    alert("Peringatan !!", "Kuantitas Melebihi Batas Stok");
                                }else{
                                    dbDataSourceKeranjang.updateBarang( listItemDataBarang.getKdBarang(), modelKeranjang1.getQty(), modelKeranjang1.getCatatan() );
                                    ((BarangTransaksiActivity)context).finish();
                                }

                            }
                        }
                    }else{
                        dbDataSourceKeranjang = new DBDataSourceKeranjang( context );
                        dbDataSourceKeranjang.open();
                        if(dbDataSourceKeranjang.cekKeranjang( listItemDataBarang.getKdBarang())==false){
                            modelKeranjang = dbDataSourceKeranjang.createModelKeranjang(
                                    listItemDataBarang.getKdBarang(),
                                    listItemDataBarang.getNamaBarang(),
                                    listItemDataBarang.getHargaBarang(),
                                    listItemDataBarang.getGambarBarang(),
                                    "1",
                                    listItemDataBarang.getStokBarang(),
                                    ""
                            );
                            ((BarangTransaksiActivity)context).finish();
                        }else{
                            ModelKeranjang modelKeranjang1 = dbDataSourceKeranjang.getKeranjang(listItemDataBarang.getKdBarang());
                            dbDataSourceKeranjang.updateBarang( listItemDataBarang.getKdBarang(), modelKeranjang1.getQty(), modelKeranjang1.getCatatan() );
                            ((BarangTransaksiActivity)context).finish();
                        }
                    }
                }
            }
        });

        if (!listItemDataBarang.getGambarBarang().equals("")){
            holder.adaGambar.setVisibility( View.VISIBLE );
            holder.tidakAdaGambar.setVisibility( View.GONE );
            Glide.with(context)
                    // LOAD URL DARI INTERNET
                    .load(baseUrl+listItemDataBarang.getGambarBarang())
                    // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                    .into(holder.fotoDataBarang1);
        }else {
            holder.adaGambar.setVisibility( View.GONE );
            holder.tidakAdaGambar.setVisibility( View.VISIBLE );
        }

        String namaDepan=listItemDataBarang.getNamaBarang();
        holder.hurufDepanDataBarang.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (holder.hurufDepanDataBarang.getText().equals( "A" ) || holder.hurufDepanDataBarang.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "B" ) || holder.hurufDepanDataBarang.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "C" ) || holder.hurufDepanDataBarang.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "D" ) || holder.hurufDepanDataBarang.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "E" ) || holder.hurufDepanDataBarang.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "F" ) || holder.hurufDepanDataBarang.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "G" ) || holder.hurufDepanDataBarang.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "H" ) || holder.hurufDepanDataBarang.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "I" ) || holder.hurufDepanDataBarang.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "J" ) || holder.hurufDepanDataBarang.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "K" ) || holder.hurufDepanDataBarang.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "L" ) || holder.hurufDepanDataBarang.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "M" ) || holder.hurufDepanDataBarang.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "N" ) || holder.hurufDepanDataBarang.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "O" ) || holder.hurufDepanDataBarang.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "P" ) || holder.hurufDepanDataBarang.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "Q" ) || holder.hurufDepanDataBarang.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(holder.hurufDepanDataBarang.getText().equals( "R" ) || holder.hurufDepanDataBarang.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "S" ) || holder.hurufDepanDataBarang.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "T" ) || holder.hurufDepanDataBarang.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "U" ) || holder.hurufDepanDataBarang.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "V" ) || holder.hurufDepanDataBarang.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "W" ) || holder.hurufDepanDataBarang.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "X" ) || holder.hurufDepanDataBarang.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "Y" ) || holder.hurufDepanDataBarang.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(holder.hurufDepanDataBarang.getText().equals( "Z" ) || holder.hurufDepanDataBarang.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        holder.fotoDataBarang.setImageResource(color);
    }

    @Override
    public int getItemCount() {
        return listItemDataBarangs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaBarang, txtStokBarang, txtHargaBarang, hurufDepanDataBarang;
        public CardView cardViewDataBarang;
        public CircleImageView fotoDataBarang, fotoDataBarang1;
        public RelativeLayout adaGambar, tidakAdaGambar;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNamaBarang = (TextView) itemView.findViewById(R.id.txtNamaBarang);
            txtStokBarang = (TextView) itemView.findViewById(R.id.txtStokBarang);
            txtHargaBarang = (TextView) itemView.findViewById(R.id.txtHargaBarang);
            cardViewDataBarang = (CardView) itemView.findViewById(R.id.cardViewDataBarang);
            hurufDepanDataBarang = (TextView) itemView.findViewById(R.id.hurufDepanBarang);
            fotoDataBarang = (CircleImageView) itemView.findViewById( R.id.fotoDataBarang );
            fotoDataBarang1 = (CircleImageView) itemView.findViewById( R.id.fotoDataBarang1 );
            adaGambar = (RelativeLayout) itemView.findViewById( R.id.adaGambar );
            tidakAdaGambar = (RelativeLayout) itemView.findViewById( R.id.tidakAdaGambar );


        }
    }

    @Override
    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ListItemDataBarang> filteredList = new ArrayList<>(  );

            if (charSequence == null || charSequence.length()==0){
                filteredList.addAll( listItemDataBarangFull );
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ListItemDataBarang itemDataBarang : listItemDataBarangFull){
                    if (itemDataBarang.getNamaBarang().toLowerCase().contains( filterPattern )){
                        filteredList.add( itemDataBarang );
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemDataBarangs.clear();
            listItemDataBarangs.addAll((List) filterResults.values );
            notifyDataSetChanged();
        }
    };

    private void alert(String title, String message){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog1.dismiss();
            }
        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

}
