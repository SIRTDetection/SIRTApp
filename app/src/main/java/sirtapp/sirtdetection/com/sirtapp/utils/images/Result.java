package sirtapp.sirtdetection.com.sirtapp.utils.images;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import sirtapp.sirtdetection.com.sirtapp.R;

public class Result extends AppCompatActivity {
    static ImageView result, image;

    public Result(ImageView image) {
        this.image = image;
    }

    public Result() {

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
}
