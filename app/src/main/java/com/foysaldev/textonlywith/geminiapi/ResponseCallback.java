package com.foysaldev.textonlywith.geminiapi;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
