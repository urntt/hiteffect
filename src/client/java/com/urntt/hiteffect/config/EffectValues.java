package com.urntt.hiteffect.config;

/**
 * An immutable set of effect settings.
 */
public record EffectValues(
		boolean armorFlash,
		boolean itemFlash,
		boolean firstPersonArmFlash,
		int hurtColor,
		int hurtStrength,
		boolean rainbow,
		boolean sharpnessParticles,
		boolean critParticles) implements EffectSettings {
	/** The defaults of the mod's own settings and of its OneConfig settings. */
	public static final EffectValues DEFAULTS = new EffectValues(
			true, true, true, HurtColors.VANILLA_COLOR, HurtColors.VANILLA_STRENGTH, false, true, false);

	/** Vanilla behavior, applied while the mod is disabled. */
	public static final EffectValues DISABLED = new EffectValues(
			false, false, false, HurtColors.VANILLA_COLOR, HurtColors.VANILLA_STRENGTH, false, false, false);

	/** Copies the current values of {@code settings}. */
	public static EffectValues copyOf(final EffectSettings settings) {
		return new EffectValues(
				settings.armorFlash(),
				settings.itemFlash(),
				settings.firstPersonArmFlash(),
				settings.hurtColor(),
				settings.hurtStrength(),
				settings.rainbow(),
				settings.sharpnessParticles(),
				settings.critParticles());
	}
}
