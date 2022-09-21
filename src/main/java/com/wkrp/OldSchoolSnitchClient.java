package com.wkrp;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wkrp.records.NpcKill;
import com.wkrp.records.XpDrop;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
public class OldSchoolSnitchClient {
    private final OkHttpClient client;
    private final Gson gson;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Inject
    private OldSchoolSnitchClient(OkHttpClient client)
    {
        this.client = client;
        this.gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public void sendXP(XpDrop xpDrop){
        RequestBody body = RequestBody.create(JSON, gson.toJson(xpDrop));
        String baseURL = "http://localhost:4000";
        Request request = new Request.Builder()
                .url(baseURL + "/api/xp")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            log.info("Response: " + response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendKill(NpcKill kill) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(kill));
        String baseURL = "http://localhost:4000";
        Request request = new Request.Builder()
                .url(baseURL + "/api/kill")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            log.info("Response: " + response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
