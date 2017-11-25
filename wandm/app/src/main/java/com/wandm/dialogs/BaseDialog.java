package com.wandm.dialogs;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.wandm.SmartAsyncPolicyHolder;


public abstract class BaseDialog extends BlurDialogFragment {

    @NonNull
    protected BlurConfig blurConfig() {
        return new BlurConfig.Builder()
                .overlayColor(Color.argb(90, 0, 0, 0))
                .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                .debug(true)
                .build();
    }
}
