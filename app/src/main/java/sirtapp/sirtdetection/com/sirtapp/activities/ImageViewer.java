/*
 * Copyright © 2019 - present | sirtapp.sirtdetection.com.sirtapp.activities by Javinator9889
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
 * Created by Javinator9889 on 4/01/19 - SecurePass.
 */
package sirtapp.sirtdetection.com.sirtapp.activities;

import android.net.Uri;
import android.os.Bundle;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.FrescoImageViewFactory;
import com.github.piasy.biv.view.ImageSaveCallback;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sirtapp.sirtdetection.com.sirtapp.R;
import sirtapp.sirtdetection.com.sirtapp.utils.progress.ProgressPieIndicator;

public class ImageViewer extends AppCompatActivity implements ImageSaveCallback {
    private BigImageView mImageView;
    private File mImageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(FrescoImageLoader.with(this));

        setContentView(R.layout.image_displayer);
        mImageView = findViewById(R.id.mBigImage);
        mImageView.setImageViewFactory(new FrescoImageViewFactory());
        mImageView.setImageSaveCallback(this);
        mImageView.setOptimizeDisplay(true);
        mImageView.setProgressIndicator(new ProgressPieIndicator());
        mImageFile = (File) getIntent().getSerializableExtra("picture");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        displayImage();
    }

    public void displayImage() {
        mImageView.showImage(Uri.fromFile(mImageFile), Uri.fromFile(mImageFile));
    }

    @Override
    public void onSuccess(String uri) {

    }

    @Override
    public void onFail(Throwable t) {

    }
}