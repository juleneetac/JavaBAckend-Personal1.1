package org.utils;

public class QueryHelper {
    public static String createQueryINSERT(Object entity) {

        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(entity.getClass().getSimpleName()).append(" ");
        sb.append("(");

        String[] fields = ObjectHelper.getFields(entity);

        sb.append("ID");   //el Id en primera posicion en teoria se pone solo
        for (String field : fields) {
            sb.append(", ").append(field);
        }

        sb.append(") VALUES (?");

        for (String field : fields) {
            sb.append(", ?");
        }

        sb.append(")");

        return sb.toString();
    }

    public static String createQuerySELECT(Class theClass) {    //obtiene datos de un objeto el cual le pasamos su ID
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE ID = ?");
        return sb.toString();
    }

    public static String createQueryDELETE(Object entity) {
        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM ").append(entity.getClass().getSimpleName());
        sb.append(" WHERE ID = ?");

        return sb.toString();
    }

    public static String createQuerySELECTall(Class theClass) {   //obtiene todos los datos de una tabla
        StringBuffer sb = new StringBuffer();                     //sirve para public List<Object> findAll(Class theClass)
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        return sb.toString();
    }

    public static String createQuerySELECTIDverifyusername(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE username = ?").append(" ").append("AND hash = ?");
        return sb.toString();
    }

    public static String createQuerySELECTIDverifyemail(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE email = ?").append(" ").append("AND hash = ?");
        return sb.toString();
    }

    public static String createQuerySELECTIDusername(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE username = ?");
        return sb.toString();
    }

    public static String createQuerySELECTIDemail(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE email = ?");
        return sb.toString();
    }

    public static String createQuerySELECTIDverifyall(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE username = ?").append(" ").append("OR email = ?").append(" ").append("AND hash = ?");
        return sb.toString();
    }
    public static String createQuerySELECTIDall(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE username = ?").append(" ").append("OR email = ?");
        return sb.toString();
    }

    public static String createQuerySELECThash(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT hash FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE ID = ?");
        return sb.toString();
    }

    public static String createQuerySELECTIDfromVerCode(Class theClass) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ").append(theClass.getSimpleName());
        sb.append(" WHERE verification_code = ?");
        return sb.toString();
    }

    public static String createQueryUPDATE(Object entity) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE ").append(entity.getClass().getSimpleName()).append(" ").append("SET");

        String [] fields = ObjectHelper.getFields(entity);

        for(String field: fields){
            sb.append(" ").append(field);
            sb.append(" = ?,");      // recorre todos los parametros de en este caso clase employee
        }
        sb.delete(sb.length() -1, sb.length());   // borra la coma final de sb.append(" = ?,")

        sb.append(" WHERE ID = ?");

        return sb.toString();
    }
    



}
