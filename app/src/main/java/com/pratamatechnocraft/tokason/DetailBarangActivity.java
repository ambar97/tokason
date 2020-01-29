package com.pratamatechnocraft.tokason;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailBarangActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private SwipeRefreshLayout refreshDetailBarang;
    private RelativeLayout adaGambar, tidakAdaGambar;
    private TextView txtNamaBarangDetail, txtNamaKategoriDetailBarang, txtDetailHargaJual, txtDetailHargaBeli, txtDetailStok,txtDetailDeskripsi, hurufDepanBarangDetail;
    private CircleImageView fotoBarangDetail, fotoBarangDetail1;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/barang?api=barangdetail&kd_barang=";
    Intent intent;

    @SuppressLint("ResourceAsColor")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_barang );
        refreshDetailBarang = (SwipeRefreshLayout) findViewById( R.id.refreshDetailBarang );
        intent = getIntent();

        /*TEXT VIEW*/
        txtNamaBarangDetail = findViewById( R.id.txtNamaBarangDetail);
        txtNamaKategoriDetailBarang = findViewById( R.id.txtNamaKategoriDetailBarang);
        txtDetailHargaJual = findViewById( R.id.txtDetailHargaJual);
        txtDetailHargaBeli = findViewById( R.id.txtDetailHargaBeli);
        txtDetailStok = findViewById(R.id.txtDetailStok);
        hurufDepanBarangDetail = findViewById( R.id.hurufDepanBarangDetail );
        txtDetailDeskripsi = findViewById(R.id.txtDetailDeskripsi);

        /*FOTO*/
        adaGambar=findViewById( R.id.adaGambarDetailBarang );
        tidakAdaGambar=findViewById( R.id.tidakAdaGambarDetailBarang );
        fotoBarangDetail=findViewById( R.id.fotoDetailBarang );
        fotoBarangDetail1=findViewById( R.id.fotoDetailBarang1 );


        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_detailbarang );
        setSupportActionBar(ToolBarAtas2);
        this.setTitle("Data Barang");
        ToolBarAtas2.setSubtitle( "Detail Barang" );
        ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadDetail(intent.getStringExtra( "kdBarang" ));

        refreshDetailBarang.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDetail(intent.getStringExtra( "kdBarang" ));
            }
        } );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.icon_edit:
                Intent i = new Intent(DetailBarangActivity.this, FormBarangActivity.class );
                i.putExtra( "type", "edit" );
                i.putExtra( "kdBarang",intent.getStringExtra( "kdBarang" ) );
                startActivity(i);
                return true;
            case R.id.icon_hapus:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Yakin Ingin Menghapus Data Ini ??");
                alertDialogBuilder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteBarang(intent.getStringExtra( "kdBarang" ));
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
        loadDetail(intent.getStringExtra( "kdBarang" ));
    }


    private void loadDetail(String kdBarang){
        refreshDetailBarang.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+kdBarang,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject barangdetail = new JSONObject(response);
                            txtNamaBarangDetail.setText(  barangdetail.getString( "nama_barang" ));
                            txtNamaKategoriDetailBarang.setText( barangdetail.getString( "nama_kategori" ));
                            txtDetailHargaBeli.setText( "Rp. "+barangdetail.getString( "harga_beli" ) );
                            txtDetailHargaJual.setText( "Rp. "+barangdetail.getString( "harga_jual" ) );
                            txtDetailStok.setText(  barangdetail.getString( "stok" )  );
                            txtDetailDeskripsi.setText(barangdetail.getString("deskripsi"));
                            if (barangdetail.getString( "gambar_barang" ).equals( "" )){
                                adaGambar.setVisibility( View.GONE );
                                tidakAdaGambar.setVisibility( View.VISIBLE );
                                setTidakAdaGambar(barangdetail.getString( "nama_barang" ));
                            }else{
                                adaGambar.setVisibility( View.VISIBLE );
                                tidakAdaGambar.setVisibility( View.GONE );
                                Glide.with(DetailBarangActivity.this)
                                        // LOAD URL DARI INTERNET
                                        .load(baseUrl+String.valueOf( barangdetail.getString( "gambar_barang" )  ))
                                        // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                                        .into(fotoBarangDetail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailBarangActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshDetailBarang.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailBarangActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshDetailBarang.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailBarangActivity.this );
        requestQueue.add( stringRequest );
    }

    private void deleteBarang(String kdBarang){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+"api/barang?api=delete&kd_barang="+kdBarang,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            if (kode.equals("1")) {
                                finish();
                                Toast.makeText(DetailBarangActivity.this, "Berhasil Menghapus Barang", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(DetailBarangActivity.this, "Gagal Menghapus Barang", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailBarangActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                            refreshDetailBarang.setRefreshing( false );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailBarangActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshDetailBarang.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailBarangActivity.this );
        requestQueue.add( stringRequest );
    }

    private void setTidakAdaGambar(String namaDepan){
        hurufDepanBarangDetail.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (hurufDepanBarangDetail.getText().equals( "A" ) || hurufDepanBarangDetail.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(hurufDepanBarangDetail.getText().equals( "B" ) || hurufDepanBarangDetail.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(hurufDepanBarangDetail.getText().equals( "C" ) || hurufDepanBarangDetail.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(hurufDepanBarangDetail.getText().equals( "D" ) || hurufDepanBarangDetail.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(hurufDepanBarangDetail.getText().equals( "E" ) || hurufDepanBarangDetail.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(hurufDepanBarangDetail.getText().equals( "F" ) || hurufDepanBarangDetail.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(hurufDepanBarangDetail.getText().equals( "G" ) || hurufDepanBarangDetail.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(hurufDepanBarangDetail.getText().equals( "H" ) || hurufDepanBarangDetail.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(hurufDepanBarangDetail.getText().equals( "I" ) || hurufDepanBarangDetail.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(hurufDepanBarangDetail.getText().equals( "J" ) || hurufDepanBarangDetail.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(hurufDepanBarangDetail.getText().equals( "K" ) || hurufDepanBarangDetail.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(hurufDepanBarangDetail.getText().equals( "L" ) || hurufDepanBarangDetail.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(hurufDepanBarangDetail.getText().equals( "M" ) || hurufDepanBarangDetail.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(hurufDepanBarangDetail.getText().equals( "N" ) || hurufDepanBarangDetail.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(hurufDepanBarangDetail.getText().equals( "O" ) || hurufDepanBarangDetail.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(hurufDepanBarangDetail.getText().equals( "P" ) || hurufDepanBarangDetail.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(hurufDepanBarangDetail.getText().equals( "Q" ) || hurufDepanBarangDetail.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(hurufDepanBarangDetail.getText().equals( "R" ) || hurufDepanBarangDetail.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(hurufDepanBarangDetail.getText().equals( "S" ) || hurufDepanBarangDetail.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(hurufDepanBarangDetail.getText().equals( "T" ) || hurufDepanBarangDetail.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(hurufDepanBarangDetail.getText().equals( "U" ) || hurufDepanBarangDetail.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(hurufDepanBarangDetail.getText().equals( "V" ) || hurufDepanBarangDetail.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(hurufDepanBarangDetail.getText().equals( "W" ) || hurufDepanBarangDetail.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(hurufDepanBarangDetail.getText().equals( "X" ) || hurufDepanBarangDetail.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(hurufDepanBarangDetail.getText().equals( "Y" ) || hurufDepanBarangDetail.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(hurufDepanBarangDetail.getText().equals( "Z" ) || hurufDepanBarangDetail.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        fotoBarangDetail1.setImageResource(color);
    }
}



