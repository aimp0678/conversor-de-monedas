import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import org.json.JSONObject;

public class ConversorMonedas {
    private static final String API_KEY = "e9a5cfa0ef00c2bbff5662cd"; // Reemplaza con tu API key
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private static final String[] DIVISAS = {"USD", "EUR", "GBP", "JPY", "MXN", "CAD", "AUD", "CNY"};
    private static final String[] NOMBRES_DIVISAS = {
            "Dólar Estadounidense (USD)",
            "Euro (EUR)",
            "Libra Esterlina (GBP)",
            "Yen Japonés (JPY)",
            "Peso Mexicano (MXN)",
            "Dólar Canadiense (CAD)",
            "Dólar Australiano (AUD)",
            "Yuan Chino (CNY)"
    };
    private static final NumberFormat FORMATO_NUMEROS = NumberFormat.getInstance(Locale.US);

    static {
        FORMATO_NUMEROS.setMaximumFractionDigits(2);
        FORMATO_NUMEROS.setMinimumFractionDigits(2);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();
        boolean salir = false;

        while (!salir) {
            System.out.println("\n💱 CONVERSOR DE MONEDAS 💰");
            System.out.println("1. Realizar conversión");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");

            int opcionMenu = leerEntero(scanner, 1, 2);

            switch (opcionMenu) {
                case 1:
                    realizarConversion(scanner, client);
                    break;
                case 2:
                    salir = true;
                    System.out.println("¡Gracias por usar el conversor! 👋");
                    break;
            }
        }
        scanner.close();
    }

    private static void realizarConversion(Scanner scanner, HttpClient client) {
        System.out.println("\nMonedas disponibles:");
        for (int i = 0; i < DIVISAS.length; i++) {
            System.out.printf("%d. %s\n", i + 1, NOMBRES_DIVISAS[i]);
        }

        // Seleccionar moneda origen
        System.out.print("\nSeleccione moneda origen (número): ");
        int opcionOrigen = leerEntero(scanner, 1, DIVISAS.length) - 1;
        String from = DIVISAS[opcionOrigen];

        // Seleccionar moneda destino
        System.out.print("Seleccione moneda destino (número): ");
        int opcionDestino = leerEntero(scanner, 1, DIVISAS.length) - 1;
        String to = DIVISAS[opcionDestino];

        // Validar que no sea la misma moneda
        if (from.equals(to)) {
            System.out.println("❌ ¡No puedes convertir la misma moneda!");
            return;
        }

        // Ingresar cantidad
        System.out.print("Ingrese la cantidad a convertir: ");
        double amount = leerDouble(scanner);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + from))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            if (json.getString("result").equals("success")) {
                double rate = json.getJSONObject("conversion_rates").getDouble(to);
                double convertedAmount = amount * rate;

                // Formatear números con separadores de miles
                String cantidadFormateada = FORMATO_NUMEROS.format(amount);
                String resultadoFormateado = FORMATO_NUMEROS.format(convertedAmount);
                String tasaFormateada = FORMATO_NUMEROS.format(rate);

                System.out.printf("\n✅ %s %s = %s %s\n",
                        cantidadFormateada, from, resultadoFormateado, to);
                System.out.println("Tasa de cambio actual: 1 " + from + " = " + tasaFormateada + " " + to);
            } else {
                System.err.println("❌ Error en la API: " + json.getString("error-type"));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con la API: " + e.getMessage());
        }

        // Pausa antes de volver al menú
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private static int leerEntero(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.printf("⚠️ Ingrese un número entre %d y %d: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Ingrese un número válido: ");
            }
        }
    }

    private static double leerDouble(Scanner scanner) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Ingrese una cantidad válida: ");
            }
        }
    }
}