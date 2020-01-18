package com.pratamatechnocraft.tokason.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.google.android.material.textfield.TextInputLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycleViewKeranjang extends RecyclerView.Adapter<AdapterRecycleViewKeranjang.ViewHolder>  {

    private ArrayList<ModelKeranjang> modelKeranjangs;
    private Context context;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private int subTotal, jenis_transaksi;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    private TextView jmlItem,totalHarga;
    private int vtotalHarga;
    private int vjmlItem;
    private int qtyTemp;
    AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;
    private ImageButton imageButtonPlusQty,imageButtonMinusQty;
    private EditText qtyDialog;
    private EditText inputCatatanPerBarang;
    private TextInputLayout inputLayoutCatatanPerBarang;
    private Button buttonBatalDialogQty,buttonSimpanDialogQty;
    private androidx.appcompat.app.AlertDialog alertDialog1;

    public AdapterRecycleViewKeranjang(ArrayList<ModelKeranjang> modelKeranjangs, Context context, TextView jmlItem, TextView totalHarga,Integer jenis_transaksi) {
        this.modelKeranjangs = modelKeranjangs;
        this.context = context;
        this.jmlItem = jmlItem;
        this.totalHarga =totalHarga;
        this.jenis_transaksi=jenis_transaksi;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_data_barang_dikeranjang,parent,false);
        jmlItem.setText( String.valueOf( vjmlItem ) );
        totalHarga.setText( String.valueOf( vtotalHarga ) );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ModelKeranjang modelKeranjang = modelKeranjangs.get(position);
        final DecimalFormat formatter = new DecimalFormat("#,###,###");

        subTotal=0;
        subTotal=modelKeranjang.getHargaBarang() * modelKeranjang.getQty();

        holder.txtNamaBarangdiKeranjang.setText(modelKeranjang.getNamaBrang());
        holder.txtHargaBarangdiKeranjang.setText(formatter.format(Double.parseDouble(String.valueOf( modelKeranjang.getHargaBarang()))));
        holder.txtQTYBarangdiKeranjang.setText(String.valueOf( modelKeranjang.getQty()));
        holder.txtSubTotalBarangdiKeranjang.setText( formatter.format(Double.parseDouble(String.valueOf( subTotal ))));


        holder.cardViewDataBarangdiKeranjang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyTemp=modelKeranjang.getQty();
                dialog = new AlertDialog.Builder(context).create();
                inflater = dialog.getLayoutInflater();
                dialogView = inflater.inflate(R.layout.dialog_keranjang_qty, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Kuantitas & Catatan");

                inputCatatanPerBarang = dialogView.findViewById(R.id.inputCatatanPerBarang);

                imageButtonMinusQty = dialogView.findViewById( R.id.imageButtonMinusQty );
                imageButtonPlusQty = dialogView.findViewById( R.id.imageButtonPlusQty );

                buttonBatalDialogQty = dialogView.findViewById( R.id.buttonBatalDialogQty );
                buttonSimpanDialogQty = dialogView.findViewById( R.id.buttonSimpanDialogQty );

                qtyDialog = dialogView.findViewById( R.id.qtyDialog );

                inputCatatanPerBarang.setText(String.valueOf(modelKeranjang.getCatatan()));

                qtyDialog.setText( String.valueOf(modelKeranjang.getQty()) );

                imageButtonMinusQty.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (qtyTemp>1) {
                            qtyTemp = qtyTemp - 1;
                            qtyDialog.setText( String.valueOf( qtyTemp ) );
                        }
                    }
                } );

                imageButtonPlusQty.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(jenis_transaksi==0){
                            if (qtyTemp<modelKeranjang.getStok()){
                                qtyTemp=qtyTemp+1;
                                qtyDialog.setText( String.valueOf(qtyTemp) );
                            }
                        }else{
                            qtyTemp=qtyTemp+1;
                            qtyDialog.setText( String.valueOf(qtyTemp) );
                        }
                    }
                } );

                buttonBatalDialogQty.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                } );

                buttonSimpanDialogQty.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbDataSourceKeranjang = new DBDataSourceKeranjang( context );
                        dbDataSourceKeranjang.open();
                        dbDataSourceKeranjang.updateBarangTypeDua(modelKeranjang.getKdBarang(),qtyTemp, inputCatatanPerBarang.getText().toString().trim());
                        vjmlItem=0;
                        vtotalHarga=0;
                        modelKeranjang.setQty( qtyTemp );
                        modelKeranjang.setCatatan( inputCatatanPerBarang.getText().toString().trim());
                        notifyItemChanged( position );
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                } );

                dialog.show();
            }
        });

        holder.btnHapusKeranjang.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Yakin Ingin Menghapus Data Ini ??");
                alertDialogBuilder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                dbDataSourceKeranjang = new DBDataSourceKeranjang( context );
                                dbDataSourceKeranjang.open();
                                dbDataSourceKeranjang.deleteBarang( modelKeranjang.getKdKeranjang() );
                                vjmlItem=0;
                                vtotalHarga=0;
                                modelKeranjangs.remove( position );
                                notifyItemRemoved( position );
                                notifyDataSetChanged();
                                if(modelKeranjangs.size()==0){
                                    vjmlItem=0;
                                    vtotalHarga=0;
                                    totalHarga.setText( "Rp. "+formatter.format(Double.parseDouble(String.valueOf( vtotalHarga ) )));
                                    jmlItem.setText( String.valueOf( vjmlItem ) );
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog1.dismiss();
                    }
                });

                alertDialog1 = alertDialogBuilder.create();
                alertDialog1.show();
            }
        } );

        Log.d( "TAG", "onBindViewHolder: "+modelKeranjangs.size() );
        if(modelKeranjangs.size()!=0){
            vjmlItem=vjmlItem+modelKeranjang.getQty();
            vtotalHarga=vtotalHarga+subTotal;
            totalHarga.setText( "Rp. "+formatter.format(Double.parseDouble(String.valueOf( vtotalHarga ) )));
            jmlItem.setText( String.valueOf( vjmlItem ) );
        }

        if (!modelKeranjang.getUrlGambarBarang().equals("")){
            holder.adaGambar.setVisibility( View.VISIBLE );
            holder.tidakAdaGambar.setVisibility( View.GONE );
            Glide.with(context)
                    // LOAD URL DARI INTERNET
                    .load(baseUrl+modelKeranjang.getUrlGambarBarang())
                    // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                    .into(holder.fotoDataBarangdiKeranjang1);
        }else {
            holder.adaGambar.setVisibility( View.GONE );
            holder.tidakAdaGambar.setVisibility( View.VISIBLE );
        }
        String namaDepan=modelKeranjang.getNamaBrang();
        holder.hurufDepanBarangdiKeranjang.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (holder.hurufDepanBarangdiKeranjang.getText().equals( "A" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "B" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "C" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "D" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "E" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "F" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "G" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "H" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "I" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "J" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "K" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "L" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "M" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "N" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "O" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "P" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "Q" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "R" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "S" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "T" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "U" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "V" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "W" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "X" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "Y" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(holder.hurufDepanBarangdiKeranjang.getText().equals( "Z" ) || holder.hurufDepanBarangdiKeranjang.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        holder.fotoDataBarangdiKeranjang.setImageResource(color);
    }

    @Override
    public int getItemCount() {
        return modelKeranjangs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNamaBarangdiKeranjang, txtQTYBarangdiKeranjang, txtHargaBarangdiKeranjang, txtSubTotalBarangdiKeranjang,hurufDepanBarangdiKeranjang;
        public CardView cardViewDataBarangdiKeranjang;
        public CircleImageView fotoDataBarangdiKeranjang1, fotoDataBarangdiKeranjang;
        public RelativeLayout adaGambar, tidakAdaGambar;
        public ImageButton btnHapusKeranjang;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaBarangdiKeranjang = (TextView) itemView.findViewById(R.id.txtNamaBarangdiKeranjang);
            txtHargaBarangdiKeranjang = (TextView) itemView.findViewById(R.id.txtHargaBarangdiKeranjang);
            txtQTYBarangdiKeranjang = (TextView) itemView.findViewById(R.id.txtQTYBarangdiKeranjang);
            txtSubTotalBarangdiKeranjang = (TextView) itemView.findViewById( R.id.txtSubTotalBarangdiKeranjang );
            cardViewDataBarangdiKeranjang = (CardView) itemView.findViewById(R.id.cardViewDataBarangdiKeranjang);
            hurufDepanBarangdiKeranjang = (TextView) itemView.findViewById(R.id.hurufDepanBarangdiKeranjang);
            fotoDataBarangdiKeranjang = (CircleImageView) itemView.findViewById( R.id.fotoDataBarangdiKeranjang );
            fotoDataBarangdiKeranjang1 = (CircleImageView) itemView.findViewById( R.id.fotoDataBarangdiKeranjang1 );
            adaGambar = (RelativeLayout) itemView.findViewById( R.id.adaGambar );
            tidakAdaGambar = (RelativeLayout) itemView.findViewById( R.id.tidakAdaGambar );
            btnHapusKeranjang = (ImageButton) itemView.findViewById( R.id.btnHapusKeranjang );
        }
    }
}
