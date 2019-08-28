package com.vargen.muse.musicplayer.net;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by weimengyuan on 2019-08-28.
 *
 * @author weimengyuan
 * @version v11.13
 * @since 2019-08-28
 */
public class requestHandler {
    private Request.Builder Builder;
    private HttpUrl HttpUrl;
    private HttpManager HttpManager;

    public requestHandler(HttpManager httpManager) {
        this.HttpManager = httpManager;
    }

    private Request.Builder initRequestHandler() {
        this.Builder = new Request.Builder();
        return this.Builder;
    }
}
