package sirtapp.sirtdetection.com.sirtapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static android.app.PendingIntent.getActivity;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoomConfig;
import ozaydin.serkan.com.image_zoom_view.SaveFileListener;
public class MainActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver
        .Delegate {
    private static final int INTENT_TAKE_PHOTO = 3;
    private final SingleUploadBroadcastReceiver mUploadBroadcastReceiver = new
            SingleUploadBroadcastReceiver();
    private File mImageFile = null;
    int tipo;
    Uri imageUri;
    private TextView textReply;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    ImageView image;

    @Override
    protected void onResume() {
        super.onResume();
        mUploadBroadcastReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUploadBroadcastReceiver.unregister(this);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tipo=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1=(Button)findViewById(R.id.camera);
        image =(ImageView)findViewById(R.id.image);
        Button button2=(Button)findViewById(R.id.gallery);
    //    textReply=(TextView)findViewById(R.id.textReply);


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo=1;
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,1);

            }
        });



        button1.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                ImageManager manager = new ImageManager(this);
                try {
                    photoFile = manager.createNewFile();
                } catch (IOException ex) {
                    Log.e("MainActivity", "Error while creating new file. Are permissions OK?", ex);
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
//                takePictureIntent.putExtra(EXTRA_OUTPUT, photoUri.toString());
//                takePictureIntent.putExtra(EXTRA_OUTPUT, photoUri.toString());
                    takePictureIntent.putExtra(EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, INTENT_TAKE_PHOTO);
                }
            }
        /*tipo=0;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.setType("image/*");
        startActivityForResult(intent,0);*/

        });

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_TAKE_PHOTO:
//                    Bundle extras = data.getExtras();
//                    Uri photoUri = extras.getParcelable(EXTRA_OUTPUT);
//                    File photoFile = (File) data.getSerializableExtra("file");
//                    Uri photoUri = Uri.parse(data.getStringExtra(EXTRA_OUTPUT));
                    mUploadBroadcastReceiver.setDelegate(this);
                    mUploadBroadcastReceiver.setUploadID("J9889");
                    if (mImageFile != null) {
                        uploadImage(mImageFile);
                        /*try {
//                            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance
//                                    (mImageFile.getAbsolutePath(), true);
//                            Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
                            Bitmap region = MediaStore.Images.Media.getBitmap(getContentResolver
                                    (), Uri.fromFile(mImageFile));
                            image.setImageBitmap(region);
                        } catch (IOException e) {
                            Log.w("MainActivity", "Error while generating BitMap", e);
                        }*/
                    }
                    mImageFile = null;
                    break;
                case 1:
                    try {
                        loadImage(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            uploadImage(imageBitmap);
        }
    }

    @SuppressLint("WrongViewCast")
    public void onImageDownloadCompleted(@NonNull Bitmap imageBitmap) {
        ImageViewZoom imageZoom;
        imageZoom=findViewById(R.id.image);
        imageZoom.getBase64();
        ImageViewZoomConfig imageViewZoomConfig=new ImageViewZoomConfig();
        imageViewZoomConfig.saveProperty(true);
        ImageViewZoomConfig.ImageViewZoomConfigSaveMethod imageViewZoomConfigSaveMethod= ImageViewZoomConfig.ImageViewZoomConfigSaveMethod.onlyOnDialog;
        imageViewZoomConfig.setImageViewZoomConfigSaveMethod(imageViewZoomConfigSaveMethod);
        imageZoom.setConfig(imageViewZoomConfig);
        imageZoom.setImageBitmap(imageBitmap);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
//            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                Log.d("MainActivity", String.valueOf(isStoragePermissionGranted()));
                if (!isStoragePermissionGranted()) {
//                SaveImage(bitmap);
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                SaveImage(bitmap);
                }
                uploadImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            image.setImageBitmap(bitmap);
        }else if(tipo==100){
            Log.d("MainActivity", String.valueOf(isStoragePermissionGranted()));
            if(isStoragePermissionGranted()) {
                try {
                    loadImage(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},1);
                try {
                    loadImage(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        /*try {
            textReply.setText(Conexion.reply(this));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

//    }

    private void uploadImage(Bitmap bitmap) {
        Connection connection = new Connection(this);
        connection.obtainToken("J9889");
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    //    Intent intentLoadNewActivity = new Intent(MainActivity.this, Result.class);
      //  startActivity(intentLoadNewActivity);

        connection.uploadPicture(bitmap, image);
    }

    private void uploadImage(File imageFile) {
     //   Intent intentLoadNewActivity = new Intent(MainActivity.this, Result.class);
      //  startActivity(intentLoadNewActivity);
        Connection connection = new Connection(this);
        connection.obtainToken("J9889");
        connection.uploadPicture(imageFile, this);
    }

    private void loadImage(Intent data) throws IOException {
        imageUri= data.getData();
        File file = new File(getPathFromUri(this,imageUri));

        uploadImage(file);





       // File file =new File(imageUri.getPath());
//        System.out.println(imageUri);
//        image.setImageURI(imageUri);
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        this.SaveImage(bitmap);

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //    Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //  Log.v(TAG,"Permission is revoked");
                //     ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //    Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    private void SaveImage(Bitmap finalBitmap) {

        //   String root = Environment.getExternalStorageDirectory().toString();
//        this.getExternalFilesDir()
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-"+ n +".jpg";
//        File file = new File (myDir, fname);
        File file = new File(getExternalFilesDir(null), "ImageDemo.png");
//        System.out.print(file.getAbsolutePath());
//        if (file.exists ()) file.delete ();
        try (FileOutputStream out = new FileOutputStream(file)) {
//            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            out.flush();
       //     Conexion.takeImage(out,this);
//            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onProgress(int progress) {
        Log.d("MainActivity", String.format("Upload progress... %d", progress));
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        Log.d("MainActivity", String.format("Upload progress: %d / %d (%d)", uploadedBytes,
                totalBytes, (int) ((uploadedBytes / totalBytes) * 100)));
    }

    @Override
    public void onError(Exception exception) {
        Log.w("MainActivity", "Upload/download error occurred!", exception);
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        Log.d("MainActivity", "Received response from server - response code: " + serverResponseCode);
        if (serverResponseCode == 200) {
            Log.i("MainActivity", "Decoding byte array...");
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(serverResponseBody, 0,
                    serverResponseBody.length);
            final float densityMultiplier = getResources().getDisplayMetrics().density;
            int h = (int) (100 * densityMultiplier);
            int w = (int) (h * decodedBitmap.getWidth() / ((double) decodedBitmap.getHeight()));

            Log.i("MainActivity", "Generating new resized BitMap...");
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(decodedBitmap, w, h, true);
            Log.i("MainActivity", "Showing image");
            image.setImageBitmap(resizedBitmap);
        } else {
            Log.w("MainActivity", "Error while obtaining image - status code: " + serverResponseCode);
        }
    }
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    @Override
    public void onCancelled() {
        Log.w("MainActivity", "Upload job cancelled");
    }
}