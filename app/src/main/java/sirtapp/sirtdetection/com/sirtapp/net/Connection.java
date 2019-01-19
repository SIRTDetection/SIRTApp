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

import com.github.javinator9889.threading.threads.notifyingthread.NotifyingThread;
import com.github.javinator9889.threading.threads.notifyingthread.OnThreadCompletedListener;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import sirtapp.sirtdetection.com.sirtapp.R;
import sirtapp.sirtdetection.com.sirtapp.activities.MainActivity;
import sirtapp.sirtdetection.com.sirtapp.utils.progress.ProgressDialog;
/**
 * Esta clase lleva a cabo las conexiones necesarias entre la app y el servidor
 */
public class Connection implements OnThreadCompletedListener {
    public static final String BASE_URL = "https://vpnservice.ddns.net/flsk";
    private static final String TAG = "Connection";
    private Context mContext;
    private AtomicReference<Bitmap> mResult;
    private MainActivity mInstance;
    private File mImageFile;
    private ProgressDialog mDialog;

    public Connection(@NonNull Context context) {
        mContext = context;
    }

    public void startInference(@NonNull String deviceUuid,
                               @NonNull File imageFile,
                               @NonNull ProgressDialog dialog,
                               @NonNull MainActivity instance) {
        mImageFile = imageFile;
        mInstance = instance;
        mDialog = dialog;
        obtainToken(deviceUuid);
    }
    /**
     * Este metodo obtiene el token y lo guarda en la sharedpreferences
     */

    public void obtainToken(@NonNull String deviceUuid) {
        NotifyingThread connectionThread = new NotifyingThread(() -> {
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
                SharedPreferences preferences = mContext.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                String prefsToken = preferences.getString("token", null);
                if ((prefsToken == null) || !prefsToken.equals(token)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.apply();
                }
            } catch (IOException ex) {
                Log.w(TAG, "Exception occurred during connection opening", ex);
            } catch (JSONException jsonEx) {
                Log.w(TAG, "JSONException occurred...", jsonEx);
            }
        }, this);
        connectionThread.setName("connectionThread");
        connectionThread.start();
    }
    /**
     * Este otro lo que hace es cread el thread para subir la imagen, cosa que hara el siguiente, despues de subir y verificar
     * que el token es correcto
     */
    public void uploadPicture() {
        NotifyingThread httpsThread = new NotifyingThread(this);
        mResult = new AtomicReference<>();
        httpsThread.setExecutable(this::uploadPictureTask, mResult);
        httpsThread.setName("httpsThread");
        httpsThread.start();
    }

    private Bitmap uploadPictureTask() {
        String token = mContext.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                .getString("token", null);
        String uploadURL = BASE_URL + "/sirt?token=" + token;
        try {
            URL url = new URL(uploadURL);
            Log.d(TAG, String.format("Opening connection to URL: %s", url.toString()));
            mInstance.runOnUiThread(() -> mDialog.updateBody(R.string.dialog_content_2));
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
                    "filename=\"" + mImageFile.getName() + "\"\r\n\r\n");
            request.write(FileUtils.readFileToByteArray(mImageFile));
            request.writeBytes("\r\n");
            request.writeBytes("--" + boundary + "--\r\n");
            request.flush();
            Log.d(TAG, "Data uploaded!");
            mInstance.runOnUiThread(() -> mDialog.updateBody(R.string.dialog_content_3));

            int response = connection.getResponseCode();
            Log.d(TAG, "Server response: " + response);

            if (response != 200)
                Log.w(TAG, String.format("Server replied error %d", response));
            else {
                Log.d(TAG, "HTTPS OK");
                InputStream body = connection.getInputStream();
                return BitmapFactory.decodeStream(body);
            }
        } catch (MalformedURLException mue) {
            Log.e(TAG, "The URL is not correctly set-up", mue);
        } catch (IOException io) {
            Log.e(TAG, "An exception while reading/writing occurred", io);
        }
        return null;
    }

    /**
     * Aqui dependiendo del thread que se vaya a ejecutar se llamaran a unos metodos u otros
     */
    @Override
    public void onThreadCompletedListener(@NotNull Thread thread, @Nullable Throwable exception) {
        if (exception != null)
            Log.w(TAG, "Exception occurred during thread execution", exception);
        switch (thread.getName()) {
            case "httpsThread":
                mInstance.onImageDownloadCompleted(mResult.get(), mImageFile);
                break;
            case "connectionThread":
                uploadPicture();
                break;
            default:
                Log.w(TAG, "Unexpected case - thread info: " + thread);
                break;
        }
    }
}
