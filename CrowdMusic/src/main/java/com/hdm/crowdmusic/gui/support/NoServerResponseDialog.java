package com.hdm.crowdmusic.gui.support;

import android.app.AlertDialog;
import android.content.Context;

public class NoServerResponseDialog extends StringOKDialog {
    AlertDialog dialog;

    public NoServerResponseDialog(Context context) {
        super(context, "Connection lost! \nTry to refresh the server view and reconnect.");
    }
}
