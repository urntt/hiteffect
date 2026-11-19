package com.urntt.hiteffect.compat.oneconfig;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.EffectSettings;
import com.urntt.hiteffect.config.EffectValues;
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
			title = SettingKeys.ARMOR_FLASH,
			titleKey = SettingKeys.ARMOR_FLASH,
			descriptionKey = SettingKeys.ARMOR_FLASH + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HURT_FLASH,
			subcategoryKey = SettingKeys.HURT_FLASH)
	public boolean armorFlash = EffectValues.DEFAULTS.armorFlash();

	@Switch(
			title = SettingKeys.ITEM_FLASH,
			titleKey = SettingKeys.ITEM_FLASH,
			descriptionKey = SettingKeys.ITEM_FLASH + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HURT_FLASH,
			subcategoryKey = SettingKeys.HURT_FLASH)
	public boolean itemFlash = EffectValues.DEFAULTS.itemFlash();

	@Switch(
			title = SettingKeys.FIRST_PERSON_ARM_FLASH,
			titleKey = SettingKeys.FIRST_PERSON_ARM_FLASH,
			descriptionKey = SettingKeys.FIRST_PERSON_ARM_FLASH + SettingKeys.DESCRIPTION,
			subcategory = SettingKeys.HURT_FLASH,
			subcategoryKey = SettingKeys.HURT_FLASH)
	public boolean firstPersonArmFlash = EffectValues.DEFAULTS.firstPersonArmFlash();

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
	public boolean armorFlash() {
		return this.armorFlash;
	}

	@Override
	public boolean itemFlash() {
		return this.itemFlash;
	}

	@Override
	public boolean firstPersonArmFlash() {
		return this.firstPersonArmFlash;
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
