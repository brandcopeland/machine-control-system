package com.example.mgtuv2;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class DjangoUser {

    private final Logger logger = Logger.getLogger("DjangoUser");

    private final String siteAddress;
    private String csrfToken = "";
    private String sessionId = "";
    //Получен ли Сессион Айди для проверки на в базе данных ( Если нет- доступа нет)
    public static boolean isSessionIdReceived;

    private static String receivedQRcodeAndTimestamp = "";

    public String getReceivedQRcodeAndTimestamp() {
        return receivedQRcodeAndTimestamp;
    }
    public void setReceivedQRcodeAndTimestamp(String InputQRCodeAndTimeSTamp) {
        receivedQRcodeAndTimestamp = InputQRCodeAndTimeSTamp;
    }

    public static void convertStringJsonReceivedQRCodeAndTimeStampToStringQR() {
        try {
            String jsonString = receivedQRcodeAndTimestamp; // здесь строка JSON
            JSONObject jsonObject = new JSONObject(jsonString); // создаем объект JSON из строки
            String QrCodeFromJSON = jsonObject.getString("code"); // получаем QR Code из JSON
            QrCode = QrCodeFromJSON;
         } catch (JSONException e) {
            e.printStackTrace();
        };
    }

    private static String QrCode = "";

    public static String getQrCode(){
        return QrCode;
    }

    public void setQrCode(String inputQrCode){
        QrCode = inputQrCode;
    }
    private String djangoCookieHeader = "";

    private final String loginUrl;

    public DjangoUser(String siteAddress) {
        if (siteAddress.endsWith("/")) {
            siteAddress = siteAddress.substring(0, siteAddress.length() - 1);
        }
        this.siteAddress = siteAddress;
        this.loginUrl = siteAddress + "/login/";

        setupCSRF();
    }

    public DjangoUser(String siteAddress, String csrfToken, String sessionId) {
        if (siteAddress.endsWith("/")) {
            siteAddress = siteAddress.substring(0, siteAddress.length() - 1);
        }
        this.siteAddress = siteAddress;
        this.csrfToken = csrfToken;
        this.sessionId = sessionId;
        setupCookies();

        this.loginUrl = siteAddress + "/login/";
    }

    public void auth(String username, String password) {
        try {
            URL url = new URL(this.loginUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            data.put("csrfmiddlewaretoken", this.csrfToken);

            Map<String, String> cookies = new HashMap<>();
            cookies.put("csrftoken", this.csrfToken);

            PostData postData = PostData.fromMap(data);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", postData.length);

            conn.setRequestProperty("Referer", this.loginUrl);

            conn.setRequestProperty("Cookie", generateCookieHeader(cookies));
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postData.bytes);

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader == null) {
                logger.severe("Отсутствует куки данные");
                return;
            }

            Map<String, String> token;
            for (String cookie : cookiesHeader) {
                token = cookieToMap(cookie);
                if (!token.isEmpty()) {
                    if (token.containsKey("csrftoken")) {
                        this.csrfToken = token.get("csrftoken");
                    }
                    if (token.containsKey("sessionid")) {
                        this.sessionId = token.get("sessionid");
                    }
                }
            }

            if (this.csrfToken.isEmpty()) {
                logger.severe("CSRF токен отсутствует");
            } else if (this.sessionId.isEmpty()) {
                logger.severe("sessionId отсутствует");
                isSessionIdReceived = false;
                System.out.println(" isSessionIdReceived = false");
            } else {
                System.out.println(" isSessionIdReceived = true");
                isSessionIdReceived = true;
                setupCookies();
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.severe("Авторизация не удалась");
    }

    private void setupCSRF() {
        this.csrfToken = getCSRF();
    }

    private String getCSRF() {
        String token = "";
        try {
            URL url = new URL(this.loginUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader == null) {
                return token;
            }
            for (String cookie : cookiesHeader) {
                token = getCSRFTokenFromCookie(cookie);
                if (!token.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe("Неудалось подключиться к сайту");
        }
        return token;
    }

    public Map<String, String> cookieToMap(String cookies) {
        Map<String, String> cookieMap = new HashMap<String, String>();
        for (String cookie : cookies.split(";")) {
            cookie = cookie.trim();
            if (cookie.contains("=")) {
                cookieMap.put(cookie.substring(0, cookie.indexOf("=")), cookie.substring(cookie.indexOf("=") + 1));
            }
        }
        return cookieMap;
    }

    public String getCSRFTokenFromCookie(String cookie) {
        Map<String, String> cookies = cookieToMap(cookie);
        if (cookies.containsKey("csrftoken")) {
            return cookies.get("csrftoken");
        }
        return "";
    }

    public String generateCookieHeader(Map<String, String> cookies) {
        if (cookies.isEmpty()) {
            return "";
        }
        StringBuilder cookieHeader = new StringBuilder("; ");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookieHeader.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }
        return cookieHeader.substring(0, cookieHeader.length() - 2);
    }

    public String generateGetParams(Map<String, String> params) {
        return PostData.mapToUTF8String(params);
    }

    public String getBody(HttpURLConnection conn) {
        BufferedReader br;
        StringBuilder sj = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sj.append(inputLine);
            }
            br.close();
        } catch (IOException e) {
            sj = new StringBuilder();
            try {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    sj.append(inputLine);
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return sj.toString();
    }

    public HttpURLConnection postRequest(String address) {
        return postRequest(address, Collections.emptyMap());
    }

    public HttpURLConnection postRequest(String address, Map<String, String> params) {
        HttpURLConnection conn = null;
        address = "/" + formatAddress(address);
        try {
            URL url = new URL(this.siteAddress + address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cookie", this.djangoCookieHeader);
            conn.setInstanceFollowRedirects(false);
            if (!params.isEmpty()) {
                PostData postData = PostData.fromMap(params);

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", postData.length);
                conn.setDoOutput(true);
                conn.getOutputStream().write(postData.bytes);
            }
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public HttpURLConnection getRequest(String address) {
        return getRequest(address, Collections.emptyMap());
    }

    public HttpURLConnection getRequest(String address, Map<String, String> params) {
        HttpURLConnection conn = null;
        address = "/" + formatAddress(address);
        try {
            URL url;
            if (params.isEmpty()) {
                url = new URL(this.siteAddress + address);
            } else {
                url = new URL(this.siteAddress + address + "?" + generateGetParams(params));
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Cookie", this.djangoCookieHeader);
            conn.setInstanceFollowRedirects(false);

            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String formatAddress(String address) {
        if (address.startsWith("/")) {
            address = address.substring(1);
        }
        if (address.endsWith("/")) {
            address = address.substring(0, address.length() - 1);
        }
        return address;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDjangoCookieHeader() {
        return djangoCookieHeader;
    }

    private void setupCookies() {
        Map<String, String> djangoCookies = new HashMap<>();
        djangoCookies.put("csrftoken", this.csrfToken);
        djangoCookies.put("sessionid", this.sessionId);


        this.djangoCookieHeader = generateCookieHeader(djangoCookies);
    }

    private static class PostData {
        public byte[] bytes;
        public String length;

        public PostData(byte[] bytes, String length) {
            this.bytes = bytes;
            this.length = length;
        }

        public static String mapToUTF8String(Map<String, String> params) {
            if (params.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
                    String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                    sb.append(key).append("=").append(value).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return sb.substring(0, sb.length() - 1);
        }

        public static PostData fromMap(Map<String, String> params) {
            String postData = mapToUTF8String(params);

            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            String postDataLength = Integer.toString(postDataBytes.length);
            return new PostData(postDataBytes, postDataLength);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PostData postData = (PostData) o;
            return Arrays.equals(this.bytes, postData.bytes) && this.length.equals(postData.length);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(this.length);
            result = 12 * result + Arrays.hashCode(bytes);
            return result;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("PostData{bytes=%s, length=%s}", Arrays.toString(bytes), length);
        }
    }
}
