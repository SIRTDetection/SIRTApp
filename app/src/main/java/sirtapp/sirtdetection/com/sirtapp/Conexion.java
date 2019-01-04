package sirtapp.sirtdetection.com.sirtapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.net.ssl.HttpsURLConnection;



public class Conexion {

    private static ImageView seeImage;

    public Conexion(ImageView image){
        this.seeImage=image;
    }
    private static String url = "https://vpnservice.ddns.net/flsk";

    public static void takeImage(File image, Context context) throws IOException {
        Conexion.getToken(context);
        Conexion.sendImage(image,context);
//        Conexion.reply(context);



    }

    private static void sendImage(final File image, final Context context) throws IOException {
        String urlSendImage=url+"/sirt";
        SharedPreferences preferences = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        String token = "?token=" + preferences.getString("Token",null);
        URL urlForSendImage = new URL(urlSendImage+token);
        System.out.print(urlForSendImage);
        final HttpsURLConnection conn = (HttpsURLConnection) urlForSendImage.openConnection();
        conn.setRequestMethod("POST");

        new Thread(new Runnable() {
            @Override
            public void run() {
                WritableByteChannel out = null;
                FileChannel in = null;
                try {
                    in = new FileInputStream((image)).getChannel();
                    try {
                        out = Channels.newChannel(conn.getOutputStream());


//                        final long size = in.size();
//                        long position = 0;
//                        while (position < size)
//                        {
//                            position += in.transferTo(position, 1024L * 1024L, out);
//                        }


                        in.transferTo(0, in.size(), out);

                     //   in.transferTo(0,999999,out);

                        InputStream preBitmap = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(preBitmap);
                      //  Intent intentLoadNewActivity = new Intent(context,Photo.class);
                        //context.startActivity(intentLoadNewActivity);
                     //   seeImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }).start();

     //   in.transferTo(0,, out);

    }
    public void pasar(){
        Result result = new Result(seeImage);
        result.mostrar();
    }

    private static void getToken(final Context context) throws IOException {
        final Object[] tempToken = new Object[1];
        int permission=0;
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permission);
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    permission);
        //    return;
        }
      //  String uuid = tManager.getDeviceId();
        String uuid = "SirtApp";
                final String urlToken=url+"/device/"+uuid;
       // System.out.print(urlToken);
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL urlForToken = null;
                try {
                    urlForToken = new URL(urlToken);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection conn = null;
                try {
                    conn = (HttpsURLConnection) urlForToken.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    tempToken[0] = conn.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) tempToken[0]));
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }

                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String json = sb.toString();
                Token token = new Gson().fromJson(json, Token.class);
                SharedPreferences preferences = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Token", token.getToken());
                editor.apply();
                conn.disconnect();




                conn.disconnect();


            }
        }).start();

    }




//    public static void reply(final Context context) throws IOException {
//
//        int permission=0;
//        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    permission);
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    permission);
//
//  //          return null;
//
//        }
////        String uuid = tManager.getDeviceId();
//        String uuid="SirtApp";
//        final String urlreply=url+"/device/"+uuid;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Object tempReply = null;
//                SharedPreferences preferences = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
//                String token = "?token=" + preferences.getString("Token", null);
//                URL urlForReply = null;
//                HttpsURLConnection conn = null;
//                StringBuilder sb = null;
//                try {
//                    urlForReply = new URL(urlreply + token);
//
//                    conn = (HttpsURLConnection) urlForReply.openConnection();
//
//                    conn.setRequestMethod("GET");
//
//                    tempReply = conn.getInputStream();
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) tempReply));
//                    sb = new StringBuilder();
//                    String line;
//
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                conn.disconnect();
//                System.out.print(sb.toString());
//
//            }
//        }).start();
//
//
//
//
//    }
}
