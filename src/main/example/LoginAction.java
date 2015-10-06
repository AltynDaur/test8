package main.example;

/**
 * Created by Sergey_Stefoglo1 on 9/23/2015.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.commons.codec.digest.DigestUtils;
import main.example.LoginForm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginAction extends Action {
    String selectSQL = "select USER_ID,LOGIN_NAME from m_user where lower(login_name)=lower(?)";
    String selectSQL1 = "select USER_ID,LOGIN_NAME from m_user where lower(login_name)=lower(?) and password=?";

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)

            throws Exception

    {
        LoginForm loginForm = (LoginForm) form;
        System.out.println(DigestUtils.md5Hex(((LoginForm) form).getPassword()));

        ConnectionPool pool = new ConnectionPool();
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        try {

            conn = pool.getConnection();
            stmt = conn.prepareStatement(selectSQL);
            stmt.setString(1, ((LoginForm) form).getUserName());
            rs = stmt.executeQuery();

    try{
            if (rs.next()) {

                String userid = rs.getString("USER_ID");
                String username = rs.getString("LOGIN_NAME");


                System.out.println("userid : " + userid);
                System.out.println("username : " + username);
                stmt=conn.prepareStatement(selectSQL1);
                stmt.setString(1, ((LoginForm) form).getUserName());
                stmt.setString(2, DigestUtils.md5Hex(((LoginForm) form).getPassword()));
                rs = stmt.executeQuery();
                if(rs.next()){
                   userid = rs.getString("USER_ID");
                     username = rs.getString("LOGIN_NAME");

                    System.out.println("userid : " + userid);
                    System.out.println("username : " + username);
                    request.setAttribute("message", "authorization is success");
                } else{
                    request.setAttribute("message", "authorization is failed , password is incorrect ");
                }
            }else{
                request.setAttribute("message", "authorization is failed , user doesn't found ");
            }



        }  finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(request.getAttribute("message"));
        return mapping.findForward("success");
    }
}