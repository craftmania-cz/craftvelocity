package cz.craftmania.craftvelocity.api.proxycheck.gson;

import com.google.gson.*;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.IPAddressInfo;
import cz.craftmania.craftvelocity.api.proxycheck.Objects.ProxyCheckResult;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class ProxyCheckResultTypeAdapter implements JsonDeserializer<ProxyCheckResult> {

    public static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");

    @Override
    public ProxyCheckResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String status = jsonObject.get("status").getAsString();
        String message = null;

        if (jsonObject.has("message")) {
            message = jsonObject.get("message").getAsString();
        }

        String queryTime = null;

        if (jsonObject.has("query time")) {
            queryTime = jsonObject.get("query time").getAsString();
        }

        String ipKey = null;
        JsonObject ipAddressInfoJson = null;
        IPAddressInfo ipAddressInfo = null;

        for (String key : jsonObject.keySet()) {
            if (IPV4_PATTERN.matcher(key).matches()) {
                ipKey = key;
                ipAddressInfoJson = jsonObject.getAsJsonObject(key);
                break;
            }
        }

        if (ipAddressInfoJson != null) {
            ipAddressInfo = new Gson().fromJson(ipAddressInfoJson, IPAddressInfo.class);
            ipAddressInfo.setIp(ipKey);
        }

        ProxyCheckResult proxyCheckResult = new ProxyCheckResult(ipAddressInfo, queryTime);
        proxyCheckResult.setStatus(status);
        proxyCheckResult.setStatus(message);
        return proxyCheckResult;
    }
}
