package com.duckcraftpvp;

import org.bukkit.plugin.java.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.logging.*;
import org.bukkit.plugin.*;
import java.sql.*;

public class LicenseRedeem extends JavaPlugin {
	private ConfigManager configMan;
	private SQLManager sqlMan;

	public void onEnable() {
		this.configMan = new ConfigManager(this);
		this.sqlMan = new SQLManager(this, this.configMan);
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.configMan.config.getString("messages.OnlyPlayer"));
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(this.configMan.config.getString("lang.Help"));
			return true;
		}
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(this.configMan.config.getString("lang.Help"));
			return true;
		}
		try {
			final ResultSet result = this.sqlMan.runSQL(
					"SELECT lkey_active, lkey_purchase FROM nexus_licensekeys WHERE lkey_key='" + args[0] + "'", false);
			int count = 0;
			while (result.next()) {
				if (result.isAfterLast()) {
					result.previous();
					break;
				}
				++count;
			}
			if (count == 0) {
				sender.sendMessage(this.configMan.config.getString("lang.NotValid"));
				return true;
			}
			if (result.isAfterLast()) {
				result.previous();
			}
			if (result.getInt(1) != 1) {
				sender.sendMessage(this.configMan.config.getString("lang.UsedKey"));
				return true;
			}
			final ResultSet result2 = this.sqlMan
					.runSQL("SELECT * FROM nexus_purchases WHERE ps_id='" + result.getInt(2) + "'", false);
			while (result.next()) {
				if (result.isAfterLast()) {
					result.previous();
					break;
				}
			}
			result2.last();
			for (final String command : this.getConfig().getStringList("packages." + result2.getString(3))) {
				Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(),
						command.replace("%player%", sender.getName()));
			}
			this.sqlMan.runSQL(
					"UPDATE nexus_licensekeys SET lkey_active=0, lkey_uses=1 WHERE lkey_key='" + args[0] + "'", true);
			sender.sendMessage(this.configMan.config.getString("lang.SuccessRedeem"));
		} catch (SQLException e) {
			this.getLogger().log(Level.SEVERE, "MySQL error.");
			e.printStackTrace();
			this.getServer().getPluginManager().disablePlugin((Plugin) this);
		}
		return true;
	}
}
