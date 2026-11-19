package com.urntt.hiteffect.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.compat.OneConfigBridge;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The mod's own settings and the configuration mode, persisted as JSON. Every change is written to disk immediately.
 */
public final class HitEffectConfig implements EffectSettings {
	private static final Logger LOGGER = LoggerFactory.getLogger(HitEffectClient.MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path path;
	private final Settings settings;

	private HitEffectConfig(final Path path, final Settings settings) {
		this.path = path;
		this.settings = settings;
	}

	/**
	 * Returns the location of the config file in the Fabric config directory.
	 */
	public static Path defaultPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(HitEffectClient.MOD_ID + ".json");
	}

	/**
	 * Loads the configuration from {@code path}. A missing file is created with the defaults. An unreadable
	 * file falls back to the defaults and is left untouched until the next change is saved.
	 */
	public static HitEffectConfig load(final Path path) {
		if (Files.notExists(path)) {
			HitEffectConfig config = new HitEffectConfig(path, new Settings());
			config.save();
			return config;
		}

		try {
			Settings settings = GSON.fromJson(Files.readString(path), Settings.class);
			return new HitEffectConfig(path, settings != null ? settings.normalized() : new Settings());
		} catch (IOException | JsonParseException e) {
			LOGGER.warn("Failed to read config file {}, using defaults", path, e);
			return new HitEffectConfig(path, new Settings());
		}
	}

	/** The mode stored in the file, which may name OneConfig even while OneConfig is not installed. */
	public ConfigMode storedMode() {
		return this.settings.mode;
	}

	/** The mode that applies: OneConfig mode falls back to custom settings while OneConfig is not available. */
	public ConfigMode effectiveMode() {
		ConfigMode mode = this.settings.mode;
		return mode == ConfigMode.ONECONFIG && !OneConfigBridge.isAvailable() ? ConfigMode.CUSTOM : mode;
	}

	/** The settings that apply in the {@linkplain #effectiveMode() effective mode}. */
	public EffectSettings effective() {
		return switch (this.effectiveMode()) {
			case DISABLED -> EffectValues.DISABLED;
			case CUSTOM -> this;
			case ONECONFIG -> OneConfigBridge.settings().orElse(this);
		};
	}

	public void setMode(final ConfigMode mode) {
		this.settings.mode = mode;
		this.save();
	}

	@Override
	public boolean flashes(final FlashPart part) {
		return this.settings.flash.getOrDefault(part.id(), EffectValues.DEFAULTS.flashes(part));
	}

	public void setFlashes(final FlashPart part, final boolean flashes) {
		this.settings.flash.put(part.id(), flashes);
		this.save();
	}

	@Override
	public int hurtColor() {
		return this.settings.hurtColor;
	}

	public void setHurtColor(final int hurtColor) {
		this.settings.hurtColor = hurtColor & 0xFFFFFF;
		this.save();
	}

	@Override
	public int hurtStrength() {
		return this.settings.hurtStrength;
	}

	public void setHurtStrength(final int hurtStrength) {
		this.settings.hurtStrength = Mth.clamp(hurtStrength, HurtColors.MIN_STRENGTH, HurtColors.MAX_STRENGTH);
		this.save();
	}

	@Override
	public boolean rainbow() {
		return this.settings.rainbow;
	}

	public void setRainbow(final boolean rainbow) {
		this.settings.rainbow = rainbow;
		this.save();
	}

	@Override
	public boolean sharpnessParticles() {
		return this.settings.sharpnessParticles;
	}

	public void setSharpnessParticles(final boolean sharpnessParticles) {
		this.settings.sharpnessParticles = sharpnessParticles;
		this.save();
	}

	@Override
	public boolean critParticles() {
		return this.settings.critParticles;
	}

	public void setCritParticles(final boolean critParticles) {
		this.settings.critParticles = critParticles;
		this.save();
	}

	private void save() {
		try {
			Files.createDirectories(this.path.getParent());
			Files.writeString(this.path, GSON.toJson(this.settings));
		} catch (IOException e) {
			LOGGER.error("Failed to write config file {}", this.path, e);
		}
	}

	/**
	 * The serialized form. Field initializers are the defaults, which also apply to keys missing from the file.
	 */
	private static final class Settings {
		private ConfigMode mode = ConfigMode.CUSTOM;
		/** Whether each {@link FlashPart} flashes, by its id. */
		private Map<String, Boolean> flash = defaultFlash();
		private int hurtColor = EffectValues.DEFAULTS.hurtColor();
		private int hurtStrength = EffectValues.DEFAULTS.hurtStrength();
		private boolean rainbow = EffectValues.DEFAULTS.rainbow();
		private boolean sharpnessParticles = EffectValues.DEFAULTS.sharpnessParticles();
		private boolean critParticles = EffectValues.DEFAULTS.critParticles();

		private static Map<String, Boolean> defaultFlash() {
			Map<String, Boolean> flash = new LinkedHashMap<>();
			for (FlashPart part : FlashPart.values()) {
				flash.put(part.id(), EffectValues.DEFAULTS.flashes(part));
			}
			return flash;
		}

		/** Replaces values a hand-edited file may have put out of range, and adds parts missing from it. */
		private Settings normalized() {
			if (this.mode == null) {
				this.mode = ConfigMode.CUSTOM;
			}
			Map<String, Boolean> flash = defaultFlash();
			if (this.flash != null) {
				this.flash.forEach((id, value) -> {
					if (flash.containsKey(id) && value != null) {
						flash.put(id, value);
					}
				});
			}
			this.flash = flash;
			this.hurtColor &= 0xFFFFFF;
			this.hurtStrength = Mth.clamp(this.hurtStrength, HurtColors.MIN_STRENGTH, HurtColors.MAX_STRENGTH);
			return this;
		}
	}
}
