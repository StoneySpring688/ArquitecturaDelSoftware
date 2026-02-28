package SegundUM.Productos;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Programa de pruebas que testea todos los endpoints REST del microservicio de Productos.
 *
 * Uso: Ejecutar con la aplicacion (App.java) ya arrancada en localhost:8080.
 */
public class PruebaEndpoints {

    private static final String BASE = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();

    // Colores ANSI para la consola
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private static int totalTests = 0;
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {

        System.out.println(CYAN + "=============================================" + RESET);
        System.out.println(CYAN + "  PRUEBAS DE ENDPOINTS - Microservicio Productos" + RESET);
        System.out.println(CYAN + "  Base URI: " + BASE + RESET);
        System.out.println(CYAN + "=============================================" + RESET);
        System.out.println();

        // ---------------------------------------------------------------
        // 1. CATEGORIAS
        // ---------------------------------------------------------------
        seccion("CATEGORIAS");

        // 1.1 GET /api/categorias - Listar todas
        HttpResponse<String> resp = get("/categorias");
        verificar("GET /categorias - Listar todas", resp, 200);

        // 1.2 GET /api/categorias/{id} - Obtener por ID (puede no existir)
        resp = get("/categorias/arte-y-ocio");
        verificar("GET /categorias/{id} - Obtener por ID", resp, 200, 404, 500);

        // ---------------------------------------------------------------
        // 2. PRODUCTOS - Alta
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - ALTA");

        // 2.1 POST /api/productos - Crear producto
        // Primero obtenemos un ID de categoria valido
        resp = get("/categorias");
        String categoriaId = extraerPrimerIdDeArray(resp.body());

        String params = "titulo=" + enc("Bicicleta de Montana")
                + "&descripcion=" + enc("Bicicleta MTB en buen estado")
                + "&precio=150.00"
                + "&estado=BUEN_ESTADO"
                + "&categoriaId=" + enc(categoriaId != null ? categoriaId : "cat-test")
                + "&envioDisponible=true"
                + "&vendedorId=" + enc("vendedor-test-123");

        resp = post("/productos", params);
        verificar("POST /productos - Alta de producto", resp, 201);

        // Extraer ID del producto creado desde el header Location
        String productoId = null;
        String location = resp.headers().firstValue("Location").orElse(null);
        if (location != null) {
            productoId = location.substring(location.lastIndexOf("/") + 1);
            System.out.println("     Producto creado con ID: " + productoId);
        }

        // ---------------------------------------------------------------
        // 3. PRODUCTOS - Consulta
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - CONSULTA");

        // 3.1 GET /api/productos/{id} - Obtener por ID
        if (productoId != null) {
            resp = get("/productos/" + productoId);
            verificar("GET /productos/{id} - Obtener producto", resp, 200);
        }

        // 3.2 GET /api/productos/buscar - Buscar con filtros
        resp = get("/productos/buscar?precioMaximo=200");
        verificar("GET /productos/buscar?precioMaximo=200", resp, 200);

        resp = get("/productos/buscar?texto=" + enc("Bicicleta"));
        verificar("GET /productos/buscar?texto=Bicicleta", resp, 200);

        resp = get("/productos/buscar");
        verificar("GET /productos/buscar (sin filtros)", resp, 200);

        // 3.3 GET /api/productos/vendedor/{vendedorId}
        resp = get("/productos/vendedor/vendedor-test-123");
        verificar("GET /productos/vendedor/{vendedorId}", resp, 200);

        // ---------------------------------------------------------------
        // 4. PRODUCTOS - Modificacion
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - MODIFICACION");

        if (productoId != null) {
            // 4.1 PUT - Modificacion NO autorizada (usuario incorrecto)
            resp = put("/productos/" + productoId,
                    "descripcion=" + enc("Hackeado") + "&precio=0&usuarioId=hacker-123");
            verificar("PUT /productos/{id} - Modificacion NO autorizada", resp, 403, 204, 500);

            // 4.2 PUT - Modificacion autorizada (dueno)
            resp = put("/productos/" + productoId,
                    "descripcion=" + enc("Bicicleta MTB rebajada") + "&precio=120.00&usuarioId=vendedor-test-123");
            verificar("PUT /productos/{id} - Modificacion autorizada", resp, 204);

            // 4.3 PUT - Asignar lugar de recogida
            resp = put("/productos/" + productoId + "/recogida",
                    "descripcion=" + enc("Plaza Mayor, Madrid") + "&longitud=-3.7038&latitud=40.4168");
            verificar("PUT /productos/{id}/recogida - Asignar lugar", resp, 204);

            // 4.4 PUT - Registrar visualizacion
            resp = put("/productos/" + productoId + "/visualizaciones", "");
            verificar("PUT /productos/{id}/visualizaciones - Vista 1", resp, 204);

            resp = put("/productos/" + productoId + "/visualizaciones", "");
            verificar("PUT /productos/{id}/visualizaciones - Vista 2", resp, 204);
        }

        // ---------------------------------------------------------------
        // 5. PRODUCTOS - Historial
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - HISTORIAL");

        int mes = LocalDate.now().getMonthValue();
        int anio = LocalDate.now().getYear();

        resp = get("/productos/historial?mes=" + mes + "&anio=" + anio);
        verificar("GET /productos/historial - Resumen mensual", resp, 200);

        resp = get("/productos/historial/test@email.com?mes=" + mes + "&anio=" + anio);
        verificar("GET /productos/historial/{email} - Por vendedor", resp, 200);

        // ---------------------------------------------------------------
        // 6. PRODUCTOS - Verificar producto modificado
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - VERIFICACION FINAL");

        if (productoId != null) {
            resp = get("/productos/" + productoId);
            verificar("GET /productos/{id} - Verificar cambios", resp, 200);
            System.out.println("     Producto actualizado:");
            imprimirJsonResumido(resp.body());
        }

        // ---------------------------------------------------------------
        // 7. PRODUCTOS - Eliminar
        // ---------------------------------------------------------------
        seccion("PRODUCTOS - ELIMINACION");

        if (productoId != null) {
            resp = delete("/productos/" + productoId);
            verificar("DELETE /productos/{id} - Eliminar producto", resp, 204);

            // Verificar que ya no existe
            resp = get("/productos/" + productoId);
            verificar("GET /productos/{id} - Verificar eliminado", resp, 404, 500);
        }

        // ---------------------------------------------------------------
        // RESUMEN
        // ---------------------------------------------------------------
        System.out.println();
        System.out.println(CYAN + "=============================================" + RESET);
        System.out.println(CYAN + "  RESUMEN DE PRUEBAS" + RESET);
        System.out.println(CYAN + "=============================================" + RESET);
        System.out.println("  Total:    " + totalTests);
        System.out.println(GREEN + "  Pasaron:  " + passed + RESET);
        if (failed > 0) {
            System.out.println(RED + "  Fallaron: " + failed + RESET);
        } else {
            System.out.println("  Fallaron: 0");
        }
        System.out.println(CYAN + "=============================================" + RESET);
    }

