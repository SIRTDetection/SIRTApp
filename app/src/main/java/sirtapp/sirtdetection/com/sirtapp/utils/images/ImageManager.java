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

 * Created by Javinator9889 on 3/01/19 - SecurePass.
 */
package sirtapp.sirtdetection.com.sirtapp.utils.images;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageManager {
    private Context mContext;

    public ImageManager(Context context) {
        mContext = context;
    }

    public File createNewFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFilename = "JPEG_" + timeStamp + "-org";
        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            File storageDir = getExternalStorageDir();
            if (!storageDir.exists())
                if (!storageDir.mkdirs())
                    throw new IOException("Unable to create the public pictures directory");
            return File.createTempFile(imageFilename, ".jpg", storageDir);
        } else
            throw new IOException("Insufficient permissions");
    }

    public static File getExternalStorageDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SIRTApp");
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
