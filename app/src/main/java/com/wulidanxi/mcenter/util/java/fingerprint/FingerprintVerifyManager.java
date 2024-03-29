package com.wulidanxi.mcenter.util.java.fingerprint;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wulidanxi.mcenter.util.java.fingerprint.bean.VerificationDialogStyleBean;
import com.wulidanxi.mcenter.util.java.uitls.AndrVersionUtil;


/**
 * Created by ZuoHailong on 2019/7/9.
 * modify by ding on 2020/10/20
 */
public class FingerprintVerifyManager {

    public FingerprintVerifyManager(Builder builder) {
        IFingerprint fingerprint;
        if (AndrVersionUtil.isAboveAndrP()) {
            if (builder.enableAndroidP)
                fingerprint = FingerprintAndrP.newInstance();
            else
                fingerprint = FingerprintAndrM.newInstance();
        } else if (AndrVersionUtil.isAboveAndrM()) {
            fingerprint = FingerprintAndrM.newInstance();
        } else {//Android 6.0 以下官方未开放指纹识别，某些机型自行支持的情况暂不做处理
            builder.callback.onHwUnavailable();
            return;
        }
        //检测指纹硬件是否存在或者是否可用，若false，不再弹出指纹验证框
        if (!fingerprint.canAuthenticate(builder.context, builder.callback)) {
            return;
        }
        /**
         * 设定指纹验证框的样式
         */
        // >= Android 6.0
        VerificationDialogStyleBean bean = new VerificationDialogStyleBean();
        bean.setCancelTextColor(builder.cancelTextColor);
        bean.setUsepwdTextColor(builder.usepwdTextColor);
        bean.setFingerprintColor(builder.fingerprintColor);
        bean.setUsepwdVisible(builder.usepwdVisible);

        // >= Android 9.0
        bean.setTitle(builder.title);
        bean.setSubTitle(builder.subTitle);
        bean.setDescription(builder.description);
        bean.setCancelBtnText(builder.cancelBtnText);
        bean.setState(builder.state);
        bean.setIntent(builder.intent);

        fingerprint.authenticate(builder.context, bean, builder.callback);
    }

    /**
     * UpdateAppManager的构建器
     */
    public static class Builder  {

        /*必选字段*/
        private Activity context;
        private FingerprintCallback callback;

        /*可选字段*/
        private int cancelTextColor;
        private int usepwdTextColor;
        private int fingerprintColor;
        private boolean usepwdVisible;

        //在Android 9.0系统上，是否使用系统验证框
        private boolean enableAndroidP;
        private String title;
        private String subTitle;
        private String description;
        private String cancelBtnText;
        private int state;
        private Intent intent;

        /**
         * 构建器
         *
         * @param activity
         */
        public Builder(@NonNull Activity activity) {
            this.context = activity;
        }

        /**
         * 指纹识别回调
         *
         * @param callback
         */
        public Builder callback(@NonNull FingerprintCallback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * 取消按钮文本色
         *
         * @param color
         */
        public Builder cancelTextColor(@ColorInt int color) {
            this.cancelTextColor = color;
            return this;
        }

        public Builder state(int state){
            this.state = state;
            return this;
        }

        public Builder intent(Intent intent){
            this.intent = intent;
            return this;
        }

        /**
         * 密码验证按钮文本色
         *
         * @param color
         */
        public Builder usepwdTextColor(@ColorInt int color) {
            this.usepwdTextColor = color;
            return this;
        }

        /**
         * 指纹图标颜色
         *
         * @param color
         */
        public Builder fingerprintColor(@ColorInt int color) {
            this.fingerprintColor = color;
            return this;
        }

        /**
         * 密码登录按钮是否显示
         *
         * @param isVisible
         */
        public Builder usepwdVisible(boolean isVisible) {
            this.usepwdVisible = isVisible;
            return this;
        }

        /**
         * 在 >= Android 9.0 系统上，是否开启google提供的验证方式及验证框
         *
         * @param enableAndroidP true/false
         */
        public Builder enableAndroidP(boolean enableAndroidP) {
            this.enableAndroidP = enableAndroidP;
            return this;
        }

        /**
         * >= Android 9.0 的验证框的主标题
         *
         * @param title
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * >= Android 9.0 的验证框的副标题
         *
         * @param subTitle
         */
        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        /**
         * >= Android 9.0 的验证框的描述内容
         *
         * @param description
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * >= Android 9.0 的验证框的取消按钮的文字
         *
         * @param cancelBtnText
         */
        public Builder cancelBtnText(String cancelBtnText) {
            this.cancelBtnText = cancelBtnText;
            return this;
        }

        /**
         * 开始构建
         *
         * @return
         */
        public FingerprintVerifyManager build() {
            return new FingerprintVerifyManager(this);
        }
    }

}
