package com.wandm.dialogs;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.wandm.AppConfig;
import com.wandm.SmartAsyncPolicyHolder;
import com.wandm.utils.PreferencesUtils;


public abstract class BaseDialog extends BlurDialogFragment {

    @NonNull
    protected BlurConfig blurConfig() {
        return AppConfig.INSTANCE.getBlurViewConfig();
    }
}
