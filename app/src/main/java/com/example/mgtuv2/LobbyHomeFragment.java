package com.example.mgtuv2;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.mgtuv2.AuthUserTask.djangoUser;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

//Класс фрагмента начальной страницы в активности лобби
public class LobbyHomeFragment extends Fragment {

    public TextView lobbyTextAccessStatus;
    public Button buttonRefreshQrCode;
    public TextView QrCodeTextOutput;
    public ImageView QrCodeImageOutput;
    public ProgressBar progressBar;
    public CountDownTimer  timer;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("fragmentOnCreateView");
        view = inflater.inflate(R.layout.fragment_lobby_home, container, false);

        lobbyTextAccessStatus = view.findViewById(R.id.lobbyTextAccessStatus);
        QrCodeTextOutput = view.findViewById(R.id.QRCodeTextOutput);
        buttonRefreshQrCode = view.findViewById(R.id.buttonRefreshQrCode);
        QrCodeImageOutput = view.findViewById(R.id.QrCodeImageOutput);
        progressBar = view.findViewById(R.id.progressBar);

        buttonRefreshQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Lobby activity = (Lobby) getActivity();
                AuthUserTask AUR = new AuthUserTask(activity,"LobbyHomeFragment");
                AUR.execute();
            }
        });

        showQRCodeUI();

        return view;
    }


    //Функция запуска таймера
    public void startTimer(long inputTime){
        System.out.println("Timer Started");
        int maxTime = (Integer.parseInt(djangoUser.getTimeExpire())-Integer.parseInt(djangoUser.getTimeStart()))/100;
        buttonRefreshQrCode.setEnabled(false);
        timer = new CountDownTimer(inputTime*1000, 1000) {

            public void onTick(long l) {
                TextView tempText = view.findViewById(R.id.QRCodeTextOutput);
                tempText.setText(" " + l/ 1000);
                progressBar.setProgress((int) l/1000/maxTime);

            }

            public void onFinish() {
                QrCodeTextOutput.setText("QR код истёк");
                setQrTextAccessFalse();
                buttonRefreshQrCode.setEnabled(true);
            }
        };
        timer.start();
    }


    //_____Функции изменения UI приложения____

    //Функция изменения UI текста статуса QR, если QR работает
    public void setQrTextAccessTrue(){
        lobbyTextAccessStatus.setText("QR код  работает");
        lobbyTextAccessStatus.setTextColor(view.getResources().getColor(R.color.access_green));
    }
    //Функция изменения UI текста статуса QR, если QR не работает
    public void setQrTextAccessFalse(){
        lobbyTextAccessStatus.setText("QR код не работает");
        lobbyTextAccessStatus.setTextColor(view.getResources().getColor(R.color.access_red));
    }

    //Функция изменения UI если нет интернета
    public void setQrTextAccessNoInternet(){
        lobbyTextAccessStatus.setText("Нет подключения к интернету");
        lobbyTextAccessStatus.setTextColor(view.getResources().getColor(R.color.access_red));
    }

    //Функция изменения UI (Показ изображения QR) (Вспомогательная функция)
    public void setQRCodeImageOutput(String inputString){
        QrCodeImageOutput.setImageBitmap(generateQrCodeImage(inputString));
    }

    //Функция изменения UI (Показ изображения QR)
    public void showQRCodeUI(){
        setQrTextAccessTrue();
        setQRCodeImageOutput(djangoUser.getQrCode());
        //Время действия таймера = время истечения действия QR - настоящее время
        long timerTime = Long.parseLong(djangoUser.getTimeExpire())-djangoUser.currentTime/1000;
        startTimer(timerTime);
    }

    //Функция генерации изображения QR кода из строки
    public static Bitmap generateQrCodeImage(String password)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(password,null, QRGContents.Type.TEXT,512);
        // Getting QR-Code as Bitmap

        return qrgEncoder.getBitmap(0);
    }
}

