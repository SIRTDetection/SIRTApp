/*
 * Copyright © 2019 - present | SIRTApp by Javinator9889

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

 * Created by Javinator9889 on 17/01/19 - SIRTApp.
 */
package sirtapp.sirtdetection.com.sirtapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import sirtapp.sirtdetection.com.sirtapp.R;
/**
 * Esta clase es la que ejecuta la introduccion que se ve la primera vez que se ejecuta la aplicación
 */
public class IntroActivity extends AppIntro {
    /**
     * Este metodo crea la actividad y basicamente lo que vemos en ella
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        if (preferences.getBoolean("mustStartMainActivity", false))
            startMainActivity();

        SliderPage firstSlide = new SliderPage();
        firstSlide.setTitle(getString(R.string.first_slide_title));
        firstSlide.setDescription(getString(R.string.first_slide_content));
        firstSlide.setImageDrawable(R.mipmap.ic_launcher_foreground);
        firstSlide.setBgColor(Color.BLUE);

        SliderPage secondSlide = new SliderPage();
        secondSlide.setTitle(getString(R.string.second_slide_title));
        secondSlide.setDescription(getString(R.string.second_slide_content));
        secondSlide.setImageDrawable(R.drawable.github);
        secondSlide.setBgColor(Color.BLUE);

        SliderPage thirdSlide = new SliderPage();
        thirdSlide.setTitle(getString(R.string.permissions_slide));
        thirdSlide.setDescription(getString(R.string.storage_permissions));
        thirdSlide.setImageDrawable(R.drawable.folder);
        thirdSlide.setBgColor(Color.BLUE);

        SliderPage fourthSlide = new SliderPage();
        fourthSlide.setTitle(getString(R.string.permissions_slide));
        fourthSlide.setDescription(getString(R.string.extra_permissions));
        fourthSlide.setImageDrawable(android.R.drawable.ic_dialog_info);
        fourthSlide.setBgColor(Color.BLUE);

        addSlide(AppIntroFragment.newInstance(firstSlide));
        addSlide(AppIntroFragment.newInstance(secondSlide));
        addSlide(AppIntroFragment.newInstance(thirdSlide));
        addSlide(AppIntroFragment.newInstance(fourthSlide));

        askForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);

        showSkipButton(false);
    }
    /**
     * Estos metodos son los que nos permiten interactuar con dicha actividad, asi como preguntarnos si concedemos los permisos e
     * iniciar la actividad principal
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            getSupportActionBar().hide();
            getActionBar().hide();
        } catch (NullPointerException ignored) {}
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences.Editor editor =
                getSharedPreferences("userPrefs", Context.MODE_PRIVATE).edit();
        editor.putString("UUID", UUID.randomUUID().toString());
        editor.putBoolean("mustStartMainActivity", true);
        editor.apply();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mainActivity);
        finish();
    }
}
