package com.example.mgtuv2;

import android.os.AsyncTask;

import java.net.HttpURLConnection;

public class AuthUserTask extends AsyncTask<Void, Void, String> {


    @Override
    protected String doInBackground(Void... _) {
        DjangoUser djangoUser = new DjangoUser("https://k7scm.site/");
        System.out.println(djangoUser.getCsrfToken());

        djangoUser.auth(main_login_page.getLoginString(), main_login_page.getPasswordString());

        System.out.println("CSRF: ");
        System.out.println(djangoUser.getCsrfToken());
        System.out.println("SESSION ID: ");
        System.out.println(djangoUser.getSessionId());

        HttpURLConnection conn = djangoUser.getRequest("api/qr");

        //djangoUser.getBody(conn) - Это QRcode + timestamp в ввиде JSON
        //Передаются в метод onPostExecute в виде  String response
        return djangoUser.getBody(conn);

    }

    @Override
    protected void onPostExecute(String response) {
        System.out.println(response);
        main_lobby.getQRCodeTextOutput().setText(response);
    }
}