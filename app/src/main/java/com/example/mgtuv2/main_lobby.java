package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class main_lobby extends AppCompatActivity {

    //random letter generator
    TextView random_text_view;
    Button random_button_generator;

    //qrcode generator
    ImageView ivOutput;
    String secret_code_for_qr = "SecretCodeForQr1";

    @Override
    //Функция создания лобби. Надо сделать проверку на разрешение доступа
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lobby);

        //random letter generator
        random_text_view=findViewById(R.id.random_text_view);
        random_button_generator=findViewById(R.id.random_button_generator);

        random_button_generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                random_text_view.setText(generate_random_password());
            }
        });

        //qrcode generator
        ivOutput = findViewById(R.id.iv_output);
    }

    //Функция выхода из учетки. Можно вызывать когда юзер выходит по кнопке, либо истек срок проверки в базе данных
    public void unlogin(View view)
    {
        Toast.makeText(main_lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, main_login_page.class);
        startActivity(intent);
    }

    public String generate_random_password()
    {
        int digit = 8;
        String lower_cases = "qwertyuiopasdfghjklzxcvbnm";
        String upper_cases = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String password = "";

        for (int i = 0; i < digit; i++)
        {
            int rand = (int)(3* Math.random());
            switch (rand)
            {
                case 0:
                    password += String.valueOf((int)(10*Math.random()));
                    break;
                case 1:
//                    password += (int)(lower_cases.length()*Math.random());
                    password += String.valueOf(lower_cases.charAt((int)(lower_cases.length()*Math.random())));
                    break;
                case 2:
//                    password += (int)(upper_cases.length()*Math.random());
                    password += String.valueOf(upper_cases.charAt((int)(lower_cases.length()*Math.random())));
                    break;
            }

        }

        return password;
    }
}