package com.example.mgtuv2;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.SharedPreferences;
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

    SharedPreferences sPref;
    final String savedSessionId = "savedSessionId";
    final String savedCsrf = "savedCsrf";
    final Boolean isNeedAuth = false;



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

        if (isNeedAutoAuth() == true) {
            //setSessionIdCsrfFromFiles();
            loginButtonOnClickUiChange();

            AuthUserTask AUR = new AuthUserTask(LoginPage.this);
            AUR.execute();
        }
//        else {
//            inputPassword.setText("noneedinauth");
//        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButtonOnClickUiChange();

                AuthUserTask AUR = new AuthUserTask(LoginPage.this);
                AUR.execute();


            }
        });


    }



    public void loginErrorUiChange(){
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

    public String getLoginString() {
        return inputLogin.getText().toString();
    }

    public String getPasswordString() {
        return inputPassword.getText().toString();
    }

    public void saveSessionIdCsrfInFiles() {
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(savedSessionId, djangoUser.getSessionId());
        ed.putString(savedCsrf, djangoUser.getCsrfToken());
        ed.apply();
   }

//    public void setSessionIdCsrfFromFiles() {
//        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
//        String savedSessionIdTemp = sPref.getString("savedSessionId", "");
//        String savedCsrfTemp = sPref.getString("savedCsrf", "");
//        djangoUser.setSessionId(savedSessionIdTemp);
//        djangoUser.setCsrfToken(savedCsrfTemp);
////        inputLogin.setText(savedSessionIdTemp);
////        inputPassword.setText(savedCsrfTemp);
//    }

    public String getSessionIdFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedSessionIdTemp = sPref.getString("savedSessionId", "");
        return savedSessionIdTemp;
    }

    public String getCsrfTokenFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedCsrfTemp = sPref.getString("savedCsrf", "");
        return savedCsrfTemp;
    }

    public Boolean isNeedAutoAuth(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        return sPref.getBoolean("isNeedAuth", false);
    }

    public void setStatusIsNeedAuth(Boolean inputBoolean){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNeedAuth", inputBoolean);
        ed.apply();
    }
}

