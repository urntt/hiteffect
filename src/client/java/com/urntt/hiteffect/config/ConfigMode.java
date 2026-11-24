package com.urntt.hiteffect.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.chat.Component;

/**
 * Where the effect settings come from.
 */
public enum ConfigMode {
	/** Every effect is off and the hurt flash is vanilla. */
	@SerializedName("disabled")
	DISABLED("disabled"),

	/** The mod's own settings apply. */
	@SerializedName("custom")
	CUSTOM("custom"),

	/**
	 * The settings registered with OneConfig apply. Only available while OneConfig is installed; without it, the mod's
	 * own settings apply as in {@link #CUSTOM}.
	 */
	@SerializedName("oneconfig")
	ONECONFIG("oneconfig");

	/** The mode of a new installation. Without OneConfig installed, it applies the mod's own settings. */
	public static final ConfigMode DEFAULT = ONECONFIG;

	private final String id;

	ConfigMode(final String id) {
		this.id = id;
	}

	public Component label() {
		return Component.translatable(SettingKeys.MODE + "." + this.id);
	}

	public Component description() {
		return Component.translatable(SettingKeys.MODE + "." + this.id + SettingKeys.DESCRIPTION);
	}
}
