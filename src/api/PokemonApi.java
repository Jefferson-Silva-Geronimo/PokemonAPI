package api;

import exception.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class PokemonApi {

    private static final String BASE_URL = "https://pokeapi.co/api/v2";
    private final HttpClient client;
    private final Map<String, String> cache;

    public PokemonApi() {
        this.client = HttpClient.newHttpClient();
        this.cache = new HashMap<>();
    }

    public String getPokemon(int id) {
        return get(BASE_URL + "/pokemon/" + id);
    }

    public String getPokemon(String name) {
        return get(BASE_URL + "/pokemon/" + name.toLowerCase());
    }

    public List<String> getPokemonTypes(int id) {
        String json = getPokemon(id);

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray types = obj.getAsJsonArray("types");

        List<String> result = new ArrayList<>();

        if (types != null) {
            for (int i = 0; i < types.size(); i++) {
                String tipo = types.get(i)
                        .getAsJsonObject()
                        .getAsJsonObject("type")
                        .get("name")
                        .getAsString();

                result.add(tipo);
            }
        }

        return result;
    }

    public String get(String url) {
        try {
            if (cache.containsKey(url)) {
                return cache.get(url);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException("Erro ao consultar API. Status: " + response.statusCode());
            }

            cache.put(url, response.body());
            return response.body();

        } catch (Exception e) {
            throw new ApiException("Erro ao consultar API.", e);
        }
    }
}