package com.example.mgtuv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//Класс фрагмента настроек в активности лобби
public class LobbySettingsFragment extends Fragment {

    View view;
    Button unloginButton;
    TextView textViewSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lobby_settings, container, false);

        unloginButton = view.findViewById(R.id.unloginButton);
        textViewSettings = view.findViewById(R.id.textViewSettings);

        unloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Lobby activity =  (Lobby) getActivity();
                // Проверка на null (безопасность)
                if (activity != null) {
                    activity.unlogin();
                }
            }
        });

        return view;
    }



}