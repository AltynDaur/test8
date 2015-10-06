package main.example;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Sergey_Stefoglo1 on 8/21/2015.
 */
public class SamlAuthenticationServlet extends HttpServlet {

    private String idpMetadata;
    private String spIdentifier;
    private static final String CALLBACK_URL = "/sso/callback";
    private boolean usePostBinding;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // load SP identifier
        spIdentifier = config.getInitParameter("spIdentifier");
        if (spIdentifier == null || spIdentifier.length()==0)
            throw new RuntimeException("SP Identifier parameter was not specified");

        // load SAML binding type
        String usePostBinding = config.getInitParameter("usePostBinding");
        if (usePostBinding != null)
            this.usePostBinding = Boolean.valueOf(usePostBinding.toLowerCase());

        // init SSL certs trust rules
        String trustAllCerts = config.getInitParameter("trustAllCerts");
        if (trustAllCerts != null && Boolean.valueOf(trustAllCerts.toLowerCase()) == true)
            SslUtil.setTrustAllSslCertificates();

        // init idp metadata
        String metadataUrl = config.getInitParameter("metadataUrl");
        if (metadataUrl == null || metadataUrl.length()==0)
            throw new RuntimeException("Required configuration parameter is not specified, metadataUrl");
        idpMetadata = readMetadata(metadataUrl);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
         if (req.getRequestURI().equals(CALLBACK_URL)){
            SamlAuthenticator authenticator = createAuthenticator(req);
            String userName = authenticator.completeLoginFlow(req, resp);
            System.out.print(userName);
            try {
                resp.getWriter().write("You've logged in as " + userName);
            } catch (IOException e) {
                throw new RuntimeException("Something went wrong", e);
            }
        }

    }

    @Override
    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
        throws ServletException, IOException {
        if (req.getRequestURI().equals("/sso/init")){
            SamlAuthenticator authenticator = createAuthenticator(req);
            authenticator.initiateLoginFlow(req, resp);
                }
    }

    private SamlAuthenticator createAuthenticator(HttpServletRequest req) {
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
        sb.append(CALLBACK_URL);

        return new SamlAuthenticator(idpMetadata, spIdentifier, sb.toString(), usePostBinding);
    }

    private String readMetadata(String metadataUrl){
        URL mdUrl;
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

    public void destroy() {
        // do nothing.
    }

}