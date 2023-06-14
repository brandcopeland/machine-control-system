package com.example.mgtuv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.net.HttpURLConnection;
//Вспомогательный класс асинхронной авторизации
public class AuthUserTask extends AsyncTask<Void, Void, String> {
    @SuppressLint("StaticFieldLeak")
    private LoginPage loginPage = null;

    public AuthUserTask(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @SuppressLint("StaticFieldLeak")
    private Lobby lobby = null;

    public AuthUserTask(Lobby lobby) {
        this.lobby = lobby;
    }

    static DjangoUser djangoUser;


    @Override
    protected String doInBackground(Void... voids) {
        if (!(loginPage == null)) {
            if (!loginPage.isNeedAutoAuth()) {
                djangoUser = new DjangoUser("https://k7scm.site/");
                djangoUser.auth(loginPage.getLoginString(), loginPage.getPasswordString());
            } else {
                djangoUser = new DjangoUser("https://k7scm.site/", loginPage.getCsrfTokenFromFiles(), loginPage.getSessionIdFromFiles());
            }
        } else { // вход через регенерацию
            djangoUser = new DjangoUser("https://k7scm.site/", lobby.getCsrfTokenFromFiles(), lobby.getSessionIdFromFiles());
        }
        System.out.println("CSRF: ");
        System.out.println(djangoUser.getCsrfToken());
        System.out.println("SESSION ID: ");
        System.out.println(djangoUser.getSessionId());
        System.out.println("ReceivedQrCodeAndTimestamp : ");
        System.out.println(djangoUser.getReceivedQrCodeAndTimestamp());

        djangoUser.getCurrentTime();
        System.out.println("CurrentTime : ");
        System.out.println(djangoUser.currentTime);

        HttpURLConnection conn = djangoUser.getRequest("api/qr");

        //djangoUser.getBody(conn) - Это QRcode + timestamp в ввиде JSON
        //Передаются в метод onPostExecute в виде  String response
        String tempQrCodeAndTimestamp = djangoUser.getBody(conn);
        djangoUser.setReceivedQrCodeAndTimestamp(tempQrCodeAndTimestamp);

        return tempQrCodeAndTimestamp;

    }

    @Override
    protected void onPostExecute(String response) {
        System.out.println(djangoUser.getSessionId());
        if (!(loginPage == null)) {
            if (djangoUser.getSessionId().isEmpty()) {
                Toast.makeText(loginPage, "Authentication failed", Toast.LENGTH_SHORT).show();
                loginPage.setStatusIsNeedAuth(false);
                loginPage.loginErrorUiChange();
            } else {
                loginPage.saveSessionIdCsrfInFiles();
                loginPage.setStatusIsNeedAuth(true);
                Intent intent = new Intent(loginPage, Lobby.class);
                loginPage.startActivity(intent);
            }
        } else {//регенерация через лобби
            if (djangoUser.getSessionId().isEmpty()) {
                lobby.setStatusIsNeedAuth(false);
                Intent intent = new Intent(lobby, LoginPage.class);
                lobby.startActivity(intent);
            } else {
                if (djangoUser.getInternetConnectionErrorStatus()){
                    System.out.println("lobby internet error if button pressed");
                    lobby.qrNoInternetUI();
               }
                else{
                    lobby.qrAccessUI();
                    lobby.showQRCodeUI();
                }

                lobby.setStatusIsNeedAuth(true);
            }
        }
    }
}