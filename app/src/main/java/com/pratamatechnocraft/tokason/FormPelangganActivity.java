package com.pratamatechnocraft.tokason;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pratamatechnocraft.tokason.Adapter.DBDataSourceKeranjang;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Model.ModelPelangganPilih;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FormPelangganActivity extends AppCompatActivity {

    private Button buttonSimpanTambahPelanggan,buttonBatalTambahPelanggan;
    private TextInputLayout inputLayoutNamaPelanggan,inputLayoutNoTelp,inputLayoutAlamat;
    private EditText inputNamaPelanggan,inputNoTelp,inputAlamat;
    private ProgressDialog progress;
    private SwipeRefreshLayout refreshFormPelanggan;
    private BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl=baseUrlApiModel.getBaseURL();
    private static final String API_URL = "api/pelanggan";
    Intent i;
    private DBDataSourceKeranjang dbDataSourceKeranjang;
    private ModelPelangganPilih modelPelangganPilih;

    SessionManager sessionManager;
    private String kdOutlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pelanggan );

        sessionManager = new SessionManager( this );
        HashMap<String, String> user = sessionManager.getUserDetail();
        kdOutlet = user.get(SessionManager.KD_OUTLET);

        i = getIntent();
        progress = new ProgressDialog(this);
        refreshFormPelanggan = findViewById( R.id.refreshFormPelanggan );
        Toolbar ToolBarAtas2 = (Toolbar)findViewById(R.id.toolbartambahpelanggan);
        ToolBarAtas2.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        this.setTitle("Data Pelanggan");
        setSupportActionBar(ToolBarAtas2);
        if (i.getStringExtra( "type" ).equals( "tambah" )){
            ToolBarAtas2.setSubtitle( "Tambah Pelanggan" );
        }else if(i.getStringExtra( "type" ).equals( "edit" )){
            ToolBarAtas2.setSubtitle( "Edit Pelanggan" );
        }
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*BUTTON*/
        buttonSimpanTambahPelanggan = findViewById( R.id.buttonSimpanTambahPelanggan );
        buttonBatalTambahPelanggan = findViewById( R.id.buttonBatalTambahPelanggan);

        /*LAYOUT INPUT*/
        inputLayoutNamaPelanggan = (TextInputLayout) findViewById(R.id.inputLayoutNamaPelanggan);
        inputLayoutNoTelp = (TextInputLayout) findViewById(R.id.inputLayoutNoTelp);
        inputLayoutAlamat = (TextInputLayout) findViewById(R.id.inputLayoutAlamat);

        /*TEXT INPUT*/
        inputNamaPelanggan = (EditText) findViewById(R.id.inputNamaPelanggan);
        inputNoTelp = (EditText) findViewById(R.id.inputNoTelp);
        inputAlamat = (EditText) findViewById(R.id.inputAlamat);

        if (i.getStringExtra( "type" ).equals( "tambah" )){
            buttonSimpanTambahPelanggan.setText("Tambah");
        }else if(i.getStringExtra( "type" ).equals( "edit" )){
            loadTampilEdit(i.getStringExtra( "kdPelanggan" ));
            buttonSimpanTambahPelanggan.setText("Simpan");
        }


        /*VALIDASI DATA*/
        inputNamaPelanggan.addTextChangedListener( new MyTextWatcher( inputNamaPelanggan) );
        inputNoTelp.addTextChangedListener( new MyTextWatcher( inputNoTelp) );
        inputAlamat.addTextChangedListener( new MyTextWatcher( inputAlamat) );

        if (i.getStringExtra( "type" ).equals("tambah")){
            buttonSimpanTambahPelanggan.setText("Tambah");
            refreshFormPelanggan.setEnabled( false );

        }else if(i.getStringExtra( "type" ).equals("edit")){
            buttonSimpanTambahPelanggan.setText("Simpan");
        }

        refreshFormPelanggan.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTampilEdit(i.getStringExtra( "kdPelanggan" ));
            }
        } );


        buttonSimpanTambahPelanggan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i.getStringExtra( "type" ).equals( "tambah" )){
                    if (!validateNamaPelanggan() || !validateNotelp() || !validateAlamat()) {
                        return;
                    }else {
                        progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(false);
                        progress.setCanceledOnTouchOutside(false);
                        prosesTambahPelanggan(
                                inputNamaPelanggan.getText().toString().trim(),
                                inputNoTelp.getText().toString().trim(),
                                inputAlamat.getText().toString().trim()
                        );
                    }
                }else if(i.getStringExtra( "type" ).equals( "edit" )){
                    if (!validateNamaPelanggan() || !validateNotelp() || !validateAlamat()) {
                        return;
                    }else {
                        progress.setMessage("Mohon Ditunggu, Sedang diProses.....");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(false);
                        progress.setCanceledOnTouchOutside(false);
                        prosesEditPelanggan(
                                inputNamaPelanggan.getText().toString().trim(),
                                inputNoTelp.getText().toString().trim(),
                                inputAlamat.getText().toString().trim()
                        );
                    }
                }
            }
        } );

        buttonBatalTambahPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormPelangganActivity.super.onBackPressed();
            }
        });

    }

    /*PROSES KE DATABASE*/
    private void prosesEditPelanggan(final String namaPelanggan, final String noTelp, final String alamatPelanggan) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        finish();
                        Toast.makeText(FormPelangganActivity.this, "Berhasil Edit Pelanggan", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(FormPelangganActivity.this, "Gagal Edit Pelanggan", Toast.LENGTH_SHORT).show();
                    }
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d( "TAG", e.toString() );
                    Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kd_pelanggan", i.getStringExtra( "kdPelanggan" ));
                params.put("nama_pelanggan", namaPelanggan);
                params.put("no_telp_pelanggan", noTelp);
                params.put("alamat_pelanggan", alamatPelanggan);
                params.put("api", "edit");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void prosesTambahPelanggan(final String namaPelanggan, final String noTelp, final String alamatPelanggan) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl+API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    if (kode.equals("1")) {
                        if(i.getStringExtra( "typedua" ).equals( "keranjang" )){
                            dbDataSourceKeranjang = new DBDataSourceKeranjang( FormPelangganActivity.this );
                            dbDataSourceKeranjang.open();
                            if(dbDataSourceKeranjang.totalPelangganPilih()==false) {
                                modelPelangganPilih = dbDataSourceKeranjang.createModelPelangganPilih(
                                        jsonObject.getString("kd_pelanggan"),
                                        jsonObject.getString("nama_pelanggan"),
                                        jsonObject.getString("no_telp_pelanggan"),
                                        jsonObject.getString("alamat_pelanggan"),
                                        jsonObject.getString("tgl_ditambahkan")
                                );
                            }else{
                                dbDataSourceKeranjang.updatePelangganPilih(
                                        jsonObject.getString("kd_pelanggan"),
                                        jsonObject.getString("nama_pelanggan"),
                                        jsonObject.getString("no_telp_pelanggan"),
                                        jsonObject.getString("alamat_pelanggan"),
                                        jsonObject.getString("tgl_ditambahkan")
                                );
                            }

                        }
                        finish();
                        Toast.makeText(FormPelangganActivity.this, "Berhasil Menambahkan Pelanggan", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(FormPelangganActivity.this, "Gagal Menambahkan Pelanggan", Toast.LENGTH_SHORT).show();
                    }
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d( "TAG", e.toString() );
                    Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d( "TAG", error.toString() );
                /*Log.d(TAG, error.printStackTrace() );*/
                Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kd_outlet", kdOutlet);
                params.put("nama_pelanggan", namaPelanggan);
                params.put("no_telp_pelanggan", noTelp);
                params.put("alamat_pelanggan", alamatPelanggan);
                params.put("api", "tambah");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadTampilEdit(String kdPelanggan){
        refreshFormPelanggan.setRefreshing(true);
        StringRequest stringRequest = new StringRequest( Request.Method.GET, baseUrl+API_URL+"?api=pelanggandetail&kd_pelanggan="+kdPelanggan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject pelanggandetail = new JSONObject(response);
                            inputNamaPelanggan.setText( pelanggandetail.getString( "nama_pelanggan" ) );
                            inputNoTelp.setText( pelanggandetail.getString( "no_telp_pelanggan" ) );
                            inputAlamat.setText( pelanggandetail.getString( "alamat_pelanggan" ) );
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                        }
                        refreshFormPelanggan.setRefreshing( false );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FormPelangganActivity.this, "Periksa koneksi & coba lagi1", Toast.LENGTH_SHORT).show();
                        refreshFormPelanggan.setRefreshing( false );
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue( FormPelangganActivity.this );
        requestQueue.add( stringRequest );
    }
    /*PROSES KE DATABASE*/


    /*INPUT*/
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputNamaPelanggan:
                    validateNamaPelanggan();
                    break;
                case R.id.inputNoTelp:
                    validateNotelp();
                    break;
                case R.id.inputAlamat:
                    validateAlamat();
                    break;
            }
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateNotelp() {
        if (inputNoTelp.getText().toString().trim().isEmpty()) {
            inputLayoutNoTelp.setError("Masukkan No Telepon");
            requestFocus(inputNoTelp);
            return false;
        } else {
            inputLayoutNoTelp.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateNamaPelanggan() {
        if (inputNamaPelanggan.getText().toString().trim().isEmpty()) {
            inputLayoutNamaPelanggan.setError("Masukkan Nama Pelanggan");
            requestFocus(inputNamaPelanggan);
            return false;
        } else {
            inputLayoutNamaPelanggan.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateAlamat() {
        if (inputAlamat.getText().toString().trim().isEmpty()) {
            inputLayoutAlamat.setError("Masukkan Alamat");
            requestFocus(inputAlamat);
            return false;
        } else {
            inputLayoutAlamat.setErrorEnabled(false);
        }
        return true;
    }

    /*INPUT*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
