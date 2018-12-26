package sirtapp.sirtdetection.com.sirtapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {
    int tipo;
    Uri imageUri;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    ImageView image;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    tipo=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    Button button1=(Button)findViewById(R.id.camera);
    image =(ImageView)findViewById(R.id.image);
    Button button2=(Button)findViewById(R.id.gallery);


    button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            tipo=1;
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,100);

            }
        });



    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tipo=0;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,0);

        }
    });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(tipo==0){
            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
            Log.d("MainActivity", String.valueOf(isStoragePermissionGranted()));
            if(isStoragePermissionGranted()) {
                SaveImage(bitmap);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
                SaveImage(bitmap);
            }

             image.setImageBitmap(bitmap);
        }else if(tipo==1){
            Log.d("MainActivity", String.valueOf(isStoragePermissionGranted()));
            if(isStoragePermissionGranted()) {
                loadImage(data);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},1);
                loadImage(data);
            }
        }

    }

    private void loadImage(Intent data) {
        imageUri= data.getData();
        image.setImageURI(imageUri);
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
            Conexion.takeImage(out,this);
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
}
