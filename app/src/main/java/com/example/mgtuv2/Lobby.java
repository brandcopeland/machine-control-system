package com.example.mgtuv2;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    static TextView QRCodeTextOutput;
    Button buttonRefreshQrCode;

    static ImageView QrCodeImageOutput;

    SharedPreferences sPref;
    @Override
    //Функция создания лобби. Надо сделать проверку на разрешение доступа
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lobby);

        //random letter generator
        QRCodeTextOutput=findViewById(R.id.QRCodeTextOutput);
        buttonRefreshQrCode =findViewById(R.id.buttonRefreshQrCode);
        //qrcode generator
        QrCodeImageOutput = findViewById(R.id.QrCodeImageOutput);
        showQRCodeUI();

        buttonRefreshQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AuthUserTask AUR = new AuthUserTask(Lobby.this);
                AUR.execute();
                //showQRCodeUI();
            }
        });


    }

    //Функция выхода из учетки. Можно вызывать когда юзер выходит по кнопке, либо истек срок проверки в базе данных
    public void unlogin(View view)
    {
        setStatusIsNeedAuth(false);
        DjangoUser.resetSessionId();
        Toast.makeText(Lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public static Bitmap generateQrCodeImage(String password)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(password,null, QRGContents.Type.TEXT,512);
        // Getting QR-Code as Bitmap

        return qrgEncoder.getBitmap(0);
    }

    public void showQRCodeUI(){

        djangoUser.setupQrCodeAndTimeRange();
        Lobby.getQRCodeTextOutput().setText(String.format("%s\n%s",
                djangoUser.getQrCode(),
                Lobby.timestampToTimeString(djangoUser.getTimeExpire())));
        System.out.println("qr code");
        System.out.println(djangoUser.getQrCode());
        Lobby.setQRCodeImageOutput(djangoUser.getQrCode());
    }

    public static void setQRCodeImageOutput(String inputString){
        QrCodeImageOutput.setImageBitmap(generateQrCodeImage(inputString));
    }
    public static TextView getQRCodeTextOutput() {
        return QRCodeTextOutput;
    }

    public static String timestampToTimeString(String timestamp){
        if (timestamp.equals("")) {
            return "";
        }
        long timestampSeconds = Long.parseLong(timestamp); // Replace with your timestamp in seconds
        Date date = new Date(timestampSeconds * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // Define the date format
        sdf.setTimeZone(TimeZone.getDefault()); // Set the time zone to the default time zone of the device
        return sdf.format(date); //local time string
    }

    public void setStatusIsNeedAuth(Boolean inputBoolean){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNeedAuth", inputBoolean);
        ed.apply();
    }

    public Boolean isNeedAutoAuth(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        return sPref.getBoolean("isNeedAuth", false);
    }

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
}