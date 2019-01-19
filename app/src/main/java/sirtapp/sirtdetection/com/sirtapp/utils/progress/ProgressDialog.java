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
package sirtapp.sirtdetection.com.sirtapp.utils.progress;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
/**
 * Esta clase nos muestra los textos del progreso en la notificacion de que esta haciendo la app y el servidor
 * entre que se hace la foto hasta que nos muestra la imagen de respuesta y colabora con la clase ProgressPieIndicator
 */
public class ProgressDialog {
    private MaterialDialog mDialog;

    public ProgressDialog(@NonNull Context context,
                          @StringRes int title,
                          @StringRes int bodyContent) {
        mDialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(bodyContent)
                .cancelable(false)
                .progress(true, 100)
                .progressIndeterminateStyle(true)
                .build();
    }

    @UiThread
    public void updateTitle(@StringRes int title) {
        mDialog.setTitle(title);
    }

    @UiThread
    public void updateBody(@StringRes int body) {
        mDialog.setContent(body);
    }

    @UiThread
    public void show() {
        mDialog.show();
    }

    @UiThread
    public void dismiss() {
        mDialog.dismiss();
    }
}
