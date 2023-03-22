package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class main_login_page extends AppCompatActivity {

    TextView login_lobby_title;
    static EditText login_lobby_login;
    static EditText login_lobby_password;
    Button login_lobby_enter_button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_page);

        login_lobby_title=findViewById(R.id.login_lobby_title);
        login_lobby_login=findViewById(R.id.login_lobby_login);
        login_lobby_password=findViewById(R.id.login_lobby_password);
        login_lobby_enter_button=findViewById(R.id.login_lobby_enter_button);

        login_lobby_enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Если логин и пароль есть в базе

                    if (check_login_and_password_in_database(getLoginString(), getPasswordString()))
                    {
                        Toast.makeText(main_login_page.this, "LOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
                        login(view);
                    }
                    else
                    {
                        Toast.makeText(main_login_page.this, "LOGIN FAILED",Toast.LENGTH_SHORT).show();
                    }

            }
        });


    }

    //Функция входа в главное меню после логина
    public void login(View view)
    {
        Intent intent = new Intent(this, main_lobby.class);
        startActivity(intent);
    }

    public boolean check_login_and_password_in_database(String login, String password) //Убрать Логин и пароль
    {
        AuthUserTask AUR = new AuthUserTask();
        AUR.execute();
        return true;
//        while(true){
//            if(DjangoUser.isSessionIdReceived == true){
//                System.out.println(" isSessionIdReceived = true in checkloginpass");
//                return true;
//            }
//            else if (DjangoUser.isSessionIdReceived == false){
//                System.out.println(" isSessionIdReceived = true in checkloginpass");
//                return false;
//            }
//            else{
//                System.out.println("Waiting for isSessionIdReceived (null)");
//            }
//        }

    }

    public static String getLoginString() {
        return login_lobby_login.getText().toString();
    }
    public static String getPasswordString() {
        return login_lobby_password.getText().toString();
    }
}

