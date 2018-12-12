package sirtapp.sirtdetection.com.sirtapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.net.ssl.HttpsURLConnection;


public class Conexion {

    private static String url = "https://vpnservice.ddns.net/flsk";

    public static void takeImage(FileOutputStream image, Context context) throws IOException {
        Conexion.getToken(context);
        Conexion.sendImage(image,context);


    }

    private static void sendImage(FileOutputStream image,Context context) throws IOException {
        String urlSendImage=url+"/sirt";
        SharedPreferences preferences = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        String token = "?token=" + preferences.getString("Token",null);
        URL urlForSendImage = new URL(urlSendImage+token);
        HttpsURLConnection conn = (HttpsURLConnection) urlForSendImage.openConnection();
        conn.setRequestMethod("POST");
        FileChannel in  = new FileInputStream(String.valueOf(image)).getChannel();
        WritableByteChannel out = Channels.newChannel(conn.getOutputStream());

        in.transferTo(0,, out);

    }

    private static void getToken(Context context) throws IOException {
        Object tempToken;
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String uuid = tManager.getDeviceId();
        String urlToken=url+"/device/"+uuid;
       // System.out.print(urlToken);
        URL urlForToken = new URL(urlToken);
        HttpsURLConnection conn = (HttpsURLConnection) urlForToken.openConnection();
        tempToken= conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) tempToken));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        conn.disconnect();
        String json = sb.toString();
        Token token = new Gson().fromJson(json, Token.class);
        SharedPreferences preferences = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token", token.getToken());
        editor.apply();
    }
}
