import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalComBusca {

    public static class Conversao {
        String fromCurrency;
        String toCurrency;
        double amount;
        double convertedAmount;

        public Conversao(String fromCurrency, String toCurrency, double amount, double convertedAmount) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.convertedAmount = convertedAmount;
        }

        @Override
        public String toString() {
            return "Conversao{" +
                    "De moeda='" + fromCurrency + '\'' +
                    ", Para moeda='" + toCurrency + '\'' +
                    ", Valor=" + amount +
                    ", Valor convertido=" + convertedAmount +
                    '}';
        }

        public static void main(String[] args) throws IOException, InterruptedException {
            Scanner leitura = new Scanner(System.in);
            String busca = "";
            List<Conversao> conversoes = new ArrayList<>();

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .create();

            String API_KEY = "https://economia.awesomeapi.com.br/json/last/:moedas";

            while (!busca.equalsIgnoreCase("Sair")) {

                System.out.println("Digite a moeda de origem (exemplo: USD) ou 'sair' para finalizar busca: ");
                busca = leitura.nextLine();

                if (busca.equalsIgnoreCase("Sair")) {
                    break;
                }

                System.out.println("Digite para qual moeda deseja converter (Exemplo BRL): ");
                String toCurrency = leitura.nextLine();

                System.out.println("Digite o valor a ser convertido: ");
                double amount = leitura.nextDouble();
                leitura.nextLine();

                String endereco = "https://economia.awesomeapi.com.br/json/last/:moedas" +  busca;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endereco))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();
                System.out.println("Resposta da API: " + json);

                LinkedTreeMap<String, Object> responseMap = gson.fromJson(json, LinkedTreeMap.class);
                LinkedTreeMap<String, Object> rates = (LinkedTreeMap<String, Object>) responseMap.get("rates");

                if (rates.containsKey(toCurrency)) {
                    double conversionRate = (double) rates.get((toCurrency));
                    double convertedAmount = amount * conversionRate;

                    System.out.println(amount + busca + "É equivalente a " + convertedAmount + " " + toCurrency);

                    Conversao conversao = new Conversao(busca, toCurrency, amount, convertedAmount);
                    conversoes.add(conversao);
                }
            }
        }
    }
}
