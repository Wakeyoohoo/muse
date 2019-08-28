package com.vargen.muse.musicplayer.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by weimengyuan on 2019-08-28.
 *
 * @author weimengyuan
 * @version v11.13
 * @since 2019-08-28
 */
public class HttpManager {
    public OkHttpClient mOkHttpClient;
    public HttpRequest mRequest;

    private HttpManager() { }

    public HttpManager getHttpManager() {
        mOkHttpClient = new OkHttpClient.Builder().readTimeout(10000, TimeUnit.MILLISECONDS).build();
        return this;
    }

    public HttpRequest getRequest() {
        mRequest = new HttpRequest(this);
        return mRequest;
    }

    public HttpRequest postRequest() {
        mRequest = new HttpRequest(this);
        return mRequest;
    }
}
