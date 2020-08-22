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

import android.util.Log;
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

public class DetailUserActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private SwipeRefreshLayout refreshDetailUser;
    private RelativeLayout adaGambar, tidakAdaGambar;
    private TextView txtDetailNamaUser, txtDetailLevelUser, txtDetailNoTelp, txtDetailAlamat, hurufDepanUserDetail;
    private CircleImageView fotoUserDetail, fotoUserDetail1;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/user?api=profile&kd_user=";
    Intent intent;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_user );
        refreshDetailUser = (SwipeRefreshLayout) findViewById( R.id.refreshDetailUser );
        intent = getIntent();

        /*TEXT VIEW*/
        txtDetailAlamat = findViewById( R.id.txtDetailAlamatUser);
        txtDetailLevelUser = findViewById( R.id.txtLevelUserDetailUser);
        txtDetailNoTelp = findViewById( R.id.txtDetailNoTelpUser);
        txtDetailNamaUser = findViewById( R.id.txtNamaDetailUser);
        hurufDepanUserDetail = findViewById( R.id.hurufDepanUserDetail );

        /*FOTO*/
        adaGambar=findViewById( R.id.adaGambarDetailUser );
        tidakAdaGambar=findViewById( R.id.tidakAdaGambarDetailUser );
        fotoUserDetail=findViewById( R.id.fotoDetailUser );
        fotoUserDetail1=findViewById( R.id.fotoDetailUser1 );


        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbar_detailuser );
        setSupportActionBar(ToolBarAtas2);
        this.setTitle("Data User");
        ToolBarAtas2.setSubtitle( "Detail User" );
        ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadDetail(intent.getStringExtra( "kdUser" ));

        refreshDetailUser.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDetail(intent.getStringExtra( "kdUser" ));
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
                Intent i = new Intent(DetailUserActivity.this, FormUserActivity.class );
                i.putExtra( "type", "edit" );
                i.putExtra( "kdUser",intent.getStringExtra( "kdUser" ) );
                startActivity(i);
                return true;
            case R.id.icon_hapus:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Yakin Ingin Menghapus Data Ini ??");
                        alertDialogBuilder.setPositiveButton("Iya",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        deleteUser(intent.getStringExtra( "kdUser" ));
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
        loadDetail(intent.getStringExtra( "kdUser" ));
    }

    private void loadDetail(String kdUser){
        refreshDetailUser.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+kdUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject userdetail = new JSONObject(response);
                            txtDetailNamaUser.setText(  userdetail.getString( "nama_depan" )+" "+userdetail.getString( "nama_belakang" ));
                            if(Integer.parseInt( userdetail.getString( "level_user" ) )==0){
                                txtDetailLevelUser.setText("Owner");
                            }else if(Integer.parseInt( userdetail.getString( "level_user" ) )==1){
                                txtDetailLevelUser.setText("Kasir");
                            }
                            txtDetailNoTelp.setText( userdetail.getString( "no_telp" ) );
                            txtDetailAlamat.setText(  userdetail.getString( "alamat" )  );
                            if (userdetail.getString( "foto" ).equals( "" )){
                                adaGambar.setVisibility( View.GONE );
                                tidakAdaGambar.setVisibility( View.VISIBLE );
                                setTidakAdaGambar(userdetail.getString( "nama_depan" ));
                            }else{
                                adaGambar.setVisibility( View.VISIBLE );
                                tidakAdaGambar.setVisibility( View.GONE );
                                Glide.with(DetailUserActivity.this)
                                        // LOAD URL DARI INTERNET
                                        .load(baseUrl+String.valueOf( userdetail.getString( "foto" )  ))
                                        // LOAD GAMBAR AWAL SEBELUM GAMBAR UTAMA MUNCUL, BISA DARI LOKAL DAN INTERNET
                                        .into(fotoUserDetail);
                            }
                        } catch (JSONException e) {
                            Log.d("Detail", "Error : ", e);
                            Toast.makeText(DetailUserActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshDetailUser.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailUserActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshDetailUser.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailUserActivity.this );
        requestQueue.add( stringRequest );
    }

    private void deleteUser(String kdUser){
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+"api/user?api=delete&kd_user="+kdUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String kode = jsonObject.getString("kode");
                            if (kode.equals("1")) {
                                /*JSONObject data = jsonObject.getJSONObject("data");
                                String idUser = data.getString("kd_user").trim();

                                Intent i = new Intent(FormUserActivity.this, DetailUserActivity.class);
                                i.putExtra( "idUser", idUser );
                                startActivity(i);*/
                                finish();
                                Toast.makeText(DetailUserActivity.this, "Berhasil Menghapus User", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(DetailUserActivity.this, "Gagal Menghapus User", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailUserActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshDetailUser.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailUserActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshDetailUser.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( DetailUserActivity.this );
        requestQueue.add( stringRequest );
    }

    private void setTidakAdaGambar(String namaDepan){
        hurufDepanUserDetail.setText(namaDepan.substring( 0,1 ));

        int color=0;

        if (hurufDepanUserDetail.getText().equals( "A" ) || hurufDepanUserDetail.getText().equals( "a" )){
            color=R.color.amber_500;
        }else if(hurufDepanUserDetail.getText().equals( "B" ) || hurufDepanUserDetail.getText().equals( "b" )){
            color=R.color.blue_500;
        }else if(hurufDepanUserDetail.getText().equals( "C" ) || hurufDepanUserDetail.getText().equals( "c" )){
            color=R.color.blue_grey_500;
        }else if(hurufDepanUserDetail.getText().equals( "D" ) || hurufDepanUserDetail.getText().equals( "d" )){
            color=R.color.brown_500;
        }else if(hurufDepanUserDetail.getText().equals( "E" ) || hurufDepanUserDetail.getText().equals( "e" )){
            color=R.color.cyan_500;
        }else if(hurufDepanUserDetail.getText().equals( "F" ) || hurufDepanUserDetail.getText().equals( "f" )){
            color=R.color.deep_orange_500;
        }else if(hurufDepanUserDetail.getText().equals( "G" ) || hurufDepanUserDetail.getText().equals( "g" )){
            color=R.color.deep_purple_500;
        }else if(hurufDepanUserDetail.getText().equals( "H" ) || hurufDepanUserDetail.getText().equals( "h" )){
            color=R.color.green_500;
        }else if(hurufDepanUserDetail.getText().equals( "I" ) || hurufDepanUserDetail.getText().equals( "i" )){
            color=R.color.grey_500;
        }else if(hurufDepanUserDetail.getText().equals( "J" ) || hurufDepanUserDetail.getText().equals( "j" )){
            color=R.color.indigo_500;
        }else if(hurufDepanUserDetail.getText().equals( "K" ) || hurufDepanUserDetail.getText().equals( "k" )){
            color=R.color.teal_500;
        }else if(hurufDepanUserDetail.getText().equals( "L" ) || hurufDepanUserDetail.getText().equals( "l" )){
            color=R.color.lime_500;
        }else if(hurufDepanUserDetail.getText().equals( "M" ) || hurufDepanUserDetail.getText().equals( "m" )){
            color=R.color.red_500;
        }else if(hurufDepanUserDetail.getText().equals( "N" ) || hurufDepanUserDetail.getText().equals( "n" )){
            color=R.color.light_blue_500;
        }else if(hurufDepanUserDetail.getText().equals( "O" ) || hurufDepanUserDetail.getText().equals( "o" )){
            color=R.color.light_green_500;
        }else if(hurufDepanUserDetail.getText().equals( "P" ) || hurufDepanUserDetail.getText().equals( "p" )){
            color=R.color.orange_500;
        }else if(hurufDepanUserDetail.getText().equals( "Q" ) || hurufDepanUserDetail.getText().equals( "q" )){
            color=R.color.pink_500;
        }else if(hurufDepanUserDetail.getText().equals( "R" ) || hurufDepanUserDetail.getText().equals( "r" )){
            color=R.color.red_600;
        }else if(hurufDepanUserDetail.getText().equals( "S" ) || hurufDepanUserDetail.getText().equals( "s" )){
            color=R.color.yellow_600;
        }else if(hurufDepanUserDetail.getText().equals( "T" ) || hurufDepanUserDetail.getText().equals( "t" )){
            color=R.color.blue_600;
        }else if(hurufDepanUserDetail.getText().equals( "U" ) || hurufDepanUserDetail.getText().equals( "u" )){
            color=R.color.cyan_600;
        }else if(hurufDepanUserDetail.getText().equals( "V" ) || hurufDepanUserDetail.getText().equals( "v" )){
            color=R.color.green_600;
        }else if(hurufDepanUserDetail.getText().equals( "W" ) || hurufDepanUserDetail.getText().equals( "w" )){
            color=R.color.purple_600;
        }else if(hurufDepanUserDetail.getText().equals( "X" ) || hurufDepanUserDetail.getText().equals( "x" )){
            color=R.color.pink_600;
        }else if(hurufDepanUserDetail.getText().equals( "Y" ) || hurufDepanUserDetail.getText().equals( "y" )){
            color=R.color.lime_600;
        }else if(hurufDepanUserDetail.getText().equals( "Z" ) || hurufDepanUserDetail.getText().equals( "z" )){
            color=R.color.orange_600;
        }

        fotoUserDetail1.setImageResource(color);
    }
}
