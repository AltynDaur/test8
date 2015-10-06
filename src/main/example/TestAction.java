package main.example;

/**
 * Created by Sergey_Stefoglo1 on 9/22/2015.
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestAction extends Action {
    private String idpMetadata;

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {



        String metadataUrl = this.servlet.getInitParameter("metadataUrl");
        if (metadataUrl == null || metadataUrl.length()==0)
            throw new RuntimeException("Required configuration parameter is not specified, metadataUrl");
        SslUtil.setTrustAllSslCertificates();
        idpMetadata = SslUtil.readMetadata(metadataUrl);
       SamlAuthenticator authenticator = SslUtil.createAuthenticator(request, idpMetadata);
        authenticator.initiateLoginFlow(request, response);
       // return mapping.findForward("success");
        return null;


//
    }

}