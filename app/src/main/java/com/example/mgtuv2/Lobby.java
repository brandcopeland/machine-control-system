package com.example.mgtuv2;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    TextView lobbyTextAccessStatus;
    static ImageView QrCodeImageOutput;
    ProgressBar progressBar;

    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lobby);

        lobbyTextAccessStatus=findViewById(R.id.lobbyTextAccessStatus);
        QRCodeTextOutput=findViewById(R.id.QRCodeTextOutput);
        buttonRefreshQrCode =findViewById(R.id.buttonRefreshQrCode);
        QrCodeImageOutput = findViewById(R.id.QrCodeImageOutput);
        progressBar = findViewById(R.id.progressBar);

        if (djangoUser.getInternetConnectionErrorStatus()){
            System.out.println("lobby internet error no button pressed");
            lobbyTextAccessStatus.setText("no internet");
        }
        else{
            lobbyTextAccessStatus.setText("Разрешен");
        }
        showQRCodeUI();

        buttonRefreshQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AuthUserTask AUR = new AuthUserTask(Lobby.this);
                AUR.execute();
            }
        });


    }

    public void startTimer(long inputTime){
        int maxTime = (Integer.parseInt(djangoUser.getTimeExpire())-Integer.parseInt(djangoUser.getTimeStart()))/100;
        buttonRefreshQrCode.setEnabled(false);
        new CountDownTimer(inputTime*1000, 1000) {

            public void onTick(long l) {
                getQRCodeTextOutput().setText(" " + l/ 1000);
                progressBar.setProgress((int) l/1000/maxTime);
            }

            public void onFinish() {
                getQRCodeTextOutput().setText("QR код истёк");
                lobbyTextAccessStatus.setText("Запрещен");
                buttonRefreshQrCode.setEnabled(true);
            }
        }.start();
    }


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
//        Lobby.getQRCodeTextOutput().setText(String.format("%s\n%s",
//                djangoUser.getQrCode(),
//                Lobby.timestampToTimeString(djangoUser.getTimeExpire())));
//        System.out.println("qr code");
//        System.out.println(djangoUser.getQrCode());
//
        Lobby.setQRCodeImageOutput(djangoUser.getQrCode());
        long timestamp = new Date().getTime();
        System.out.println(timestamp);
        System.out.println(Long.parseLong(djangoUser.getTimeExpire()));
        startTimer(Long.parseLong(djangoUser.getTimeExpire())-timestamp/1000);

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