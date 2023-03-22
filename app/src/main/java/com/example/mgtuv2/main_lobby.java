package com.example.mgtuv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class main_lobby extends AppCompatActivity {

    //random letter generator
    static TextView QRCodeTextOutput;
    Button random_button_generator;

    //qrcode generator
    static ImageView ivOutput;
    String secret_code_for_qr = "SecretCodeForQr1";

    @Override
    //Функция создания лобби. Надо сделать проверку на разрешение доступа
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lobby);

        //random letter generator
        QRCodeTextOutput=findViewById(R.id.QRCodeTextOutput);
        random_button_generator=findViewById(R.id.random_button_generator);
        //qrcode generator
        ivOutput = findViewById(R.id.qrcode_output);


        random_button_generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AuthUserTask AUR = new AuthUserTask();
                AUR.execute();
//                String password = generate_random_password();
//                QRCodeTextOutput.setText(password);
//                ivOutput.setImageBitmap(generate_qrcode_image(password));
            }
        });


    }

    //Функция выхода из учетки. Можно вызывать когда юзер выходит по кнопке, либо истек срок проверки в базе данных
    public void unlogin(View view)
    {
        Toast.makeText(main_lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, main_login_page.class);
        startActivity(intent);
    }

    //Генерирует пароль из 8 символов, возвращает String password
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

    public static Bitmap generate_qrcode_image(String password)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(password,null, QRGContents.Type.TEXT,512);
        // Getting QR-Code as Bitmap
        Bitmap qrBits = qrgEncoder.getBitmap(0);

        return qrBits;
    }


    public static void setQRCodeImageOutputFromString(String inputString){
        ivOutput.setImageBitmap(generate_qrcode_image(inputString));
    }
    public static TextView getQRCodeTextOutput() {
        return QRCodeTextOutput;
    }
}