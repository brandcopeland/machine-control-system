package com.example.mgtuv2;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//Класс страницы логина
public class LoginPage extends AppCompatActivity {

    TextView loginPageTitle;
    TextView textErrorLogin;
    EditText inputLogin;
    EditText inputPassword;
    Button loginButton;
    ProgressBar loadingCircle;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        textErrorLogin = findViewById(R.id.textErrorLogin);
        loginPageTitle = findViewById(R.id.loginPageTitle);
        inputLogin = findViewById(R.id.inputLogin);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        loadingCircle = findViewById(R.id.loadingCircle);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButtonOnClickUiChange();

                AuthUserTask AUR = new AuthUserTask(LoginPage.this, "LoginPage");
                AUR.execute();
            }
        });


        if (isNeedAutoAuth()) {
            loginButtonOnClickUiChange();
            AuthUserTask AUR = new AuthUserTask(LoginPage.this, "LoginPage");
            AUR.execute();
        }

    }

    //Функция изменения UI при выдаче ошибки
    public void loginErrorUiChange(){
        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();

        if (djangoUser.getInternetConnectionErrorStatus()){
            textErrorLogin.setText(getResources().getString(R.string.loginErrorInternet));
        }
        else{
            textErrorLogin.setText(getResources().getString(R.string.loginErrorCommon));
            setStatusIsNeedAutoAuth(false);
        }
        if (textErrorLogin.getVisibility() == View.INVISIBLE)
        {
            textErrorLogin.setVisibility(View.VISIBLE);
        }
        loginButton.setEnabled(true);
        loadingCircle.setVisibility(View.INVISIBLE);
    }

    //Функция изменения UI при нажатии кнопки логина
    public void loginButtonOnClickUiChange(){
        textErrorLogin.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(false);
        loadingCircle.setVisibility(View.VISIBLE);

    }

    //Получение логина и пароля из input-полей
    public String getLoginString() {
        return inputLogin.getText().toString();
    }
    public String getPasswordString() {
        return inputPassword.getText().toString();
    }


    //Сохранение sessionID в файлы
    public void saveSessionIdCsrfInFiles() {
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("savedSessionId", djangoUser.getSessionId());
        ed.putString("savedCsrf", djangoUser.getCsrfToken());
        ed.apply();
   }
    //Получение sessionID из файлов
    public String getSessionIdFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedSessionIdTemp = sPref.getString("savedSessionId", "");
        return savedSessionIdTemp;
    }
    //Получение csrf token из файлов
    public String getCsrfTokenFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedCsrfTemp = sPref.getString("savedCsrf", "");
        return savedCsrfTemp;
    }
    //bool функция на проверку, нужен ли автоматическая авторизация (Если юзер уже был залогинен в систему)
    public Boolean isNeedAutoAuth(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        return sPref.getBoolean("isNeedAutoAuth", false);
    }
    //Установка статуса автологина
    public void setStatusIsNeedAutoAuth(Boolean inputBoolean){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNeedAutoAuth", inputBoolean);
        ed.apply();
    }
}

