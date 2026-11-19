package com.urntt.hiteffect.config;

/**
 * Translation keys of the settings, shared by the configuration screen and the OneConfig integration. They are
 * compile-time constants so that OneConfig's option annotations can use them.
 */
public final class SettingKeys {
	private static final String PREFIX = "options.hiteffect.";

	public static final String TITLE = PREFIX + "title";
	public static final String MODE = PREFIX + "mode";
	public static final String ONECONFIG_MISSING = PREFIX + "mode.oneconfig.missing";
	public static final String OPEN_ONECONFIG = PREFIX + "open_oneconfig";

	public static final String HURT_FLASH = PREFIX + "hurt_flash";
	public static final String ARMOR_FLASH = PREFIX + "armor_flash";
	public static final String ITEM_FLASH = PREFIX + "item_flash";
	public static final String FIRST_PERSON_ARM_FLASH = PREFIX + "first_person_arm_flash";

	public static final String HURT_COLOR = PREFIX + "hurt_color";
	public static final String HURT_COLOR_HEX = PREFIX + "hurt_color.hex";
	public static final String HURT_COLOR_PREVIEW = PREFIX + "hurt_color.preview";
	public static final String HURT_COLOR_PRESETS = PREFIX + "hurt_color.presets";
	public static final String RED = PREFIX + "red";
	public static final String GREEN = PREFIX + "green";
	public static final String BLUE = PREFIX + "blue";
	public static final String RAINBOW = PREFIX + "rainbow";
	public static final String HURT_STRENGTH = PREFIX + "hurt_strength";

	public static final String HIT_PARTICLES = PREFIX + "hit_particles";
	public static final String SHARPNESS_PARTICLES = PREFIX + "sharpness_particles";
	public static final String CRIT_PARTICLES = PREFIX + "crit_particles";

	/** Suffix of the key that describes a setting, shown as its tooltip. */
	public static final String DESCRIPTION = ".description";

	private SettingKeys() {
	}
}
