package com.example.mgtuv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

//Вспомогательный класс асинхронной авторизации
public class AuthUserTask extends AsyncTask<Void, Void, String> {


    @SuppressLint("StaticFieldLeak")
    private LoginPage loginPage = null;

    public AuthUserTask(LoginPage loginPage, String source) {
        this.loginPage = loginPage;
        this.source = source;
    }

    @SuppressLint("StaticFieldLeak")
    private Lobby lobby = null;

    public AuthUserTask(Lobby lobby, String source) {
        this.lobby = lobby;
        this.source = source;
    }

    String source;
    static DjangoUser djangoUser;


    @Override
    protected String doInBackground(Void... voids) {
        if (source == "LoginPage") { //Если пользователь на странице логина, то ...
            if (!loginPage.isNeedAutoAuth()) { //Если НЕ нужна автоматическая авторизация, то ...
                djangoUser = new DjangoUser("https://k7scm.site/");
                djangoUser.auth(loginPage.getLoginString(), loginPage.getPasswordString()); //Запускаем авторизацию через вручной ввод данных
            } else { //Если нужна автоматическая авторизация, то ...
                //Запускаем авторизацию через  ввод данных из сохраненных файлов юзера
                djangoUser = new DjangoUser("https://k7scm.site/", loginPage.getCsrfTokenFromFiles(), loginPage.getSessionIdFromFiles());
            }
        } else { // Если пользователь не на странице логина, то есть в лобби
            //Запускаем авторизацию через  ввод данных из сохраненных файлов юзера
            djangoUser = new DjangoUser("https://k7scm.site/", lobby.getCsrfTokenFromFiles(), lobby.getSessionIdFromFiles());
        }

        //Делаем GET запрос к API, чтобы получить user info
        Map<String, String> tempParams = new HashMap<>();
        tempParams.put("sessionid", djangoUser.getSessionId());
        djangoUser.setupCookies();
        HttpURLConnection conn = djangoUser.getRequest("api/profile", tempParams);
        djangoUser.setReceivedUserInfo(djangoUser.getBody(conn));
        System.out.println("---User info:");
        System.out.println(djangoUser.getReceivedUserInfo());


        //Делаем GET запрос к API, чтобы получить devices list
        conn = djangoUser.getRequest("api/accesses");
        djangoUser.setReceivedDevicesList(djangoUser.getBody(conn));
        System.out.println("LAME");
        System.out.println(djangoUser.getReceivedDevicesList());



        //djangoUser.getUserInfo();


        //Получаем настоящее время
        djangoUser.getCurrentTime();

        //Делаем GET запрос к API, чтобы получить QR-code
        conn = djangoUser.getRequest("api/qr");

        //djangoUser.getBody(conn) - Это QRcode + timestamp в ввиде JSON
        //Передаются в метод onPostExecute в виде  String response
        String tempQrCodeAndTimestamp = djangoUser.getBody(conn);
        djangoUser.setReceivedQrCodeAndTimestamp(tempQrCodeAndTimestamp);

        return tempQrCodeAndTimestamp;
    }


    @Override
    protected void onPostExecute(String response) { //Функция отвечающая за действия после ответа на запрос к серверу
        if (source == "LoginPage") { //Если пользователь на странице логина, то ...
            if (djangoUser.getReceivedQrCodeAndTimestamp().isEmpty()) { //Если ответ не пришел
                loginPage.loginErrorUiChange();
            } else { //В ином случае сохраняем данные в файлы и переходим в лобби
                loginPage.saveSessionIdCsrfInFiles();
                loginPage.setStatusIsNeedAutoAuth(true);
                Intent intent = new Intent(loginPage, Lobby.class);
                loginPage.startActivity(intent);
            }
        }
         else {// Если пользователь на странице лобби (релогин через лобби при нажатии кнопки), то ...
            if (djangoUser.getReceivedQrCodeAndTimestamp().isEmpty()) { //Если пуст ответ, значит неверный логин или пароль/истек csrf, возвращаем в LoginPage
                Toast.makeText(lobby, "Authentication failed", Toast.LENGTH_SHORT).show();
                lobby.setStatusIsNeedAutoAuth(false);
                Intent intent = new Intent(lobby, LoginPage.class);
                lobby.startActivity(intent);
            } else {
                if (djangoUser.getInternetConnectionErrorStatus()){ //Если у пользователя нет интернета в момент авторизации
                    System.out.println("lobby internet error if button pressed");
                    lobby.homeFragment.setQrTextAccessNoInternet();
               }
                else{ //В ином случае у пользователя показывается QR,  полученный при авторизации (и запускается таймер действия
                    lobby.homeFragment.showQRCodeUI();
                }
                lobby.setStatusIsNeedAutoAuth(true);
            }
        }
    }

}