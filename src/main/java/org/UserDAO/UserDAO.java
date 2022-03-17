package org.UserDAO;

import org.models.EmailCfg;
import org.models.Users;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

//@Component
public interface UserDAO {
    public int register(String nick, String email, String password, String siteURL);  //registrarse
    public Users login(Users users);                       //login
    public Users getUser(String username);               //me da un usuario a partir de su nombre
    public List<Users> getUsers();                       //me da la lista de usuarios
    public int deleteUser(String nick, String email, String password, String param); //borra un user
    //public void sendVerificationEmail(Users user, String siteURL, EmailCfg emailCfg) throws UnsupportedEncodingException, MessagingException;
    public int verify(String code);  //me da el código de verificación
}
