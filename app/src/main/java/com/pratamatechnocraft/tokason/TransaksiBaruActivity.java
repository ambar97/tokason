package com.pratamatechnocraft.tokason;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.AdapterPagerTransaksiBaru;
import com.pratamatechnocraft.tokason.Adapter.DBDataSourceKeranjang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ModelKeranjang;
import com.pratamatechnocraft.tokason.Model.ModelPelangganPilih;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransaksiBaruActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int[] layouts;
    private AdapterPagerTransaksiBaru adapterPagerTransaksiBaru;

    private SwipeRefreshLayout refreshTransaksiEdit;
    private LinearLayout dots_layout;
    private ImageView[] dots;
    private Button btnLanjut,btnKembali;
    private AlertDialog alertDialog;
    private Intent intent;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    private androidx.appcompat.app.AlertDialog alertDialog1;
    private BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL_EDIT = "api/transaksi?api=transaksidetail&kd_transaksi=";
    ModelKeranjang modelKeranjang=null;
    ModelPelangganPilih modelPelangganPilih=null;
    private boolean statusLoadEdit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transaksi_baru );
        intent = getIntent();
        refreshTransaksiEdit = (SwipeRefreshLayout) findViewById(R.id.refreshTransaksiEdit);
        dbDataSourceKeranjang = new DBDataSourceKeranjang( this );
        dbDataSourceKeranjang.open();

        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_transaksi_baru);
        setSupportActionBar(ToolBarAtas2);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intent.getStringExtra("form").equals("1")){
            loadEditTransaksi();
        }

        mViewPager = findViewById( R.id.viewPager );
        if (intent.getStringExtra( "type" ).equals( "0" )){
            layouts = new int[]{R.layout.fragment_transaksi_baru_dua, R.layout.fragment_transaksi_baru_tiga};
            if (intent.getStringExtra( "form" ).equals( "0" )) {
                this.setTitle("Transaksi Penjualan Baru");
                adapterPagerTransaksiBaru = new AdapterPagerTransaksiBaru(layouts, this, 0 , 0, null, "0","0",null);
                refreshTransaksiEdit.setEnabled(false);
            }else{
                this.setTitle("Edit Transaksi Penjualan");
                ToolBarAtas2.setSubtitle("No Invoice :  #PL"+intent.getStringExtra("noInvoice"));
                adapterPagerTransaksiBaru = new AdapterPagerTransaksiBaru(layouts, this, 0, 1, intent.getStringExtra("kdTransaksi"), intent.getStringExtra("pajak"), intent.getStringExtra("diskon"),intent.getStringExtra("statusTransaksi"));
                ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
            }
        }else{
            layouts = new int[]{R.layout.fragment_transaksi_baru_dua, R.layout.fragment_transaksi_baru_tiga};
            if (intent.getStringExtra( "form" ).equals( "0" )) {
                this.setTitle("Transaksi Pembelian Baru");
                refreshTransaksiEdit.setEnabled(false);
                adapterPagerTransaksiBaru = new AdapterPagerTransaksiBaru( layouts,this,1, 0, null ,"0","0",null);
            }else{
                this.setTitle("Edit Transaksi Pembelian");
                ToolBarAtas2.setSubtitle("No Invoice :  #PB"+intent.getStringExtra("noInvoice"));
                adapterPagerTransaksiBaru = new AdapterPagerTransaksiBaru( layouts,this,1, 1, intent.getStringExtra("kdTransaksi"), intent.getStringExtra("pajak"), intent.getStringExtra("diskon"),intent.getStringExtra("statusTransaksi"));
                ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
            }
        }

        mViewPager.setAdapter( adapterPagerTransaksiBaru );

        dots_layout = findViewById( R.id.dotsLayouts );
        btnLanjut = findViewById( R.id.lanjut );
        btnKembali = findViewById( R.id.kembali );

        btnKembali.setVisibility( View.GONE );

        btnLanjut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem()==1){
                    if (dbDataSourceKeranjang.totalKeranjang()==false){
                        alert("Peringatan !!","Barang kosong");
                    }else{
                        mViewPager.setCurrentItem( mViewPager.getCurrentItem()+1 );
                    }
                }else{
                    mViewPager.setCurrentItem( mViewPager.getCurrentItem()+1 );
                }
            }
        } );

        btnKembali.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem( mViewPager.getCurrentItem()-1 );
            }
        } );
        createDots( 0 );

        mViewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                createDots( i );
                if (intent.getStringExtra( "type" ).equals( "0" )) {
                    if(i==0) {
                        btnKembali.setVisibility( View.GONE );
                        btnLanjut.setVisibility( View.VISIBLE );
                    }else if(i==2){
                        if (dbDataSourceKeranjang.totalKeranjang()==false){
                            alert("Peringatan !!","Barang kosong");
                            mViewPager.setCurrentItem( 1 );
                        }else{
                            btnLanjut.setVisibility( View.GONE );
                            btnKembali.setVisibility( View.VISIBLE );
                        }
                    }
                }else{
                    if (i==0){
                        btnKembali.setVisibility( View.GONE );
                        btnLanjut.setVisibility( View.VISIBLE );
                    }else if(i==1){
                        if (dbDataSourceKeranjang.totalKeranjang()==false){
                            alert("Peringatan !!","Barang kosong");
                            mViewPager.setCurrentItem( 0 );
                        }else{
                            btnLanjut.setVisibility( View.GONE );
                            btnKembali.setVisibility( View.VISIBLE );
                        }
                    }
                }
                adapterPagerTransaksiBaru.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        } );

        refreshTransaksiEdit.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEditTransaksi();
            }
        });

    }

    private void createDots(int current_position){
        if (dots_layout!=null)
            dots_layout.removeAllViews();

        dots=new  ImageView[layouts.length];
        for (int i=0;i<layouts.length;i++){
            dots[i] = new ImageView( this );
            if (i==current_position){
                dots[i].setImageDrawable( ContextCompat.getDrawable( this, R.drawable.active_dots ) );
            }else{
                dots[i].setImageDrawable( ContextCompat.getDrawable( this, R.drawable.inactive_dots ) );
            }

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT );
            params.setMargins( 4,0,4,0 );
            dots_layout.addView( dots[i], params );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Yakin Ingin Keluar ??");
                alertDialogBuilder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                TransaksiBaruActivity.super.onBackPressed();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterPagerTransaksiBaru.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem()!=0){
            mViewPager.setCurrentItem( mViewPager.getCurrentItem()-1 );
        }else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Yakin Ingin Keluar ??");
            alertDialogBuilder.setPositiveButton("Iya",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            TransaksiBaruActivity.super.onBackPressed();
                        }
                    });

            alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void alert(String title, String message){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    private void alertLoadEdit(String title, String message){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Coba Lagi !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                loadEditTransaksi();
                alertDialog1.dismiss();
            }
        });

        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

    public void loadEditTransaksi(){
        refreshTransaksiEdit.setEnabled(true);
        refreshTransaksiEdit.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL_EDIT+intent.getStringExtra("kdTransaksi"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject transaksi = new JSONObject(response);
                            Log.d("", "onResponse: "+transaksi.getString("kd_pelanggan"));
                            modelPelangganPilih = dbDataSourceKeranjang.createModelPelangganPilih(
                                    transaksi.getString("kd_pelanggan"),
                                    transaksi.getString("nama_pelanggan"),
                                    transaksi.getString("no_telp_pelanggan"),
                                    transaksi.getString("alamat_pelanggan"),
                                    transaksi.getString("tgl_ditambahkan")

                            );
                            if (statusLoadEdit==false){
                                JSONArray transaksiDetail = transaksi.getJSONArray("detailinvoice");
                                for (int i = 0; i<transaksiDetail.length(); i++){
                                    JSONObject transaksiDetailJSONObject = transaksiDetail.getJSONObject( i );
                                    String harga;
                                    if (transaksi.getString( "jenis_transaksi" ).equals( "0" )){
                                        harga=transaksiDetailJSONObject.getString( "harga_jual_detail" );
                                    }else{
                                        harga=transaksiDetailJSONObject.getString( "harga_beli_detail" );
                                    }

                                    modelKeranjang = dbDataSourceKeranjang.createModelKeranjang(
                                            transaksiDetailJSONObject.getString("kd_barang"),
                                            transaksiDetailJSONObject.getString("nama_barang"),
                                            harga,
                                            transaksiDetailJSONObject.getString("gambar_barang"),
                                            transaksiDetailJSONObject.getString("qty"),
                                            transaksiDetailJSONObject.getString("stok"),
                                            transaksiDetailJSONObject.getString("catatan")
                                    );

                                    dbDataSourceKeranjang.close();
                                    dbDataSourceKeranjang.open();
                                }
                            }

                            adapterPagerTransaksiBaru.notifyDataSetChanged();

                            refreshTransaksiEdit.setEnabled(false);

                            statusLoadEdit=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            alertLoadEdit("Gagal Memuat", "Periksa koneksi & coba lagi");
                            Toast.makeText(TransaksiBaruActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshTransaksiEdit.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertLoadEdit("Gagal Memuat", "Periksa koneksi & coba lagi");
                        Toast.makeText(TransaksiBaruActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshTransaksiEdit.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( TransaksiBaruActivity.this );
        requestQueue.add( stringRequest );
    }
}
