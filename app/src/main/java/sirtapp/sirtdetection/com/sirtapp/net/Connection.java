/*
 * Copyright © 2019 - present | sirtapp.sirtdetection.com.sirtapp by Javinator9889

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.

 * Created by Javinator9889 on 2/01/19 - SecurePass.
 */
package sirtapp.sirtdetection.com.sirtapp.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import sirtapp.sirtdetection.com.sirtapp.activities.MainActivity;

public class Connection {
    public static final String BASE_URL = "https://vpnservice.ddns.net/flsk";
    private static final String TAG = "Connection";
    private Context mContext;

    public Connection(@NonNull Context context) {
        mContext = context;
    }

    public void obtainToken(@NonNull String deviceUuid) {
        Thread connectionThread = new Thread(() -> {
            try {
                URL url = null;
                try {
                    url = new URL(BASE_URL + "/device/" + deviceUuid);
                    Log.d(TAG, "Connection to URL: " + url);
                } catch (MalformedURLException ignored) {
                }
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder jsonBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line).append("\n");
                }
                Log.i(TAG, jsonBuilder.toString());
                JSONObject json = new JSONObject(jsonBuilder.toString());
                String token = json.getString("token");
                SharedPreferences.Editor editor = mContext.getSharedPreferences("userPrefs",
                        Context.MODE_PRIVATE).edit();
                editor.putString("token", token);
                editor.apply();
            } catch (IOException ex) {
                Log.w(TAG, "Exception occurred during connection opening", ex);
            } catch (JSONException jsonEx) {
                Log.w(TAG, "JSONException occurred...", jsonEx);
            }
        });
        connectionThread.start();
    }

    public void uploadPicture(@NonNull File imageFile, MainActivity instance) {
        String token = mContext.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                .getString("token", null);
        String uploadURL = BASE_URL + "/sirt?token=" + token;
        final Bitmap[] imageBitmap = new Bitmap[1];
        Thread httpsThread = new Thread(() -> {
            try {
                URL url = new URL(uploadURL);
                Log.d(TAG, String.format("Opening connection to URL: %s", url.toString()));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                String boundary = UUID.randomUUID().toString();
                Log.d(TAG, "Connection boundary: " + boundary);
                Log.d(TAG, "Setting up connection protocols");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                Log.d(TAG, "Obtaining the output stream");
                DataOutputStream request = new DataOutputStream(connection.getOutputStream());
                request.writeBytes("--" + boundary + "\r\n");
                request.writeBytes("Content-Disposition: form-data; " +
                        "name=\"picture\"; " +
                        "filename=\"" + imageFile.getName() + "\"\r\n\r\n");
                request.write(FileUtils.readFileToByteArray(imageFile));
                request.writeBytes("\r\n");
                request.writeBytes("--" + boundary + "--\r\n");
                request.flush();
                Log.d(TAG, "Data uploaded!");

                int response = connection.getResponseCode();
                Log.d(TAG, "Server response: " + response);

                if (response != 200)
                    Log.w(TAG, String.format("Server replied error %d", response));
                else {
                    Log.d(TAG, "HTTPS OK");
                    InputStream body = connection.getInputStream();
                    imageBitmap[0] = BitmapFactory.decodeStream(body);
                }
            } catch (MalformedURLException mue) {
                Log.e(TAG, "The URL is not correctly set-up", mue);
            } catch (IOException io) {
                Log.e(TAG, "An exception while reading/writing occurred", io);
            }
        });
        httpsThread.start();
        try {
            httpsThread.join();
            instance.onImageDownloadCompleted(imageBitmap[0]);
        } catch (InterruptedException ignored) {
        }
    }
}
