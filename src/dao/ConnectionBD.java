package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBD {
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Connection conectar() throws SQLException{
		String servidor = "localhost";
		String porta = "3306";
		String database = "web_server_pi7";
		String usuario = "Alunos";
		String senha = "alunos";
		String host = "jdbc:mysql://";
		
		return DriverManager.getConnection(host+servidor+":"+porta+"/"+database+"?useTimezone=true&serverTimezone=UTC",usuario, senha);
	}
	
	public static void desconectar(Connection conn) throws SQLException {
		conn.close();
	}
}