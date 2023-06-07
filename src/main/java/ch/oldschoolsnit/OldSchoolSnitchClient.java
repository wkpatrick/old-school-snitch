package ch.oldschoolsnit;

import ch.oldschoolsnit.records.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.api.Client;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
public class OldSchoolSnitchClient {
    @Inject
    private OldSchoolSnitchConfig config;
    @Inject
    private Client gameClient;
    private final OkHttpClient client;
    private final Gson gson;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String baseUrl = "https://oldschoolsnit.ch";

    @Inject
    private OldSchoolSnitchClient(OkHttpClient client) {
        this.client = client;
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public void SignIn(NameSignIn name) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(name));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/name")
                .post(body)
                .build();

        makeRequest(request);
    }

    public void sendXP(XpDrop xpDrop) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(xpDrop));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/xp")
                .post(body)
                .build();

        makeRequest(request);
    }

    public void sendKill(NpcKill kill) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(kill));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/kill")
                .post(body)
                .build();

        makeRequest(request);
    }

    public void sendItem(ItemDrop itemDrop) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(itemDrop));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/item")
                .post(body)
                .build();
        makeRequest(request);
    }

    public void sendLocation(UserLocation location) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(location));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/location")
                .post(body)
                .build();
        makeRequest(request);
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            log.error("Error in OldSchoolSnitchClient", e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            response.close();
        }
    };

    public void makeRequest(Request request) {
        client.newCall(request).enqueue(callback);
    }

}
