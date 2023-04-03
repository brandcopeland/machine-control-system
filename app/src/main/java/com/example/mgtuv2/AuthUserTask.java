package com.example.mgtuv2;

import android.os.AsyncTask;
import java.net.HttpURLConnection;

public class AuthUserTask extends AsyncTask<Void, Void, String> {

    private static DjangoUser djangoUser;


    @Override
    protected String doInBackground(Void... _) {
        this.djangoUser = new DjangoUser("https://k7scm.site/");
        System.out.println(djangoUser.getCsrfToken());

        djangoUser.auth(LoginPage.getLoginString(), LoginPage.getPasswordString());

        System.out.println("CSRF: ");
        System.out.println(djangoUser.getCsrfToken());
        System.out.println("SESSION ID: ");
        System.out.println(djangoUser.getSessionId());

        HttpURLConnection conn = djangoUser.getRequest("api/qr");

        //djangoUser.getBody(conn) - Это QRcode + timestamp в ввиде JSON
        //Передаются в метод onPostExecute в виде  String response
        String tempQrCodeAndTimestamp = djangoUser.getBody(conn);
        djangoUser.setReceivedQrCodeAndTimestamp(tempQrCodeAndTimestamp);

        return tempQrCodeAndTimestamp;

    }

    @Override
    protected void onPostExecute(String response) {
        System.out.println(response);
        if (djangoUser.getSessionId().isEmpty()){
            Lobby.getQRCodeTextOutput().setText("Ошибка авторизации");
            Lobby.setQRCodeImageOutput("Error");
        }
        else {
            djangoUser.setupQrCodeAndTimeRange();
            Lobby.getQRCodeTextOutput().setText(String.format("%s\n%s",
                    djangoUser.getQrCode(),
                    Lobby.timestampToTimeString(djangoUser.getTimeExpire())));
            Lobby.setQRCodeImageOutput(djangoUser.getQrCode());
        }
    }
}