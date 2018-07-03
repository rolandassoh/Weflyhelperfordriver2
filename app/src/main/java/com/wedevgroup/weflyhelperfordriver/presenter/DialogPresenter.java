package com.wedevgroup.weflyhelperfordriver.presenter;

import android.support.annotation.NonNull;

/**
 * Created by admin on 16/06/2018.
 */

public class DialogPresenter {
    private OnDialogListener mListener;
    public DialogPresenter(){

    }


    public static interface OnDialogListener {
        void onSaveRequest();
        void onDeleteRequest();
    }

    public void setOnDialogListener(@NonNull OnDialogListener listener) {
        this.mListener = listener;
    }

    public void shouldNotify(boolean canSave){
        notifyOnDialogListener(canSave);
    }

    private void notifyOnDialogListener(boolean isOK) {
        if (mListener != null){
            try {
                if (isOK){
                    mListener.onSaveRequest();
                }else{
                    mListener.onDeleteRequest();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
