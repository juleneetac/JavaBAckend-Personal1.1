package org.SessionDAO;


import org.utils.ObjectHelper;
import org.utils.QueryHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionImpl implements SessionDAO {

    private final Connection conn;

    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public void save(Object entity) {  //seria como registrarse porque usa el INSERT en el comando sql es generico porque puedes guardar cualquier cosa
        String insertQuery = QueryHelper.createQueryINSERT(entity); // tambien sirve para añadir un objeto a la tabla de objetos
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(insertQuery);
            pstm.setObject(1, 0);   //la i es la posición de los interrogantes y la o el valor que le pone
            int i = 2;

            for (String field: ObjectHelper.getFields(entity)) {
                pstm.setObject(i++, ObjectHelper.getter(entity, field));
            }

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void update(Object object, int ID) {
        String updateQuery = QueryHelper.createQueryUPDATE(object);
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(updateQuery);
            int i = 1;
            for (String field : ObjectHelper.getFields(object)) {
                pstm.setObject(i++, ObjectHelper.getter(object, field));
            }
            pstm.setObject(i, ID);   // probablemente redunante  // la "i" es 1 porque no le afecta lo del for
            pstm.executeUpdate(); //executeUpdate method execute sql statements that insert/update/delete data at the database.
            //This method return int value representing number of records affected; Returns 0 if the query returns nothing
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



    public Object login(Class theClass, int ID) {
        String selectQuery = QueryHelper.createQuerySELECT(theClass);

        Object entity = null;
        try {
            entity = theClass.getDeclaredConstructor().newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {   //para el getDeclaredConstructor()
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        ResultSet rs = null;
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1,ID);
            rs = pstm.executeQuery();


            while(rs.next()){
                Field[] fields = theClass.getDeclaredFields();
                rs.getString(1);
                for (int i = 0; i<fields.length; i++){
                    String fieldName = this.getNameCampo(i+2, rs);
                    ObjectHelper.setter(entity, fieldName, rs.getObject(i + 2));
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }


    public void delete(Object object, int ID) {    //borra un usuario o objeto el cual le pasamos id como parametro
        String deleteQuery = QueryHelper.createQueryDELETE(object);

        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(deleteQuery);
            pstm.setObject(1, ID);     //Las columnas empiezan a contar en 1 y no en 0

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object> findAll(Class theClass) {      //obtiene todos los datos de una tabla
        String findAllQuery = QueryHelper.createQuerySELECTall(theClass);

        Object entity = null;
        List<Object> listOfObjects = new ArrayList<Object>();

        try {
            entity = theClass.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {  //excepcion para newInstance
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {   //excepción para el getDeclaredConstructor
            e.printStackTrace();
        }
        ResultSet rs = null;
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(findAllQuery);
            rs = pstm.executeQuery();

            while(rs.next()){   //recorre cada fila
                Field[] fields = theClass.getDeclaredFields();
                rs.getString(1);     //te da el id, que esta en la columna 1
                for (int i = 0; i<fields.length; i++){
                    ObjectHelper.setter(entity, fields[i].getName(), rs.getObject(i + 2));   // "i" lo hemos inicializado a 0
                }                                                                             // por tanto ponemos i +2 que sera la siquiente columna 2, 3, ...
                listOfObjects.add(entity);
                entity = theClass.getDeclaredConstructor().newInstance();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return listOfObjects;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public Object get(Class theClass, int ID) {   //lo daba el profe pero no lo utilizamos alfinal
        String selectQuery = QueryHelper.createQuerySELECT(theClass);
        Object entity = null;
        try {
            entity = theClass.getDeclaredConstructor().newInstance();   //esto es nuevo antes era newInstance solo
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {   //excepción para el getDeclaredConstructor
            e.printStackTrace();
        }
        ResultSet rs = null;
        PreparedStatement pstm = null;

        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1,ID);
            rs = pstm.executeQuery();  //execute statements that returns a result set by fetching some data from the database.


            while(rs.next()){
                Field[] fields = theClass.getDeclaredFields();
                rs.getString(1);
                for (int i = 0; i<fields.length; i++){
                    String campoName = this.getNameCampo(i+2, rs);  //quiere decir que coge la columna i y le suma 2
                    ObjectHelper.setter(entity, campoName, rs.getObject(i + 2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }


    public int getIDusername(Class theClass, String username)//, String hash) //Obtiene id de la base de datos de un usuario pero verificado con su password
    {                                   //principalmente para login y para borrar un user se tendrá que verificar
        String selectQuery = QueryHelper.createQuerySELECTIDusername(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        int id = 0; // lo pongo a 0
        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, username);   //en el primer interrogante va el nickname
            //pstm.setObject(2, hash);   // en el segundo va el hash
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                id = rs.getInt(1);  //se obtiene el ID encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }

        return id;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }

    public int getIDemail(Class theClass, String email)//, String hash) //Obtiene id de la base de datos de un usuario pero verificado con su password
    {                                   //principalmente para login y para borrar un user se tendrá que verificar
        String selectQuery = QueryHelper.createQuerySELECTIDemail(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        int id = 0; // lo pongo a 0
        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, email);   //en el primer interrogante va el nickname
            //pstm.setObject(2, hash);   // en el segundo va el password
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                id = rs.getInt(1);  //se obtiene el ID encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }

        return id;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }

    public int getIDverifyall(Class theClass, String username, String email, String password) //Obtiene id de la db a partir del username o email pero verificado con su password
    {                                   //principalmente para login y para borrar un user se tendrá que verificar
        String selectQuery = QueryHelper.createQuerySELECTIDverifyall(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        int id = 0; // lo pongo a 0
        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, username);   //en el primer interrogante va el nickname
            pstm.setObject(2, email);   // en el segundo va el password
            pstm.setObject(3, password);   // en el segundo va el password
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                id = rs.getInt(1);  //se obtiene el ID encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }

        return id;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }

    public int getIDall(Class theClass, String username, String email) //Obtiene id de la base de datos de un usuario a partir del username o email sin verificar con password
    {                                   //principalmente para login y para borrar un user se tendrá que verificar
        String selectQuery = QueryHelper.createQuerySELECTIDall(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        int id = 0; // lo pongo a 0
        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, username);   //en el primer interrogante va el nickname
            pstm.setObject(2, email);   // en el segundo va el password
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                id = rs.getInt(1);  //se obtiene el ID encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }

        return id;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }



    public String gethash(Class theClass, int ID)  //hace un get de un parametro a partir del ID
    {                                   //principalmente para login y para borrar un user se tendrá que verificar
        String selectQuery = QueryHelper.createQuerySELECThash(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        //String hash = "hash"; //
        String hashe = "No user with this hash";
        try {
            pstm = conn.prepareStatement(selectQuery);
            //pstm.setObject(1, hash);   //en el primer interrogante va el nickname
            pstm.setObject(1, ID);   // en el segundo va el hash
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                hashe = rs.getString(1);  //se obtiene el hash encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }

        return hashe;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }

    public int getIDALLfromVerCode(Class theClass, String verfCode) //Obtiene id de la base de datos de un usuario a partir del verification_code
    {                                   //principalmente para verificar user
        String selectQuery = QueryHelper.createQuerySELECTIDfromVerCode(theClass);
        ResultSet rs;
        PreparedStatement pstm;
        int id = 0; // lo pongo a 0
        try {
            pstm = conn.prepareStatement(selectQuery);
            pstm.setObject(1, verfCode);   //en el primer interrogante va el nickname
            rs = pstm.executeQuery(); //cambiar para que haga algo si no lo encuentra

            if (rs.next())
            {
                id = rs.getInt(1);  //se obtiene el ID encontrado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new UserNotFoundException();

        }
        return id;   // si no lo encuentra  el id es 0 y si lo encuentra pues devuelve el id que tiene
    }


    private String getNameCampo(int i, ResultSet rs) throws SQLException {   //metodo privado para darme el nombre de un campo
        ResultSetMetaData rsmd = rs.getMetaData();    // viene de "public interface ResultSetMetaData extends Wrapper" que es de java.sql
        String name = rsmd.getColumnName(i);
        return name;
    }



    public void close() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
