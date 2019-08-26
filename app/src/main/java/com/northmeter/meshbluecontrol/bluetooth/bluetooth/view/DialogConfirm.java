package com.northmeter.meshbluecontrol.bluetooth.bluetooth.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.northmeter.meshbluecontrol.R;


/**
 * Created by benjamin on 16/5/12.
 */
public class DialogConfirm extends DialogFragment {
    private DialogInterface.OnClickListener mOnClickListener;
    private int title;
    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_confrim_dialog, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_title);
        if(title!=0){
            tv.setText(title);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout  setView(view)
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok,mOnClickListener
                ).setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
