package com.pratamatechnocraft.tokason.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pratamatechnocraft.tokason.R;

public class WaitingConfirmationFragment extends Fragment {

    Context context;

    public WaitingConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnRefresh = view.findViewById(R.id.btn_refresh_subscription);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 1/29/2020 Cek Status User, jika sudah dibayar masuk ke Main Activity
            }
        });


    }
}
