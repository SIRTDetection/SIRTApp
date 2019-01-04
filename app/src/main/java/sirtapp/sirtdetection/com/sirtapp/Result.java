package sirtapp.sirtdetection.com.sirtapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Result extends AppCompatActivity {
static ImageView result,image;

public Result(ImageView image){
    this.image=image;
}
    public static void mostar(Bitmap bitmap) {
        result.setImageBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result = findViewById(R.id.image2);
    }

    public void mostrar() {
        image.buildDrawingCache();
        Bitmap bmap = image.getDrawingCache();
        result.setImageBitmap(bmap);
    }
}
