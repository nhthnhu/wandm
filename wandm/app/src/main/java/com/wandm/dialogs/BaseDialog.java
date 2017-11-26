package com.wandm.dialogs;

import android.support.annotation.NonNull;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.wandm.utils.Utils;


public abstract class BaseDialog extends BlurDialogFragment {

    @NonNull
    protected BlurConfig blurConfig() {
        return Utils.INSTANCE.getBlurViewConfig();
    }
}
