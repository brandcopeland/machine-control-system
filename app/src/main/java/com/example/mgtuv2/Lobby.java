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

//Класс страницы лобби
public class Lobby extends AppCompatActivity {

    static TextView QRCodeTextOutput;
    Button buttonRefreshQrCode;
    TextView lobbyTextAccessStatus;
    static ImageView QrCodeImageOutput;
    ProgressBar progressBar;
    CountDownTimer  timer;

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
            qrNoInternetUI();
        }
        else{
            System.out.println("checkpoint 6");
            qrAccessUI();
            showQRCodeUI();
        }


        buttonRefreshQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AuthUserTask AUR = new AuthUserTask(Lobby.this);
                AUR.execute();
            }
        });


    }
    //Функция запуска таймера
    public void startTimer(long inputTime){
        int maxTime = (Integer.parseInt(djangoUser.getTimeExpire())-Integer.parseInt(djangoUser.getTimeStart()))/100;
        buttonRefreshQrCode.setEnabled(false);
        timer = new CountDownTimer(inputTime*1000, 1000) {

            public void onTick(long l) {
                getQRCodeTextOutput().setText(" " + l/ 1000);
                progressBar.setProgress((int) l/1000/maxTime);

            }

            public void onFinish() {
                getQRCodeTextOutput().setText("QR код истёк");
                qrNoAccessUI();
                buttonRefreshQrCode.setEnabled(true);
            }
        };
        timer.start();
    }
    //Функция изменения UI если QR истек или не работает
    public void qrNoAccessUI(){
        lobbyTextAccessStatus.setText("QR код не работает");
        lobbyTextAccessStatus.setTextColor(getResources().getColor(R.color.access_red));
    }
    //Функция изменения UI если нет интернета
    public void qrNoInternetUI(){
        lobbyTextAccessStatus.setText("Нет подключения к интернету");
        lobbyTextAccessStatus.setTextColor(getResources().getColor(R.color.access_red));
    }
    //Функция изменения UI если QR работает
    public void qrAccessUI(){
        lobbyTextAccessStatus.setText("QR код  работает");
        lobbyTextAccessStatus.setTextColor(getResources().getColor(R.color.access_green));
    }
    //Функция выхода из аккаунта / перехода на страницу логина
    public void unlogin(View view)
    {
        timer.cancel();
        setStatusIsNeedAuth(false);
        DjangoUser.resetSessionId();
        Toast.makeText(Lobby.this, "UNLOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
    //Функция генерации изображения QR кода из строки
    public static Bitmap generateQrCodeImage(String password)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(password,null, QRGContents.Type.TEXT,512);
        // Getting QR-Code as Bitmap

        return qrgEncoder.getBitmap(0);
    }
    //Функция изменения UI (Показ изображения QR)
    public void showQRCodeUI(){
        djangoUser.setupQrCodeAndTimeRange();
        Lobby.setQRCodeImageOutput(djangoUser.getQrCode());
        long timestamp = djangoUser.currentTime;
        System.out.println(timestamp);
        System.out.println(Long.parseLong(djangoUser.getTimeExpire()));
        startTimer(Long.parseLong(djangoUser.getTimeExpire())-timestamp/1000);
    }
    //Функция изменения UI (Показ изображения QR) (Вспомогательная функция)
    public static void setQRCodeImageOutput(String inputString){
        QrCodeImageOutput.setImageBitmap(generateQrCodeImage(inputString));
    }
    //Получение текстового поля под Qr кодом
    public static TextView getQRCodeTextOutput() {
        return QRCodeTextOutput;
    }
    //timestamp в строку отображения времени
    public static String timestampToTimeString(String timestamp){
        if (timestamp.equals("")) {
            return "";
        }
        long timestampSeconds = Long.parseLong(timestamp);
        Date date = new Date(timestampSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
    //Сохранения статуса необходимости автоматической авторизации
    public void setStatusIsNeedAuth(Boolean inputBoolean){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNeedAuth", inputBoolean);
        ed.apply();
    }
    //Получение статуса необходимости автоматической авторизации
    public Boolean isNeedAutoAuth(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        return sPref.getBoolean("isNeedAuth", false);
    }
    //Получение sessionID  из файла
    public String getSessionIdFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedSessionIdTemp = sPref.getString("savedSessionId", "");
        return savedSessionIdTemp;
    }
    //Получение csrf  из файла
    public String getCsrfTokenFromFiles(){
        sPref = getSharedPreferences("savedSessionIdCsrf", MODE_PRIVATE);
        String savedCsrfTemp = sPref.getString("savedCsrf", "");
        return savedCsrfTemp;
    }
}