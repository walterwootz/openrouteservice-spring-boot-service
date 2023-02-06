package com.walterwootz.openrouteserviceexample.services;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    @Value("${openroute-service.api.key}")
    private String apiKey;

    @Value("${openroute-service.api.endpoint}")
    private String apiURL;

    @Value("${openroute-service.api.profile}")
    private String profile;

    /**
     * Calculate route duration between two points using matrix API
     * @param source latitude,longitude format e.g. -71.602844,-34.517101
     * @param destination latitude,longitude format e.g. -71.602844,-34.517101
     * @return duration in seconds
     */
    public Double calculateDurationMatrix(String source, String destination) {
        try {
            URL url = new URL(apiURL + "matrix/" + profile);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");
            http.setRequestProperty("Authorization", apiKey);

            String data = "{\"locations\":[[" + source + "],[" + destination + "]],\"sources\":[0],\"destinations\":[1],\"metrics\":[\"duration\"],\"resolve_locations\":\"false\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            InputStream in = http.getInputStream();
            String responseBody = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JSONObject geoResponse = new JSONObject(responseBody);
            JSONArray durations = geoResponse.getJSONArray("durations");
            Double duration = durations.getJSONArray(0).getDouble(0);
            http.disconnect();
            return duration;

        } catch (IOException e) {
            log.error("Error occured on Openroute Service");
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Calculate route duration between two points using directions API
     * @param source latitude,longitude format e.g. -71.602844,-34.517101
     * @param destination latitude,longitude format e.g. -71.602844,-34.517101
     * @return duration in seconds
     */
    public Double calculateDurationDirections(String source, String destination) {
        try {
            String strUrl = apiURL + "directions/" + profile + "?api_key=" + apiKey
                    + "&start=" + source.replaceAll(" ", "")
                    + "&end=" + destination.replaceAll(" ", "");
            URL url = new URL(strUrl);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");

            InputStream in = http.getInputStream();
            String responseBody = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JSONObject geoResponse = new JSONObject(responseBody);
            JSONArray features = geoResponse.getJSONArray("features");
            JSONObject feature = features.getJSONObject(0);
            JSONObject properties = feature.getJSONObject("properties");
            JSONObject summary = properties.getJSONObject("summary");
            Double duration = summary.getDouble("duration");
            http.disconnect();
            return duration;

        } catch (IOException e) {
            log.error("Error occured on Openroute Service");
            log.error(e.getMessage());
        }
        return null;
    }
}
