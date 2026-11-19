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

	public static final String EQUIPMENT_FLASH = PREFIX + "equipment_flash";
	public static final String ITEM_FLASH = PREFIX + "item_flash";
	public static final String FIRST_PERSON_FLASH = PREFIX + "first_person_flash";

	/** Prefix of the keys of the {@link FlashPart}s; the rest of each key is the part's id in the config file. */
	public static final String FLASH_PART_PREFIX = PREFIX + "flash.";
	public static final String FLASH_ARMOR = FLASH_PART_PREFIX + "armor";
	public static final String FLASH_ELYTRA = FLASH_PART_PREFIX + "elytra";
	public static final String FLASH_HORSE_ARMOR = FLASH_PART_PREFIX + "horse_armor";
	public static final String FLASH_WOLF_ARMOR = FLASH_PART_PREFIX + "wolf_armor";
	public static final String FLASH_SADDLES = FLASH_PART_PREFIX + "saddles";
	public static final String FLASH_OTHER_EQUIPMENT = FLASH_PART_PREFIX + "other_equipment";
	public static final String FLASH_HELD_ITEMS = FLASH_PART_PREFIX + "held_items";
	public static final String FLASH_HEAD_ITEMS = FLASH_PART_PREFIX + "head_items";
	public static final String FLASH_VILLAGER_ITEMS = FLASH_PART_PREFIX + "villager_items";
	public static final String FLASH_FOX_ITEMS = FLASH_PART_PREFIX + "fox_items";
	public static final String FLASH_PANDA_ITEMS = FLASH_PART_PREFIX + "panda_items";
	public static final String FLASH_DOLPHIN_ITEMS = FLASH_PART_PREFIX + "dolphin_items";
	public static final String FLASH_ENDERMAN_BLOCKS = FLASH_PART_PREFIX + "enderman_blocks";
	public static final String FLASH_FIRST_PERSON_ARM = FLASH_PART_PREFIX + "first_person_arm";

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
