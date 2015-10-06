package main.example;

/**
 * Created by Sergey_Stefoglo1 on 9/23/2015.
 */
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class LoginForm extends ActionForm {

    private String userName = null;
    private String password = null;
    private String result;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.password = null;
    }

}
