package sirtapp.sirtdetection.com.sirtapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import sirtapp.sirtdetection.com.sirtapp.R;
import sirtapp.sirtdetection.com.sirtapp.net.Connection;
import sirtapp.sirtdetection.com.sirtapp.utils.images.ImageManager;
import sirtapp.sirtdetection.com.sirtapp.utils.progress.ProgressDialog;

import static android.provider.MediaStore.EXTRA_OUTPUT;
import static sirtapp.sirtdetection.com.sirtapp.utils.images.ImageManager.getExternalStorageDir;
import static sirtapp.sirtdetection.com.sirtapp.utils.images.ImageUtils.getPathFromUri;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    private static final short INTENT_OPEN_GALLERY = 1;
    private static final short INTENT_TAKE_PHOTO = 2;
    private static final String TAG = "MainActivity";
    private String mUUID;
    private File mImageFile = null;
    private ImageView mImageView;
    private ProgressDialog mDialog;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        mUUID = preferences.getString("UUID", null);
        if (mUUID == null) {
            mUUID = UUID.randomUUID().toString();
            preferences.edit().putString("UUID", mUUID).apply();
        }
        Button cameraButton = findViewById(R.id.camera);
        mImageView = findViewById(R.id.image);
        Button galleryButton = findViewById(R.id.gallery);


        galleryButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        mImageView.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_TAKE_PHOTO:
                    if (mImageFile != null)
                        uploadImage(mImageFile);
                    mImageFile = null;
                    break;
                case INTENT_OPEN_GALLERY:
                    loadImage(data);
                    break;
                default:
                    Log.w(TAG, "Unhandled value at switch for \"onActivityResult\": " + requestCode);
                    break;
            }
        }
    }

    public void onImageDownloadCompleted(Bitmap imageBitmap, File source) {
        runOnUiThread(() -> {
            mImageView.setImageBitmap(imageBitmap);
            File tempFile = new File(getExternalStorageDir(), source.getName().replace("-org", "-inf"));
            Log.d(TAG, tempFile.getAbsolutePath());
            if (mDialog != null)
                mDialog.dismiss();
            if (imageBitmap == null) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.error_server_response)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (self, which) -> self.dismiss())
                        .create().show();
                return;
            }
            try {
                tempFile.createNewFile();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(tempFile));
                Intent showBigImageIntent = new Intent(this, ImageViewer.class);
                showBigImageIntent.putExtra("picture", tempFile);
                startActivity(showBigImageIntent);
            } catch (IOException e) {
                Log.w(TAG, "Unexpected exception!", e);
            }
        });
    }

    private void uploadImage(File imageFile) {
        Connection connection = new Connection(this);
        mDialog = new ProgressDialog(this,
                R.string.dialog_title,
                R.string.dialog_content_1);
        mDialog.show();
        connection.startInference(mUUID, imageFile, mDialog, this);
//        connection.obtainToken(mUUID);
//        connection.uploadPicture(imageFile, mDialog, this);
    }

    private void loadImage(Intent data) {
        Uri imageUri = data.getData();
        try {
            File file = new File(getPathFromUri(this, imageUri));
            uploadImage(file);
        } catch (RuntimeException ignored) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.error_no_pic)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.ok),
                            (self, which) -> self.dismiss())
                    .setCancelable(true)
                    .create();
            dialog.show();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery:
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, INTENT_OPEN_GALLERY);
                break;
            case R.id.camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    ImageManager manager = new ImageManager(this);
                    try {
                        photoFile = manager.createNewFile();
                    } catch (IOException ex) {
                        Log.e(TAG, "Error while creating new file. Are permissions OK?", ex);
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(R.string.error_title)
                                .setMessage(R.string.error_save_file)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(getString(android.R.string.ok),
                                        (self, which) -> self.dismiss())
                                .setCancelable(true)
                                .create();
                        dialog.show();
                    }
                    if (photoFile != null) {
                        mImageFile = photoFile;
                        Uri imageUri = FileProvider.getUriForFile(
                                this,
                                "sirtapp.sirtdetection.com.sirtapp",
                                photoFile);
                        takePictureIntent.putExtra(EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, INTENT_TAKE_PHOTO);
                        break;
                    }
                }
           case R.id.image:
               this.showZoom();
               break;
            default:
                Log.w(TAG, "Unhandled case at \"onClick\": " + view.toString());
                break;
        }
    }

    private void showZoom() {
        File storageDir = getExternalStorageDir();
        File tempFile =this.lastFileModified(storageDir.getPath());
        try {

            Intent showBigImageIntent = new Intent(this, ImageViewer.class);
            showBigImageIntent.putExtra("picture", tempFile);
            startActivity(showBigImageIntent);
        } catch (UndeclaredThrowableException e) {
            Log.w(TAG, "Unexpected exception!", e);
        }

    }
    public static File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;

    }
}