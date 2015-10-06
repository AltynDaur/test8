package main.example;


import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.client.RedirectAction.RedirectType;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.saml.client.Saml2Client;
import org.pac4j.saml.credentials.Saml2Credentials;
import org.pac4j.saml.profile.Saml2Profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sergey_Stefoglo1 on 9/2/2015.
 */
public class SamlAuthenticator {
    private static final String SAML_BINDING_POST = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private static final String SAML_BINDING_REDIRECT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    private static final int DEFAULT_MAXIMUM_AUTHENTICATION_LIFETIME = 24 * 60 * 60; // 24h

    private String idpMetadata;
    private String spIdentifier;
    private String callbackUrl;
    private boolean usePostBinding;

    public SamlAuthenticator(String idpMetadata, String spIdentifier, String callbackUrl, boolean usePostBinding) {
        this.idpMetadata = idpMetadata;
        this.spIdentifier = spIdentifier;
        this.callbackUrl = callbackUrl;
        this.usePostBinding = usePostBinding;
    }

    public void initiateLoginFlow(HttpServletRequest request, HttpServletResponse response) {
        try {
            Saml2Client client = newClient();
            WebContext context = new J2EContext(request, response);

            RedirectAction action = client.getRedirectAction(context, true, false);

            if (action.getType() == RedirectType.REDIRECT) {
                response.sendRedirect(action.getLocation());

            } else if (action.getType() == RedirectType.SUCCESS) {
                response.getWriter().write(action.getContent());
            } else {
                throw new IllegalStateException("Received unexpected response type " + action.getType());
            }
        } catch (RequiresHttpAction e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String completeLoginFlow(HttpServletRequest req, HttpServletResponse resp) {

        Saml2Credentials credentials;
        Saml2Client client = newClient();
        WebContext context = new J2EContext(req, resp);

        try {
            credentials = client.getCredentials(context);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        Saml2Profile saml2Profile = client.getUserProfile(credentials, context);
        // TODO: Extract any user attributes here
        return saml2Profile.getId();
    }

    private Saml2Client newClient() {
        Saml2Client client = new Saml2Client();
        client.setIdpMetadataPath("https://login-prod.epm-sso.projects.epam.com/FederationMetadata/2007-06/FederationMetadata.xml");

        client.setIdpEntityId(spIdentifier);
        client.setCallbackUrl(callbackUrl);
//        client.setKeystorePath("resource:samlKeystore.jks");
        client.setKeystorePath("resource:d:/Java/jrockit-R26.3.0-jdk1.5.0_06/jre/lib/security/cacerts");
        client.setKeystorePassword("changeit");
        client.setPrivateKeyPassword("changeit");
      //  client.setKeystorePath("samlKeystore.jks");
//        client.setKeystorePath("d:/Java/jrockit-R26.3.0-jdk1.5.0_06/jre/lib/security/samlKeystore.jks");
//        client.setKeystorePassword("pac4j-demo-passwd");
//        client.setPrivateKeyPassword("pac4j-demo-passwd");
   //     client.setDestinationBindingType(usePostBinding ? SAML_BINDING_POST : SAML_BINDING_REDIRECT);
        client.setMaximumAuthenticationLifetime(DEFAULT_MAXIMUM_AUTHENTICATION_LIFETIME);
        return client;
    }
}
