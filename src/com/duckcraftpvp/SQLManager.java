package com.duckcraftpvp;

import java.util.logging.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import java.sql.*;

public class SQLManager {
	private LicenseRedeem pl;
	private String host;
	private String database;
	private String username;
	private String password;
	private int port;
	private Connection conn;

	public SQLManager(final LicenseRedeem pl, final ConfigManager configMan) {
		this.pl = pl;
		this.host = configMan.config.getString("sql.host");
		this.database = configMan.config.getString("sql.database");
		this.username = configMan.config.getString("sql.username");
		this.password = configMan.config.getString("sql.password");
		this.port = configMan.config.getInt("sql.port");
		try {
			this.connect();
		} catch (ClassNotFoundException e2) {
			pl.getLogger().log(Level.SEVERE, "No MySQL driver found. Aborting!");
			Bukkit.getServer().getPluginManager().disablePlugin((Plugin) pl);
		} catch (SQLException e) {
			pl.getLogger().log(Level.SEVERE, "Put in the correct MySQL auth info.");
			e.printStackTrace();
		}
	}

	public ResultSet runSQL(final String sql, final boolean data) {
		try {
			this.connect();
			final Statement statement = this.conn.createStatement();
			if (data) {
				statement.executeUpdate(sql);
				return null;
			}
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			this.pl.getLogger().log(Level.SEVERE, "MySQL error.");
			e.printStackTrace();
		} catch (ClassNotFoundException e2) {
			this.pl.getLogger().log(Level.SEVERE, "No MySQL driver found. Aborting!");
			Bukkit.getServer().getPluginManager().disablePlugin((Plugin) this.pl);
		}
		return null;
	}

	private void connect() throws SQLException, ClassNotFoundException {
		if (this.conn != null && !this.conn.isClosed()) {
			return;
		}
		synchronized (this) {
			if (this.conn != null && !this.conn.isClosed()) {
				// monitorexit(this)
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
					this.username, this.password);
		}
	}
}
