package com.urntt.hiteffect.compat.oneconfig;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.EffectSettings;
import com.urntt.hiteffect.config.EffectValues;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.config.SettingKeys;
import org.polyfrost.compose.render.PolyColor;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Color;
import org.polyfrost.oneconfig.api.config.v1.annotations.MasterToggle;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

/**
 * The settings OneConfig manages in OneConfig mode, with the same defaults as the mod's own settings. OneConfig's
 * chroma color option is the rainbow. Titles use the mod's translation keys; the keys double as fallback titles.
 */
public final class HitEffectOneConfig extends Config implements EffectSettings {
	private static final int OPAQUE = 0xFF000000;

	/**
	 * OneConfig keeps its files in the same config directory as the mod's own {@code hiteffect.json} and watches them
	 * for changes, so its file needs a name of its own.
	 */
	private static final String FILE_NAME = HitEffectClient.MOD_ID + "-oneconfig.json";

	/** The toggle on the mod's OneConfig card. While it is off, every effect is off. */
	@MasterToggle
	public boolean enabled = true;

	@Switch(
			title = SettingKeys.FLASH_ARMOR,
			titleKey = SettingKeys.FLASH_ARMOR,
			descriptionKey = SettingKeys.FLASH_ARMOR + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean armorFlash = EffectValues.DEFAULTS.flashes(FlashPart.ARMOR);

	@Switch(
			title = SettingKeys.FLASH_ELYTRA,
			titleKey = SettingKeys.FLASH_ELYTRA,
			descriptionKey = SettingKeys.FLASH_ELYTRA + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean elytraFlash = EffectValues.DEFAULTS.flashes(FlashPart.ELYTRA);

	@Switch(
			title = SettingKeys.FLASH_HORSE_ARMOR,
			titleKey = SettingKeys.FLASH_HORSE_ARMOR,
			descriptionKey = SettingKeys.FLASH_HORSE_ARMOR + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean horseArmorFlash = EffectValues.DEFAULTS.flashes(FlashPart.HORSE_ARMOR);

	@Switch(
			title = SettingKeys.FLASH_WOLF_ARMOR,
			titleKey = SettingKeys.FLASH_WOLF_ARMOR,
			descriptionKey = SettingKeys.FLASH_WOLF_ARMOR + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean wolfArmorFlash = EffectValues.DEFAULTS.flashes(FlashPart.WOLF_ARMOR);

	@Switch(
			title = SettingKeys.FLASH_SADDLES,
			titleKey = SettingKeys.FLASH_SADDLES,
			descriptionKey = SettingKeys.FLASH_SADDLES + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean saddlesFlash = EffectValues.DEFAULTS.flashes(FlashPart.SADDLES);

	@Switch(
			title = SettingKeys.FLASH_OTHER_EQUIPMENT,
			titleKey = SettingKeys.FLASH_OTHER_EQUIPMENT,
			descriptionKey = SettingKeys.FLASH_OTHER_EQUIPMENT + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.EQUIPMENT_FLASH,
			subcategoryKey = SettingKeys.EQUIPMENT_FLASH)
	public boolean otherEquipmentFlash = EffectValues.DEFAULTS.flashes(FlashPart.OTHER_EQUIPMENT);

	@Switch(
			title = SettingKeys.FLASH_HELD_ITEMS,
			titleKey = SettingKeys.FLASH_HELD_ITEMS,
			descriptionKey = SettingKeys.FLASH_HELD_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean heldItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.HELD_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_HEAD_ITEMS,
			titleKey = SettingKeys.FLASH_HEAD_ITEMS,
			descriptionKey = SettingKeys.FLASH_HEAD_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean headItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.HEAD_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_VILLAGER_ITEMS,
			titleKey = SettingKeys.FLASH_VILLAGER_ITEMS,
			descriptionKey = SettingKeys.FLASH_VILLAGER_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean villagerItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.VILLAGER_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_FOX_ITEMS,
			titleKey = SettingKeys.FLASH_FOX_ITEMS,
			descriptionKey = SettingKeys.FLASH_FOX_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean foxItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.FOX_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_PANDA_ITEMS,
			titleKey = SettingKeys.FLASH_PANDA_ITEMS,
			descriptionKey = SettingKeys.FLASH_PANDA_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean pandaItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.PANDA_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_DOLPHIN_ITEMS,
			titleKey = SettingKeys.FLASH_DOLPHIN_ITEMS,
			descriptionKey = SettingKeys.FLASH_DOLPHIN_ITEMS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean dolphinItemsFlash = EffectValues.DEFAULTS.flashes(FlashPart.DOLPHIN_ITEMS);

	@Switch(
			title = SettingKeys.FLASH_ENDERMAN_BLOCKS,
			titleKey = SettingKeys.FLASH_ENDERMAN_BLOCKS,
			descriptionKey = SettingKeys.FLASH_ENDERMAN_BLOCKS + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.ITEM_FLASH,
			subcategoryKey = SettingKeys.ITEM_FLASH)
	public boolean endermanBlocksFlash = EffectValues.DEFAULTS.flashes(FlashPart.ENDERMAN_BLOCKS);

	@Switch(
			title = SettingKeys.FLASH_FIRST_PERSON_ARM,
			titleKey = SettingKeys.FLASH_FIRST_PERSON_ARM,
			descriptionKey = SettingKeys.FLASH_FIRST_PERSON_ARM + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.FIRST_PERSON_FLASH,
			subcategoryKey = SettingKeys.FIRST_PERSON_FLASH)
	public boolean firstPersonArmFlash = EffectValues.DEFAULTS.flashes(FlashPart.FIRST_PERSON_ARM);

	@Color(
			title = SettingKeys.HURT_COLOR,
			titleKey = SettingKeys.HURT_COLOR,
			descriptionKey = SettingKeys.HURT_COLOR + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HURT_COLOR,
			subcategoryKey = SettingKeys.HURT_COLOR,
			alpha = false)
	public PolyColor hurtColor = new PolyColor(OPAQUE | EffectValues.DEFAULTS.hurtColor(), EffectValues.DEFAULTS.rainbow());

	@Slider(
			title = SettingKeys.HURT_STRENGTH,
			titleKey = SettingKeys.HURT_STRENGTH,
			descriptionKey = SettingKeys.HURT_STRENGTH + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HURT_COLOR,
			subcategoryKey = SettingKeys.HURT_COLOR,
			min = HurtColors.MIN_STRENGTH,
			max = HurtColors.MAX_STRENGTH,
			step = 1)
	public float hurtStrength = EffectValues.DEFAULTS.hurtStrength();

	@Switch(
			title = SettingKeys.SHARPNESS_PARTICLES,
			titleKey = SettingKeys.SHARPNESS_PARTICLES,
			descriptionKey = SettingKeys.SHARPNESS_PARTICLES + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HIT_PARTICLES,
			subcategoryKey = SettingKeys.HIT_PARTICLES)
	public boolean sharpnessParticles = EffectValues.DEFAULTS.sharpnessParticles();

	@Switch(
			title = SettingKeys.CRIT_PARTICLES,
			titleKey = SettingKeys.CRIT_PARTICLES,
			descriptionKey = SettingKeys.CRIT_PARTICLES + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HIT_PARTICLES,
			subcategoryKey = SettingKeys.HIT_PARTICLES)
	public boolean critParticles = EffectValues.DEFAULTS.critParticles();

	public HitEffectOneConfig() {
		super(FILE_NAME, HitEffectClient.MOD_ID, Category.VISUALS);
	}

	/** The settings that apply in OneConfig mode, which are all off while the card's toggle is off. */
	EffectSettings applied() {
		return this.enabled ? this : EffectValues.DISABLED;
	}

	@Override
	public boolean flashes(final FlashPart part) {
		return switch (part) {
			case ARMOR -> this.armorFlash;
			case ELYTRA -> this.elytraFlash;
			case HORSE_ARMOR -> this.horseArmorFlash;
			case WOLF_ARMOR -> this.wolfArmorFlash;
			case SADDLES -> this.saddlesFlash;
			case OTHER_EQUIPMENT -> this.otherEquipmentFlash;
			case HELD_ITEMS -> this.heldItemsFlash;
			case HEAD_ITEMS -> this.headItemsFlash;
			case VILLAGER_ITEMS -> this.villagerItemsFlash;
			case FOX_ITEMS -> this.foxItemsFlash;
			case PANDA_ITEMS -> this.pandaItemsFlash;
			case DOLPHIN_ITEMS -> this.dolphinItemsFlash;
			case ENDERMAN_BLOCKS -> this.endermanBlocksFlash;
			case FIRST_PERSON_ARM -> this.firstPersonArmFlash;
		};
	}

	@Override
	public int hurtColor() {
		return this.hurtColor.getRawArgb() & 0xFFFFFF;
	}

	@Override
	public int hurtStrength() {
		return Math.round(this.hurtStrength);
	}

	@Override
	public boolean rainbow() {
		return this.hurtColor.getChroma();
	}

	@Override
	public boolean sharpnessParticles() {
		return this.sharpnessParticles;
	}

	@Override
	public boolean critParticles() {
		return this.critParticles;
	}
}
