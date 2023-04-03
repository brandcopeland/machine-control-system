package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    TextView loginPageTitle;
    static TextView textErrorLogin;
    static EditText inputLogin;
    static EditText inputPassword;
    Button loginButton;
    ProgressBar loadingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_page);

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

                //Если логин и пароль есть в базе
                if (checkLoginPasswordInDatabase()) {
                    Toast.makeText(LoginPage.this, "LOGIN SUCCESSFULL", Toast.LENGTH_SHORT).show();
                    login(view);
                } else {
                    Toast.makeText(LoginPage.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    //Функция входа в главное меню после логина
    public void login(View view) {
        Intent intent = new Intent(this, Lobby.class);
        startActivity(intent);
    }

    public boolean checkLoginPasswordInDatabase()
    {
        AuthUserTask AUR = new AuthUserTask();
        AUR.execute();
        return true;

    }

    public static void loginErrorUiChange(){
        if (textErrorLogin.getVisibility() == View.INVISIBLE)
        {
            textErrorLogin.setVisibility(View.VISIBLE);
        }
    }

    public void loginButtonOnClickUiChange(){
        textErrorLogin.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(false);
        loadingCircle.setVisibility(View.VISIBLE);

    }

    public static String getLoginString() {
        return inputLogin.getText().toString();
    }

    public static String getPasswordString() {
        return inputPassword.getText().toString();
    }
}
