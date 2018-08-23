package injoy.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DAO {
	
	//private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy_novo?user=injoy&password=injoybd98yu";
	private final String CONEXAO = "jdbc:mariadb://localhost:3306/injoy?user=root";
	private Connection connection;
	
	public DAO() {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			setConnection(DriverManager.getConnection(CONEXAO));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	private void setConnection(Connection connection) {
		this.connection = connection;
	}

}
