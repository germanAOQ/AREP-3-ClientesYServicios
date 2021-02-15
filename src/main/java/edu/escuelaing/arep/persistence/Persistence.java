package edu.escuelaing.arep.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Persistence {
	private static final String url = "jdbc:postgresql://ec2-100-24-139-146.compute-1.amazonaws.com:5432/ddvgkl5cq2e61c";
	private static final String user = "cxixtuwojbnwmn";
	private static final String password = "dbf55124f4de8f399bf67f892832573717d130a18e8893269ab4f21c12776850";
	
	
	/**
	 * @return retorna la conexión a la base de datos.
	 */
	public Connection connection() {
		Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
       
	}
	
	/**
	 * @param conexion parametro asociado a la conexión con la base de datos.
	 * @param name cadena que será concatenada junto con la respuesta del servidor de la base datos.
	 * @return retorna cadena que concatena la respuesta de la base de datos con name. 
	 */
	public String getStatement(Connection conexion, String name) {
		Statement statement;
		ResultSet rs;
		String res = null;
		try {
			statement = conexion.createStatement();
			rs = statement.executeQuery("SELECT frase FROM func");
			while(rs.next()) {
				res = rs.getString("frase");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res+" "+name;
		
	}
	
	public static void main(String[] args) {
		Persistence per = new Persistence();
		System.out.println(per.getStatement(per.connection(),"Germán"));
	}

}
