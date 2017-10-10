package com.duckcraftpvp;

import org.bukkit.configuration.file.*;
import java.util.logging.*;
import java.io.*;

public class ConfigManager {
	public FileConfiguration config;

	public ConfigManager(final LicenseRedeem pl) {
		pl.getLogger().log(Level.INFO, "Loading configuration...");
		if (!new File(pl.getDataFolder(), "config.yml").exists()) {
			pl.saveDefaultConfig();
		}
		this.config = pl.getConfig();
	}
}
