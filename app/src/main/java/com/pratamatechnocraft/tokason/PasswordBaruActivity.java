package com.pratamatechnocraft.tokason;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class PasswordBaruActivity extends AppCompatActivity {
    EditText passbaru,barulagi;
    Button finish;
    BaseUrlApiModel baseUrlApiModel = new BaseUrlApiModel();
    private String baseUrl = baseUrlApiModel.getBaseURL();
    private static String URL_Daftar = "api/user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_baru);
        Toolbar toolbar = findViewById(R.id.toolbar_lupasandi);
        setSupportActionBar(toolbar);
        setTitle("Password Baru");
        Intent intent = getIntent();
        final String no_telp = intent.getStringExtra("no_telp");
        passbaru = findViewById(R.id.inputLayoutPasswordBaru);
        barulagi = findViewById(R.id.inputLayoutPasswordBaruLagi);
        finish = findViewById(R.id.buttonSimpanUbahPass);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p1,p2;
                p1 = passbaru.getText().toString();
                p2 = barulagi.getText().toString();
                if (!p1.equals(p2)){
                    Toast.makeText(PasswordBaruActivity.this,"Password Harus Sama",Toast.LENGTH_SHORT).show();
                    ubahPassword(no_telp,p1);
                }
            }
        });

    }

    private void ubahPassword(final String noTelp, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + URL_Daftar, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", ""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String kode = jsonObject.getString("kode");
                    String pesan = jsonObject.getString("pesan");
                    if (kode.equals("1")) {
                        Toast.makeText(PasswordBaruActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PasswordBaruActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (kode.equals("2")) {
                        Toast.makeText(PasswordBaruActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PasswordBaruActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(PasswordBaruActivity.this, "Periksa koneksi & coba lagi", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("no_telp", noTelp);
                params.put("password", password);
                params.put("api", "newpass");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
