package com.urntt.hiteffect.config;

import net.minecraft.network.chat.Component;

/**
 * A part that vanilla leaves out of the hurt flash and that can be set to flash with the hurt entity.
 */
public enum FlashPart {
	/** Armor worn by humanoids. */
	ARMOR(Group.EQUIPMENT, SettingKeys.FLASH_ARMOR),
	ELYTRA(Group.EQUIPMENT, SettingKeys.FLASH_ELYTRA),
	HORSE_ARMOR(Group.EQUIPMENT, SettingKeys.FLASH_HORSE_ARMOR),
	/** Wolf armor, including the cracks of damaged armor. */
	WOLF_ARMOR(Group.EQUIPMENT, SettingKeys.FLASH_WOLF_ARMOR),
	/** Saddles of every rideable mob. */
	SADDLES(Group.EQUIPMENT, SettingKeys.FLASH_SADDLES),
	/** Llama carpets, happy ghast harnesses and nautilus armor. */
	OTHER_EQUIPMENT(Group.EQUIPMENT, SettingKeys.FLASH_OTHER_EQUIPMENT),
	/** Items held in the hands, including a spyglass held to the eye. */
	HELD_ITEMS(Group.ITEMS, SettingKeys.FLASH_HELD_ITEMS),
	/** Carved pumpkins, skulls and blocks worn on the head. */
	HEAD_ITEMS(Group.ITEMS, SettingKeys.FLASH_HEAD_ITEMS),
	/** Items villagers and wandering traders hold in their crossed arms. */
	VILLAGER_ITEMS(Group.ITEMS, SettingKeys.FLASH_VILLAGER_ITEMS),
	FOX_ITEMS(Group.ITEMS, SettingKeys.FLASH_FOX_ITEMS),
	PANDA_ITEMS(Group.ITEMS, SettingKeys.FLASH_PANDA_ITEMS),
	DOLPHIN_ITEMS(Group.ITEMS, SettingKeys.FLASH_DOLPHIN_ITEMS),
	ENDERMAN_BLOCKS(Group.ITEMS, SettingKeys.FLASH_ENDERMAN_BLOCKS),
	/** The local player's arm, including the sleeve, in first person. */
	FIRST_PERSON_ARM(Group.FIRST_PERSON, SettingKeys.FLASH_FIRST_PERSON_ARM);

	private final Group group;
	private final String translationKey;

	FlashPart(final Group group, final String translationKey) {
		this.group = group;
		this.translationKey = translationKey;
	}

	public Group group() {
		return this.group;
	}

	/** The part's key in the config file. */
	public String id() {
		return this.translationKey.substring(SettingKeys.FLASH_PART_PREFIX.length());
	}

	public String translationKey() {
		return this.translationKey;
	}

	public Component label() {
		return Component.translatable(this.translationKey);
	}

	/** How the configuration screen and OneConfig group the parts. */
	public enum Group {
		EQUIPMENT(SettingKeys.EQUIPMENT_FLASH),
		ITEMS(SettingKeys.ITEM_FLASH),
		FIRST_PERSON(SettingKeys.FIRST_PERSON_FLASH);

		private final String translationKey;

		Group(final String translationKey) {
			this.translationKey = translationKey;
		}

		public Component label() {
			return Component.translatable(this.translationKey);
		}
	}
}
