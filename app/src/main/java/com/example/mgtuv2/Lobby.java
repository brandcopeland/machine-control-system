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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class Lobby extends AppCompatActivity {

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
            }
        });


    }

    //Функция выхода из учетки. Можно вызывать когда юзер выходит по кнопке, либо истек срок проверки в базе данных
    public void unlogin(View view)
    {
        DjangoUser.resetSessionId();
        Toast.makeText(Lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public static Bitmap generateQrCodeImage(String password)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(password,null, QRGContents.Type.TEXT,512);
        // Getting QR-Code as Bitmap
        Bitmap qrBits = qrgEncoder.getBitmap(0);

        return qrBits;
    }


    public static void setQRCodeImageOutput(String inputString){
        ivOutput.setImageBitmap(generateQrCodeImage(inputString));
    }
    public static TextView getQRCodeTextOutput() {
        return QRCodeTextOutput;
    }

    public static String timestampToTimeString(String timestamp){
        long timestampSeconds = Long.parseLong(timestamp); // Replace with your timestamp in seconds
        Date date = new Date(timestampSeconds * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // Define the date format
        sdf.setTimeZone(TimeZone.getDefault()); // Set the time zone to the default time zone of the device
        String localTimeString1 = sdf.format(date); // Convert the date to a local time string
        return localTimeString1;
    }
}