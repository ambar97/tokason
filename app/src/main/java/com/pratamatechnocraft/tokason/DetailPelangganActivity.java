package com.pratamatechnocraft.tokason;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPelangganActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private SwipeRefreshLayout refreshDetailPelanggan;
    private RelativeLayout  tidakAdaGambar;
    private TextView txtDetailNamaPelanggan, txtTgldiTambahkanDetailPelanggan, txtDetailNoTelp, txtDetailAlamat, hurufDepanPelangganDetail;
    private CircleImageView fotoPelangganDetail1;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/pelanggan?api=pelanggandetail&kd_pelanggan=";
    Intent intent;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_pelanggan );
        refreshDetailPelanggan = (SwipeRefreshLayout) findViewById( R.id.refreshDetailPelanggan );
        intent = getIntent();

        /*TEXT VIEW*/
        txtDetailAlamat = findViewById( R.id.txtDetailAlamatPelanggan);
        txtTgldiTambahkanDetailPelanggan = findViewById( R.id.txtTgldiTambahkanDetailPelanggan);
        txtDetailNoTelp = findViewById( R.id.txtDetailNoTelpPelanggan);
        txtDetailNamaPelanggan = findViewById( R.id.txtNamaDetailPelanggan);
        hurufDepanPelangganDetail = findViewById( R.id.hurufDepanPelangganDetail );

        /*FOTO*/
        tidakAdaGambar=findViewById( R.id.tidakAdaGambarDetailPelanggan );
        fotoPelangganDetail1=findViewById( R.id.fotoDetailPelanggan1 );


        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_detailpelanggan );
        setSupportActionBar(ToolBarAtas2);
        this.setTitle("Data Pelanggan");
        ToolBarAtas2.setSubtitle( "Detail Pelanggan" );
        ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadDetail(intent.getStringExtra( "kdPelanggan" ));

        refreshDetailPelanggan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDetail(intent.getStringExtra( "kdPelanggan" ));
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
                Intent i = new Intent(DetailPelangganActivity.this, FormPelangganActivity.class );
                i.putExtra( "type", "edit" );
                i.putExtra( "kdPelanggan",intent.getStringExtra( "kdPelanggan" ) );
                startActivity(i);
                return true;
            case R.id.icon_hapus:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Yakin Ingin Menghapus Data Ini ??");
                alertDialogBuilder.setPositiveButton("Iya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deletePelanggan(intent.getStringExtra( "kdPelanggan" ));
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
        loadDetail(intent.getStringExtra( "kdPelanggan" ));
    }

    private void loadDetail(String kdPelanggan){
        refreshDetailPelanggan.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+kdPelanggan,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONObject pelanggandetail = new JSONObject(response);
                        txtDetailNamaPelanggan.setText(  pelanggandetail.getString( "nama_pelanggan" ));
                        txtTgldiTambahkanDetailPelanggan.setText(pelanggandetail.getString( "tgl_ditambahkan" ));
                        txtDetailNoTelp.setText( pelanggandetail.getString( "no_telp_pelanggan" ) );
                        txtDetailAlamat.setText(  pelanggandetail.getString( "alamat_pelanggan" )  );
                        setTidakAdaGambar(pelanggandetail.getString( "nama_pelanggan" ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailPelangganActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                    refreshDetailPelanggan.setRefreshing( false );
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DetailPelangganActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    refreshDetailPelanggan.setRefreshing( false );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailPelangganActivity.this );
        requestQueue.add( stringRequest );
    }

    private void deletePelanggan(String kdPelanggan){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+"api/pelanggan?api=delete&kd_pelanggan="+kdPelanggan,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String kode = jsonObject.getString("kode");
                        if (kode.equals("1")) {
                            finish();
                            Toast.makeText(DetailPelangganActivity.this, "Berhasil Menghapus Pelanggan", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(DetailPelangganActivity.this, "Gagal Menghapus Pelanggan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailPelangganActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                    refreshDetailPelanggan.setRefreshing( false );
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DetailPelangganActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                    refreshDetailPelanggan.setRefreshing( false );
                }
            }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailPelangganActivity.this );
        requestQueue.add( stringRequest );
    }

    private void setTidakAdaGambar(String namaDepan){
        hurufDepanPelangganDetail.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (hurufDepanPelangganDetail.getText().equals( "A" ) || hurufDepanPelangganDetail.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "B" ) || hurufDepanPelangganDetail.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "C" ) || hurufDepanPelangganDetail.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "D" ) || hurufDepanPelangganDetail.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "E" ) || hurufDepanPelangganDetail.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "F" ) || hurufDepanPelangganDetail.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "G" ) || hurufDepanPelangganDetail.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "H" ) || hurufDepanPelangganDetail.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "I" ) || hurufDepanPelangganDetail.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "J" ) || hurufDepanPelangganDetail.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "K" ) || hurufDepanPelangganDetail.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "L" ) || hurufDepanPelangganDetail.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "M" ) || hurufDepanPelangganDetail.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "N" ) || hurufDepanPelangganDetail.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "O" ) || hurufDepanPelangganDetail.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "P" ) || hurufDepanPelangganDetail.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "Q" ) || hurufDepanPelangganDetail.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(hurufDepanPelangganDetail.getText().equals( "R" ) || hurufDepanPelangganDetail.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "S" ) || hurufDepanPelangganDetail.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "T" ) || hurufDepanPelangganDetail.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "U" ) || hurufDepanPelangganDetail.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "V" ) || hurufDepanPelangganDetail.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "W" ) || hurufDepanPelangganDetail.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "X" ) || hurufDepanPelangganDetail.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "Y" ) || hurufDepanPelangganDetail.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(hurufDepanPelangganDetail.getText().equals( "Z" ) || hurufDepanPelangganDetail.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        fotoPelangganDetail1.setImageResource(color);
    }
}
