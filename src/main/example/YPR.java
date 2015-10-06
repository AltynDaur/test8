package main.example;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sergey_Stefoglo1 on 9/25/2015.
 */
public class YPR extends RequestProcessor {
    protected ActionMapping processMapping(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        ActionMapping mapping = super.processMapping(request, response, path);

            if (!request.getMethod().equals("POST")) {
                mapping = null;

        }
        return mapping;
    }
}