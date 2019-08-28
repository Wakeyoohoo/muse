package com.vargen.muse.musicplayer.net;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by weimengyuan on 2019-08-28.
 *
 * @author weimengyuan
 * @version v11.13
 * @since 2019-08-28
 */
public class HttpRequest {
    private HttpManager httpManager;
    private Request.Builder builder;
    private HttpUrl httpUrl;
    private LinkedHashMap<String, String> params;

    public HttpRequest(HttpManager httpManager) {
        this.httpManager = httpManager;
        initRequestHandler();
    }

    private HttpRequest initRequestHandler() {
        this.builder = new Request.Builder();
        return this;
    }

    public HttpRequest url(String url) {
        this.httpUrl = HttpUrl.parse(url);
        return this;
    }

    public HttpRequest params(Map params) {
        this.params = new LinkedHashMap<>(params);
        return this;
    }

    protected RequestBody buildRequestBody() {
        RequestBody requestBody = null;
        if (this.params != null && !this.params.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            Set<Entry<String, String>> entrySet = this.params.entrySet();
            Iterator iterator = entrySet.iterator();

            while(iterator.hasNext()) {
                Entry<String, String> entry = (Map.Entry)iterator.next();
                builder.add((String)entry.getKey(), (String)entry.getValue());
            }

            requestBody = builder.build();
        } else {
            requestBody = RequestBody.create((MediaType)null, new byte[0]);
        }
        return (RequestBody)requestBody;
    }

    public Response executeSync() {
        Request request = this.builder
                .url(this.httpUrl)
                .build();
        Call call = this.httpManager.mOkHttpClient.newCall(request);
        Response response = null;
        String reponseBody = null;
        try {
            response = call.execute();
            reponseBody = response.body().string();
        } catch (IOException e) {
            Logger.d(reponseBody);
        } finally {
            return response;
        }
    }

    public Response executePostSync() {
        Request request = this.builder
                .url(this.httpUrl)
                .post(buildRequestBody())
                .build();
        Call call = this.httpManager.mOkHttpClient.newCall(request);
        Response response = null;
        String reponseBody = null;
        try {
            response = call.execute();
            reponseBody = response.body().string();
        } catch (IOException e) {
            Logger.d(reponseBody);
        } finally {
            return response;
        }
    }
}
