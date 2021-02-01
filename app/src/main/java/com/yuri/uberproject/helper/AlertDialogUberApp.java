package com.yuri.uberproject.helper;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.yuri.uberproject.activity.LoginActivity;

public class AlertDialogUberApp {

    public static void createAlertDiaglog(Context context, String title, String erro){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(erro)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}
