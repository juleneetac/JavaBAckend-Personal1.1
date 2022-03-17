package org.UserDAO;

import org.SessionDAO.SessionDAO;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.models.EmailCfg;
import org.models.Users;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class UserImpl implements UserDAO {
    final static Logger log = Logger.getLogger(UserImpl.class.getName());

//    @Autowired
//    private static JavaMailSender mailSender;//no usamos ahora


    public UserImpl() {  //dependency ingection yto populate this field
        users = new HashMap<>();
    }

    private static UserDAO instance;

    private final HashMap<String, Users> users;




    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserImpl();
        }
        return instance;
    }



    public int register(String username, String email, String password, String siteURL) {

        SessionDAO session = null;
        //Boolean bool = false;
        int userIDn;
        int userIDe;
        String hashedPass;
        try {
            session = FactorySession.openSession();
            hashedPass = hashPassword(password);
            users.put(username, new Users(username, email, hashedPass));
            Users usernew = new Users(username, email, hashedPass);  //registras al user y le pones todos los stats
            userIDn = session.getIDusername(Users.class, usernew.getUsername()); // aqui comprueba si el id ya esta o no con el username
            userIDe = session.getIDemail(Users.class, usernew.getEmail());

            if (userIDn == 0 && userIDe == 0) {    //como hemos visto en el getidverify si no encuentra ninguno el
                usernew.setEnabled(0);  //poner el enabled en 0 para saber que no se ha verificado todavia
                String randomCode = RandomString.make(64);//para crear un verification_code random
                usernew.setVerification_code(randomCode);
                //poner session.save(usernew);
                sendVerificationEmail(usernew, siteURL);
                session.save(usernew);
                return 201;
            } else if (userIDn != 0) {
                //bool = false;
                log.error("Conflict (User already exists)");
                return 409;
            } else {                          //HACER QUE DETECTE SI ES CONTRASEÑA O EMAIL LO QUE YA EXISTE
                //bool = false;
                log.error("Conflict (Email already registered)");
                return 401;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("Bad Request: Error in input parameters' format");
            return 400;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Internal Server Error");
            return 500;
        } finally {
            //session.close();
        }
    }


    public Users login(Users users) {  //se hace el login
        Users u = null;
        SessionDAO session = null;
        int userID;
        Users userfromid;
        int userIDh;
        String hashPass;
        try {
            session = FactorySession.openSession();
//          if (users.getEmail() == null) {  //miro si se loggea con el email o el username
//                userID = session.getIDverifyusername(Users.class, users.getUsername(), users.getPassword());
//            }
//          else {
//                userID = session.getIDverifyemail(Users.class, users.getEmail(), users.getPassword());
//            }
            userID = session.getIDall(Users.class, users.getUsername(), users.getEmail());
            if (userID != 0) {
                hashPass = session.gethash(Users.class, userID);
                if (validatePassword(users.getPassword(), hashPass)) {
                    userID = session.getIDverifyall(Users.class, users.getUsername(), users.getEmail(), hashPass);
                    if (userID == 0) {   // aqui pasa al reves que en addUser y es que si no encuentra un id con el nickname  y el password
                        log.error("Incorrect credentials"); // del usuario, el id será 0 y nos saltará una excepcion
                        u = new Users("404", "null", "null"); //404 User Not found
                    } else {
                        userfromid = (Users) session.get(Users.class, userID); //cojo el user de la db entero a partir del id
                        if (userfromid.getEnabled() == 0){
                            log.error("User not verified after registry");
                            u = new Users("409", "null", "null");
                        }
                        else {
                            u = (Users) session.login(Users.class, userID);
                        }
                    }
                } else {
                    log.error("Incorrect password de: " + users.getUsername());
                    u = new Users("402", "null", "null"); //402 Mal password
                }
            } else {
                log.error("Incorrect credentials"); // del usuario, el id será 0 y nos saltará una excepcion
                u = new Users("404", "null", "null"); //404 User Not found
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("Internal Server Error");
            u = new Users("500", "null", "null");
        } finally {
            //session.close();
            return u;
        }
    }

    public int deleteUser(String username, String email, String pwd, String param) {
        //Podría hacer tambien que el usuario solo fuera el que se pasa en el param y no haria falta pasarselo en el json
        // y en el frontend solo escribiria la contraseña
        SessionDAO session = null;
        String hashPass;
        Users users = new Users(username, email, "");
        Users userfromid;
        String usernamelocal;
        int userID;
        try {

            session = FactorySession.openSession();
            userID = session.getIDall(Users.class, username, email);


            if (userID != 0) {
                userfromid = (Users) session.get(Users.class, userID);  //(Users) es para convertir del typo Object a la clase Users
                usernamelocal = userfromid.getUsername();  //coge el username por si acaso le hemos pasado el email
                if (usernamelocal.equals(username)) {  //si no coincide el username con el que se le pasa en la URL no valdra
                    hashPass = session.gethash(Users.class, userID);
                    if (validatePassword(pwd, hashPass)) {
                        userID = session.getIDverifyall(Users.class, username, email, hashPass);
                        if (userID == 0) {   // aqui pasa al reves que en addUser y es que si no encuentra un id con el nickname  y el password
                            log.error("Incorrect credentials, user not deleted"); // del usuario, el id será 0 y nos saltará una excepcion
                            return 404;
                        } else {
                            session.delete(users, userID);
                            return 201;
                        }
                    } else {
                        log.error("Incorrect password de: " + users.getUsername() + ", user not deleted");
                        return 402;
                    }
                } else {
                    log.error("Incorrect URL, user not deleted");
                    return 405;
                }
            } else {
                log.error("Incorrect credentials, user not deleted"); //
                return 404;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Couldn't delete the User");
            return 500;
        } finally {
            //session.close();
        }
    }

    public int verify(String verificationCode){
        SessionDAO session = null;
        Users users = null;
        Users userfromid;
        int userID;
        try {
            session = FactorySession.openSession();
            userID = session.getIDALLfromVerCode(Users.class, verificationCode);
            if (userID == 0) {   // aqui pasa al reves que en addUser y es que si no encuentra un id con el nickname  y el password
                log.error("User not found o ya verificado 404"); // del usuario, el id será 0 y nos saltará una excepcion
                return 404;
            }
            else {
                users = (Users) session.get(Users.class, userID);
                if (users.getEnabled() == 0) {
                    users.setVerification_code(null);
                    users.setEnabled(1);
                    session.update(users, userID);
                    log.error("User verified 202");
                    return 200;
                }
                else{
                    log.error("User ya esta verificado 409");
                    return 409;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Internal error");
            return 500;
            //users = new Users("500", "null", "null");
        } finally {
            //session.close();

        }
    }


    public Users getUser(String username) {
        SessionDAO session = null;
        Users users = null;
        int userID;
        try {
            session = FactorySession.openSession();
            userID = session.getIDusername(Users.class, username);
            if (userID == 0) {   // aqui pasa al reves que en addUser y es que si no encuentra un id con el nickname  y el password
                log.error("User not found"); // del usuario, el id será 0 y nos saltará una excepcion
                users = new Users("404", "null", "null"); //404 User Not found
            }
            else {
                users = (Users) session.get(Users.class, userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Internal error");
            users = new Users("500", "null", "null");
        } finally {
            //session.close();
            return users;
        }
    }


    public List<Users> getUsers() {   // me da una lista de usuarios
        SessionDAO session = null;
        List<Users> usersList = null;
        try {
            session = FactorySession.openSession();
            usersList = session.findAll(Users.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error getting the list of users");
            usersList.add(new Users("500", "null", "null"));
        } finally {
            //session.close();
            return usersList;
        }
    }



    //cosas utiles//
    public String hashPassword(String textPassword) {
        return BCrypt.hashpw(textPassword, BCrypt.gensalt());
    }

    public boolean validatePassword(String passedPassword, String hashedPass) {
        if (BCrypt.checkpw(passedPassword, hashedPass))
            return true;
        else
            return false;
    }

    public void sendVerificationEmail(Users user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String verifyURL = "http://localhost:8080/project/usermanager/verify/" + user.getVerification_code();
        //tiene que llevar la url del servidor y no la del cliente
        //por tanto al final no uso la clase Utility ni nada para pedir la URL
        String toAddress = user.getEmail();
        String fromAddress = "noreply@baeldung.com";
        String senderName = "myproject";
        String subject = "Please verify your registration";

        String content = "Dear " +  user.getUsername() +",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\" "+ verifyURL + "\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "My proyect team";


        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // otro video
        //log.info(emailCfg);
//        mailSender.setHost("smtp.gmail.com");          //nullpointer exception
//        mailSender.setPort(587);
//        mailSender.setUsername("mimail@gmail.com");
//        mailSender.setPassword("password");

        Properties mailtrap = new Properties();            //store credentials and config in a separate txt file
        try (FileReader in = new FileReader("login.properties.txt")) {
            mailtrap.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String usercode = mailtrap.getProperty("usercode");
        String passcode = mailtrap.getProperty("passcode");

        mailSender.setHost("smtp.mailtrap.io");
        mailSender.setPort(2525);
        mailSender.setUsername(usercode);
        mailSender.setPassword(passcode);



        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
//
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(fromAddress);
//        mailMessage.setTo(toAddress);
//        mailMessage.setSubject(subject);
//        mailMessage.setContent(content, "text/html; charset=utf-8");
//        mailMessage.setText(content);


//        MimeMessage message = mailSender.createMimeMessage(); //no usamos ahora
//        MimeMessageHelper helper = new MimeMessageHelper(message);  //no usamos ahora
//
//        helper.setFrom(fromAddress, senderName);
//        helper.setTo(toAddress);
//        helper.setSubject(subject);
//
//        helper.setText(content, true);

        mailSender.send(message);
    }






}
