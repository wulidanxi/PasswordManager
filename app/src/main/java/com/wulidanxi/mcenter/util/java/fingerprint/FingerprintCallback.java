package com.wulidanxi.mcenter.util.java.fingerprint;

import android.content.Intent;

/**
 * 验证结果回调，供使用者调用
 * Created by ZuoHailong on 2019/7/9.
 * modify by ding on 2020/10/20
 */
public interface FingerprintCallback {

    /**
     * 无指纹硬件或者指纹硬件不可用
     */
    void onHwUnavailable();

    /**
     * 未添加指纹
     */
    void onNoneEnrolled();

    /**
     * 验证成功
     */
    void onSucceeded(int state , Intent intent);

    /**
     * 验证失败
     */
    void onFailed();

    /**
     * 密码登录
     */
    void onUsepwd();

    /**
     * 取消验证
     */
    void onCancel();

}
