package main.example;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by thief on 8/31/2014.
 * Utility class for working with SSL, mostly in dev/debug purposes.
 */
public class SslUtil {
    private String idpMetadata;
    private static String  spIdentifier="https://localhost:8443/testapp";
    public static final String CALLBACK_URL = "/sso/callback";
    private static boolean usePostBinding=true;
    /**
     * Overrides SSL TrustManager to custom one which accepts all certificates
     * returning by the calling server.
     * @return true if operation was succeeded
     */
    public static Boolean setTrustAllSslCertificates() {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
            }

            public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };

        try {
            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getDefault();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);


            return true;
        }

        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Collects all the data, that may be interesting in case of SSL errors analysis
     * @return collected information
     */
    public static String collectHttpsSslContextDetails() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try{
            pw.append("SSL Context parameters");
            pw.append("\nhttps.protocols: " + System.getProperty("https.protocols"));
            pw.append("\njsse.enableSNIExtension: " + System.getProperty("jsse.enableSNIExtension"));

            SSLContext ctx = SSLContext.getInstance("SSL");
            pw.append("\nDefault SSL Context Data");
            pw.append("\nContext class: " + ctx.getClass());
            pw.append("\nProtocol: " + ctx.getProtocol());
     //       pw.append("\nSupported protocols: " + Arrays.toString(ctx.getSupportedSSLParameters().getProtocols()));
      //     pw.append("\nDefault protocols: " + Arrays.toString(ctx.getDefaultSSLParameters().getProtocols()));

            pw.append("\nDefault SSLSocket Data");
            SSLSocketFactory scdf = ctx.getSocketFactory();
            SSLSocket scd = (SSLSocket) scdf.createSocket();
            pw.append("\nFactory class: " + scdf.getClass());
            pw.append("\nSocket class: " + scd.getClass());
            pw.append("\nEnabled protocols: " + Arrays.toString(scd.getEnabledProtocols()));

            pw.append("\nHttpsURLConnection default SSLSocket Data");
            SSLSocketFactory sccf = HttpsURLConnection.getDefaultSSLSocketFactory();
            SSLSocket scc = (SSLSocket)sccf.createSocket();
            pw.append("\nFactory class: " + sccf.getClass());
            pw.append("\nSocket class: " + scc.getClass());
            pw.append("\nEnabled protocols: " + Arrays.toString(scc.getEnabledProtocols()));

        } catch (Exception e){

            pw.append("\nError collecting SSL context: ");
            e.printStackTrace(pw);
        }
        return sw.toString();
    }
    public static   SamlAuthenticator createAuthenticator(HttpServletRequest req, String idpMetadata) {
        // build callback url from current request
        StringBuilder sb = new StringBuilder();
        sb.append(req.getScheme());
        sb.append("://");
        sb.append(req.getServerName());
        int port = req.getServerPort();
        if (port != 80 && port != 443){
            sb.append(":");
            sb.append(port);
        }
      //  sb.append(CALLBACK_URL).append(".do");
        sb.append(CALLBACK_URL);

        return new SamlAuthenticator(idpMetadata, spIdentifier, sb.toString(), usePostBinding);
    }
    public static String readMetadata(String metadataUrl){
        URL mdUrl;
//8443
        try {
            mdUrl = new URL(metadataUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Wrong URL specified: " + metadataUrl, e);
        }
        URLConnection cnn;
        try {
            cnn = mdUrl.openConnection();

        } catch (IOException e) {
            throw new RuntimeException("Unable to open URL: " + metadataUrl, e);
        }
        try {

            InputStream stream = cnn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }

            return sb.toString();

        } catch (IOException e) {
            throw new RuntimeException("Unable to read URL: " + metadataUrl, e);
        }
    }
}
