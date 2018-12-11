package de.objectiveit.kempdnsscaler.util;

import de.objectiveit.kempdnsscaler.ApplicationException;
import de.objectiveit.kempdnsscaler.model.Credentials;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

public class HttpUtil {

    public static boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String doGet(String requestURL, Map<String, Object> queryParams) throws IOException {
        return doGet(requestURL, queryParams, null);
    }

    /**
     * Calls specified URL and returns response as a string.
     * <p>
     * Host name and certificate verifications are switched off for SSL connections.
     *
     * @param requestURL  URL to call
     * @param queryParams Query parameters
     * @param basicAuth   Basic authentication info
     * @return response
     * @throws IOException if an I/O exception occurs
     */
    public static String doGet(String requestURL, Map<String, Object> queryParams, Credentials basicAuth) throws IOException {
        // Disabling host name verification
        HttpsURLConnection.setDefaultHostnameVerifier(
                new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession sslSession) {
                        return true;
                    }
                });

        URL url = new URL(requestURL + formatQueryParams(queryParams));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (basicAuth != null) {
            String toEncode = basicAuth.getLogin() + ":" + basicAuth.getPassword();
            String encoded = Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoded);
        }

        if (connection instanceof HttpsURLConnection) {
            // Trust all certificates
            SSLContext context = null;
            try {
                context = SSLContext.getInstance("SSL");
                context.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new ApplicationException("Shouldn't happen!", e);
            }
            ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
        }

        try (InputStream is = new BufferedInputStream(connection.getInputStream())) {
            return toString(is);
        }
    }

    protected static String toString(InputStream input) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Prepares URL query parameters part based on the map of parameters.
     *
     * @param params map of parameters
     * @return query parameters part of URL
     */
    protected static String formatQueryParams(Map<String, Object> params) {
        if (params == null) {
            return "";
        }
        return params.entrySet().stream()
                .map(p -> p.getKey() + "=" + p.getValue())
                .reduce((p1, p2) -> p1 + "&" + p2)
                .map(s -> "?" + s)
                .orElse("");
    }

    /**
     * {@link TrustManager} implementation that trusts all certificates.
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

}
