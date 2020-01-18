package com.pratamatechnocraft.tokason;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifikasiActivity extends AppCompatActivity {
    String verificationId;
    FirebaseAuth mAuth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        Toolbar toolbar = findViewById(R.id.toolbar_verifikasi);
        setSupportActionBar(toolbar);
        this.setTitle("Verifikasi");
        toolbar.setSubtitleTextColor( ContextCompat.getColor(this, R.color.colorIcons) );
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        verificationId = intent.getStringExtra("verificationId");
    }

    private void verifyOtp(String verificationId, String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);

        //sign in user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(VerifikasiActivity.this , PasswordBaruActivity.class);
                            startActivity(intent);
                            finish();
                        }else {

                            String message = "Verification failed , Please try again later.";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(VerifikasiActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
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
}
