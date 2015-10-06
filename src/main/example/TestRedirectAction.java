package main.example;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sergey_Stefoglo1 on 9/22/2015.
 */
public class TestRedirectAction extends Action {
    private String idpMetadata;

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        if (request.getRequestURI().equals(SslUtil.CALLBACK_URL)){
            String metadataUrl = this.servlet.getInitParameter("metadataUrl");
            if (metadataUrl == null || metadataUrl.length()==0)
                throw new RuntimeException("Required configuration parameter is not specified, metadataUrl");
            SslUtil.setTrustAllSslCertificates();
            idpMetadata = SslUtil.readMetadata(metadataUrl);


            SamlAuthenticator authenticator = SslUtil.createAuthenticator(request,idpMetadata);
            String userName = authenticator.completeLoginFlow(request, response);
            System.out.print(userName);
            try {
                request.setAttribute("message", userName);
                response.getWriter().write("You've logged in as " + userName);
            } catch (IOException e) {
                throw new RuntimeException("Something went wrong", e);
            }
        }
        return mapping.findForward("success");
    }

}
