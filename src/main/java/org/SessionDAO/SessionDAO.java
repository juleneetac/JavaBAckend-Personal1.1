package org.SessionDAO;


import java.util.List;

public interface SessionDAO <E>{
    void save(Object entity);  // esto es registrarse en verdad
    Object login(Class theClass, int ID);  // esto es el login
    void delete(Object object, int ID);     //esto es el borrar
    List<Object> findAll(Class theClass);    //obtiene todos los datos de una tabla
    Object get(Class theClass, int ID);
    int getIDusername(Class theClass, String username);//, String password); //Obtiene id de la base de datos a partir de username
    int getIDemail(Class theClass, String email); //, String password); //Obtiene id de la base de datos a partir de email
    int getIDverifyall(Class theClass, String username, String email, String password);
    int getIDall(Class theClass, String username, String email);
    String gethash(Class theClass, int ID);
    int getIDALLfromVerCode(Class theClass, String verfCode); //me da el ID a partir del verification_code
    void update(Object object, int ID);  // hace update de ciertos campos
}
