package com.pratamatechnocraft.tokason;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.pratamatechnocraft.tokason.Fragment.SubscriptionFragment;
import com.pratamatechnocraft.tokason.Fragment.WaitingConfirmationFragment;
import com.pratamatechnocraft.tokason.Service.SessionManager;

public class SubscriptionActivity extends AppCompatActivity {
    public static final String EXTRA_SUBSCRIPTION = "extra_subscription";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Toolbar toolbar = findViewById(R.id.toolbar_verifikasi);
        setSupportActionBar(toolbar);
        this.setTitle("Subscription");
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorIcons));
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);

        if (savedInstanceState == null) {
            Intent getIntent = getIntent();
            String statusBayar = getIntent.getStringExtra(EXTRA_SUBSCRIPTION);

            if (statusBayar.equals("belumbayar")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SubscriptionFragment())
                        .commitNow();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new WaitingConfirmationFragment())
                        .commitNow();
            }

        }

    }

    @Override
    public void onBackPressed() {
        sessionManager.logout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sessionManager.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

