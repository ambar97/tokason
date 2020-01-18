package com.pratamatechnocraft.tokason;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pratamatechnocraft.tokason.Model.BaseUrlApiModel;
import com.pratamatechnocraft.tokason.Service.Config;
import com.pratamatechnocraft.tokason.Service.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    Button btnLogin, btnLupaSandi, btnDaftar;
    EditText eTxtUsername, eTxtPassword;
    String username, password;
    ProgressBar loading;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl = baseUrlApiModel.getBaseURL();
    private static String URL_LOGIN = "api/user";
    SessionManager sessionManager;

    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggin()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        btnLupaSandi = findViewById(R.id.btnLupaSandi);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnLogin = findViewById(R.id.buttonLogin);
        eTxtUsername = findViewById(R.id.editTxtUsernameLogin);
        eTxtPassword = findViewById(R.id.editTxtPasswordLogin);
        loading = findViewById(R.id.progressBar);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = eTxtUsername.getText().toString().trim();
                password = eTxtPassword.getText().toString().trim();
                if (username.isEmpty()) {
                    eTxtUsername.setError("Username tidak boleh kosong !");
                } else if (password.isEmpty()) {
                    eTxtPassword.setError("Password tidak boleh kosong !");
                } else {
                    prosesLogin(username, password);
                }

            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, DaftarActivity.class);
                startActivity(i);
            }
        });

        btnLupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, LupaSandiActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggin()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void prosesLogin(final String user, final String pass) {
        loading.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        JSONObject data_user = jsonObject.getJSONObject("data_user");
                        String kd_user = data_user.getString("kd_user").trim();
                        String level_user = String.valueOf(data_user.getInt("level_user"));
                        String kd_outlet = String.valueOf(data_user.getInt("kd_outlet"));

                        sessionManager.createSession(kd_user, level_user, kd_outlet);

                        Toast.makeText(LoginActivity.this, "Login Berhasil !", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();

                        loading.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                    } else if (success.equals("2")) {
                        loading.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Password Tidak Valid !!", Toast.LENGTH_SHORT).show();
                    } else if (success.equals("3")) {
                        loading.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Username Tidak Valid !!", Toast.LENGTH_SHORT).show();
                    } else if(success.equals("4")) {
                        finish();
                        Intent intent = new Intent(LoginActivity.this, VerifikasiActivity.class);
                        startActivity(intent);
                    } else if(success.equals("5")) {
                        // TODO: 1/18/2020 AKUN BELUM BAYAR 
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        loading.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user", user);
                params.put("pass", pass);
                params.put("api", "login");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", regId);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
