package com.pratamatechnocraft.tokason;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DaftarActivity extends AppCompatActivity {
    EditText txtNamaDepan, txtNamaBelakang, txtNoTelepon, txtAlamat, txtNamaToko, txtAlamatToko,
            txtRefferalCode, txtUsername, txtPassword, txtEmail;
    CheckBox checkBoxAgrrement;
    TextView textView;
    Button btnDaftar;
    ProgressBar progressBar;
    String namaDepan, namaBelakang, noTelp, alamat, namaToko, alamatToko, referralCode, username, password, email;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl = baseUrlApiModel.getBaseURL();
    private static String URL_Daftar = "api/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        textView = findViewById(R.id.syarat);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtEmail = findViewById(R.id.email);
        txtNamaDepan = findViewById(R.id.namaDepanPemilik);
        txtNamaBelakang = findViewById(R.id.namaBelakangPemilik);
        txtNoTelepon = findViewById(R.id.NotelpPemilik);
        txtAlamat = findViewById(R.id.alamatLengkapPemilik);
        txtNamaToko = findViewById(R.id.NamaToko);
        txtAlamatToko = findViewById(R.id.AlamatToko);
        txtRefferalCode = findViewById(R.id.Referral);
        checkBoxAgrrement = findViewById(R.id.checkBox);
        btnDaftar = findViewById(R.id.btnLanjutDaftar);
        progressBar = findViewById(R.id.progressBar);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DaftarActivity.this,SyaratKetentuanActivity.class);
                startActivity(intent);
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar_daftar);
//        setSupportActionBar(toolbar);
//        this.setTitle("Daftar Baru");
//        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorIcons));
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namaDepan = txtNamaDepan.getText().toString().trim();
                namaBelakang = txtNamaBelakang.getText().toString().trim();
                noTelp = txtNoTelepon.getText().toString().trim();
                alamat = txtAlamat.getText().toString().trim();
                namaToko = txtNamaToko.getText().toString().trim();
                alamatToko = txtAlamatToko.getText().toString().trim();
                referralCode = txtRefferalCode.getText().toString().trim();
                username = txtUsername.getText().toString().trim();
                password = txtPassword.getText().toString().trim();
                email =txtEmail.getText().toString().trim();
                referralCode = txtRefferalCode.getText().toString().trim();

                if (namaDepan.isEmpty()) {
                    txtNamaDepan.setError("Nama Depan tidak boleh kosong!");
                } else if (namaBelakang.isEmpty()) {
                    txtNamaBelakang.setError("Nama Belakang tidak boleh kosong!");
                } else if (noTelp.isEmpty()) {
                    txtNoTelepon.setError("No Telepon tidak boleh kosong!");
                } else if (alamat.isEmpty()) {
                    txtAlamat.setError("Alamat tidak boleh kosong!");
                } else if (email.isEmpty()) {
                    txtAlamat.setError("Alamat tidak boleh kosong!");
                } else if (username.isEmpty()) {
                    txtUsername.setError("Alamat tidak boleh kosong!");
                } else if (password.isEmpty()) {
                    txtPassword.setError("Alamat tidak boleh kosong!");
                } else if (namaToko.isEmpty()) {
                    txtNamaToko.setError("Nama Toko tidak boleh kosong!");
                } else if (alamatToko.isEmpty()) {
                    txtAlamatToko.setError("Alamat Toko tidak boleh kosong!");
                } else if (!checkBoxAgrrement.isChecked()) {
                    checkBoxAgrrement.setError("Anda harus menyetujui syarat dan ketentuan");
                } else {
                    if (noTelp.substring(0,3).equals("+62")){
                        noTelp = noTelp;
                    } else if (noTelp.substring(0,1).equals("0")){
                        noTelp = "+62"+noTelp.substring(1);
                    } else {
                        noTelp = "+62"+noTelp;
                    }
                    btnDaftar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    prosesDaftar(namaDepan, namaBelakang, noTelp, alamat, namaToko, alamatToko,
                            referralCode, username, password, email);
                }
            }
        });
    }

    private void prosesDaftar(final String namaDepan, final String namaBelakang, final String noTelp, final String alamat,
                              final String namaToko, final String alamatToko, final String referralCode, final String username,
                              final String password, final String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + URL_Daftar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", ""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    String pesan = jsonObject.getString("pesan");
                    if (kode.equals("1")) {
                        Toast.makeText(DaftarActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        sendVerificationCode(noTelp);
                    } else if (kode.equals("2")) {
                        Toast.makeText(DaftarActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    } else if (kode.equals("3")){
                        Toast.makeText(DaftarActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        txtNoTelepon.setError(pesan);
                    } else if (kode.equals("4")){
                        Toast.makeText(DaftarActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        txtEmail.setError(pesan);
                    } else if (kode.equals("5")){
                        Toast.makeText(DaftarActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        txtUsername.setError(pesan);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DaftarActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                }

                btnDaftar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(DaftarActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();

                btnDaftar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama_depan", namaDepan);
                params.put("nama_belakang", namaBelakang);
                params.put("no_telp", noTelp);
                params.put("alamat", alamat);
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                params.put("nama_outlet", namaToko);
                params.put("alamat_outlet", alamatToko);
                params.put("referral", referralCode);
                params.put("api", "daftar");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DaftarActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void sendVerificationCode(String phoneNumber){
        String noTelp;
        if (phoneNumber.substring(0,3).equals("+62")){
            noTelp = phoneNumber;
        } else if (phoneNumber.substring(0,1).equals("0")){
            noTelp = "+62"+phoneNumber.substring(1);
        } else {
            noTelp = "+62"+phoneNumber;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                noTelp,
                120,
                TimeUnit.SECONDS,
                DaftarActivity.this,
                mCall
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCall = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("DaftarActivity", ""+ phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("DaftarActivity" , "Tidak Berhasil");
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            String mVerificationId = verificationId;
            Log.e("DaftarActivity" , "Verification id : " + verificationId);
            Intent intent = new Intent(DaftarActivity.this , VerifikasiActivity.class);
            intent.putExtra("verificationId" , verificationId);
            intent.putExtra("username" , noTelp);
            intent.putExtra("from" , "daftar");
            startActivity(intent);
            finish();
        }
    };
}
