package com.manuelmaly.hn.server;

import java.util.HashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

// A Command class for json  # Calvin Chang
public class JsonStringDownloadCommand extends BaseHTTPCommand<String> {

    public JsonStringDownloadCommand(String url, HashMap<String, String> queryParams, RequestType type, boolean notifyFinishedBroadcast,
            String notificationBroadcastIntentID, Context applicationContext, CookieStore cookieStore) {
            super(url, queryParams, type, notifyFinishedBroadcast, notificationBroadcastIntentID, applicationContext, 60000, 60000);
            setCookieStore(cookieStore);
        }

        @Override
        protected HttpUriRequest setRequestData(HttpUriRequest request) {
            request.setHeader(ACCEPT_HEADER, JSON_MIME);
            return request;
        }

        @Override
        protected ResponseHandler<String> getResponseHandler(HttpClient client) {
            return new HTMLResponseHandler(this, client);
        }
	
}
