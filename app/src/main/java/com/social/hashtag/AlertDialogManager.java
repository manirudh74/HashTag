package com.social.hashtag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by payojb on 3/8/2015.
 */
public class AlertDialogManager {

    public void showAlertDialog(Context context, String title, String message, Boolean status){

        AlertDialog.Builder builder = new AlertDialog
                .Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //if(status != null)
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
