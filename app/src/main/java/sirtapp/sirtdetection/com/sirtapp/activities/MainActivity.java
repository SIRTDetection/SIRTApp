package sirtapp.sirtdetection.com.sirtapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import sirtapp.sirtdetection.com.sirtapp.R;
import sirtapp.sirtdetection.com.sirtapp.net.Connection;
import sirtapp.sirtdetection.com.sirtapp.utils.images.ImageManager;

import static android.provider.MediaStore.EXTRA_OUTPUT;
import static sirtapp.sirtdetection.com.sirtapp.utils.images.ImageUtils.getPathFromUri;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    private static final short INTENT_OPEN_GALLERY = 1;
    private static final short INTENT_TAKE_PHOTO = 2;
    private static final String TAG = "MainActivity";
    private File mImageFile = null;
    private ImageView mImageView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cameraButton = findViewById(R.id.camera);
        mImageView = findViewById(R.id.image);
        Button galleryButton = findViewById(R.id.gallery);

        galleryButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
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

    public void onImageDownloadCompleted(@NonNull Bitmap imageBitmap) {
        mImageView.setImageBitmap(imageBitmap);
    }

    private void uploadImage(File imageFile) {
        Connection connection = new Connection(this);
        connection.obtainToken("J9889");
        connection.uploadPicture(imageFile, this);
    }

    private void loadImage(Intent data) {
        Uri imageUri = data.getData();
        File file = new File(getPathFromUri(this, imageUri));

        uploadImage(file);
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
                                .setTitle("Error")
                                .setMessage("Error al crear el archivo donde se guardará la imagen")
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
            default:
                Log.w(TAG, "Unhandled case at \"onClick\": " + view.toString());
                break;
        }
    }
}