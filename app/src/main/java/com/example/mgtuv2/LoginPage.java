package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {

    TextView loginPageTitle;
    static TextView textErrorLogin;
    static EditText inputLogin;
    static EditText inputPassword;
    static Button loginButton;
    static ProgressBar loadingCircle;

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

                AuthUserTask AUR = new AuthUserTask(LoginPage.this);
                AUR.execute();


            }
        });


    }



    public static void loginErrorUiChange(){
        if (textErrorLogin.getVisibility() == View.INVISIBLE)
        {
            textErrorLogin.setVisibility(View.VISIBLE);
        }
        loginButton.setEnabled(true);
        loadingCircle.setVisibility(View.INVISIBLE);
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

