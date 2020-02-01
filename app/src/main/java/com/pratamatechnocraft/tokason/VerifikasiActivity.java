package com.pratamatechnocraft.tokason;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifikasiActivity extends AppCompatActivity {
    String verificationId;
    FirebaseAuth mAuth;
    Intent intent;
    Button btnVerify;
    EditText txtOtp;
    String otp, from, username;
    SessionManager sessionManager;
    TextView txtInfoTelp;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl = baseUrlApiModel.getBaseURL();
    private static String URL_Daftar = "api/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);
        sessionManager = new SessionManager(this);
        btnVerify = findViewById(R.id.btnKirimVerifikasi);
        txtOtp = findViewById(R.id.editTextKodeVerifikasi);
        txtInfoTelp = findViewById(R.id.txt_info_telp);
        String string = txtInfoTelp.getText().toString();

        Toolbar toolbar = findViewById(R.id.toolbar_verifikasi);
        setSupportActionBar(toolbar);
        this.setTitle("Verifikasi");
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorIcons));
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        verificationId = intent.getStringExtra("verificationId");
        from = intent.getStringExtra("from");
        username = intent.getStringExtra("username");
        txtInfoTelp.setText(string+username);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = txtOtp.getText().toString().trim();
                if (!otp.isEmpty()) {
                    verifyOtp(verificationId, otp, from);
                }
            }
        });
    }

    private void verifyOtp(String verificationId, String otp, String from) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        //sign in user

        signInWithPhoneAuthCredential(credential, from);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String from) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            if (from.equals("daftar")) {
                                Log.d("DDADA", "onComplete: "+from);
                                changeStatusUser(username);
                            } else if (from.equals("lupa")) {
                                Log.d("elseif", "onComplete: "+from);
                                Intent mintent = new Intent(VerifikasiActivity.this, PasswordBaruActivity.class);
                                mintent.putExtra("no_telp",username);
                                startActivity(mintent);
                                finish();
                            }
                        } else {

                            String message = "Verification failed , Please try again later.";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(VerifikasiActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void changeStatusUser(final String username) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + URL_Daftar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
//                        Toast.makeText(VerifikasiActivity.this, "username: "+username, Toast.LENGTH_SHORT).show();
                        JSONObject data_user = jsonObject.getJSONObject("data_user");
                        String kd_user = data_user.getString("kd_user").trim();
                        String level_user = String.valueOf(data_user.getInt("level_user"));
                        String kd_outlet = String.valueOf(data_user.getInt("kd_outlet"));

                        sessionManager.createSession(kd_user, level_user, kd_outlet);

                        Toast.makeText(VerifikasiActivity.this, "Login Berhasil !", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(VerifikasiActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(VerifikasiActivity.this, "Periksa koneksi", Toast.LENGTH_SHORT).show();
                    }

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(VerifikasiActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(VerifikasiActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                Toast.makeText(VerifikasiActivity.this, "username: "+username, Toast.LENGTH_SHORT).show();
                params.put("username", username);
                params.put("api", "verify");
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
                finish();
                Intent intent = new Intent(VerifikasiActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(VerifikasiActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
