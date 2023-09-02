package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//Класс активности лобби
public class Lobby extends AppCompatActivity  {
    SharedPreferences sPref;
    //Создаем 3 фрагмента при инициализации класса
    LobbyHomeFragment homeFragment = new LobbyHomeFragment();
    LobbyDevicesFragment devicesFragment = new LobbyDevicesFragment();
    LobbySettingsFragment settingsFragment = new LobbySettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) //Запуск лобби приложения, по сути только система смены фрагментов
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //Добавляем 3 фрагмента на экран и прячем 2 из них
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, homeFragment, "homeFragmentTag");
        transaction.add(R.id.frame_layout, devicesFragment, "devicesFragmentTag");
        transaction.add(R.id.frame_layout, settingsFragment, "Fragment3Tag");
        transaction.hide(devicesFragment);
        transaction.hide(settingsFragment);
        transaction.commit();
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
        //Активируем переключатель фрагментов через меню навигации
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            FragmentTransaction tempTransaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.home:
                    hideAllFragments(tempTransaction);
                    selectedFragment = homeFragment;
                    break;
                case R.id.devices:
                    hideAllFragments(tempTransaction);
                    selectedFragment = devicesFragment;
                    break;
                case R.id.settings:
                    hideAllFragments(tempTransaction);
                    selectedFragment = settingsFragment;
                    break;
            }

            if (selectedFragment != null) {
                if (selectedFragment.isAdded()) {
                    tempTransaction.show(selectedFragment);
                } else {
                    tempTransaction.add(R.id.frame_layout, selectedFragment);
                }
                tempTransaction.commit();
            }
            return true;
        });
    }

    // Функция скрывает все фрагменты, которые могут быть видны в данный момент
    void hideAllFragments(FragmentTransaction transaction) {

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

    //Функция выхода из аккаунта / перехода на страницу логина
    public void unlogin()
    {
        homeFragment.timer.cancel();
        setStatusIsNeedAutoAuth(false);
        Toast.makeText(Lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    //Сохранения статуса необходимости автоматической авторизации
    public void setStatusIsNeedAutoAuth(Boolean inputBoolean){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNeedAutoAuth", inputBoolean);
        ed.apply();
    }
    //Получение статуса необходимости автоматической авторизации
    public Boolean isNeedAutoAuth(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        return sPref.getBoolean("isNeedAutoAuth", false);
    }
    //Получение sessionID  из файла
    public String getSessionIdFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedSessionIdTemp = sPref.getString("savedSessionId", "");
        return savedSessionIdTemp;
    }
    //Получение csrf  из файла
    public String getCsrfTokenFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedCsrfTemp = sPref.getString("savedCsrf", "");
        return savedCsrfTemp;
    }

}