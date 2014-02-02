package com.hdm.crowdmusic.gui.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class StringOKDialog{
    AlertDialog dialog;

    public StringOKDialog(Context context, String dialogText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(dialogText);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new OkOnClickListener());
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    private final class OkOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }
}