    // --- Metodos HTTP ---

    private static HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String formParams) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path + "?" + formParams))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> put(String path, String formParams) throws Exception {
        String uri = BASE + path;
        if (formParams != null && !formParams.isEmpty()) {
            uri += "?" + formParams;
        }
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .DELETE()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    // --- Utilidades ---

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void seccion(String nombre) {
        System.out.println();
        System.out.println(YELLOW + "--- " + nombre + " ---" + RESET);
    }

    private static void verificar(String testName, HttpResponse<String> resp, int... codigosEsperados) {
        totalTests++;
        int status = resp.statusCode();
        boolean ok = false;
        for (int esperado : codigosEsperados) {
            if (status == esperado) {
                ok = true;
                break;
            }
        }

        if (ok) {
            passed++;
            System.out.println(GREEN + " OK  " + RESET + testName + " [HTTP " + status + "]");
        } else {
            failed++;
            System.out.println(RED + " FAIL " + RESET + testName + " [HTTP " + status + "]");
            // Mostrar cuerpo del error si existe
            String body = resp.body();
            if (body != null && !body.isEmpty() && body.length() < 500) {
                System.out.println("      Respuesta: " + body);
            }
        }
    }

    private static String extraerPrimerIdDeArray(String json) {
        // Extraccion simple del primer "id" en un JSON array
        if (json == null || json.isEmpty() || json.equals("[]")) return null;
        int idx = json.indexOf("\"id\"");
        if (idx < 0) return null;
        int start = json.indexOf("\"", idx + 4) + 1;
        int end = json.indexOf("\"", start);
        if (start > 0 && end > start) {
            return json.substring(start, end);
        }
        return null;
    }

    private static void imprimirJsonResumido(String json) {
        if (json == null || json.isEmpty()) return;
        // Mostrar las primeras lineas del JSON formateado basico
        String formatted = json.replace(",", ",\n     ");
        if (formatted.length() > 500) {
            formatted = formatted.substring(0, 500) + "...";
        }
        System.out.println("     " + formatted);
    }
}
