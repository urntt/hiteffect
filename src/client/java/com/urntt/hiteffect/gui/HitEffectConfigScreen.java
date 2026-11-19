package com.urntt.hiteffect.gui;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.compat.OneConfigBridge;
import com.urntt.hiteffect.config.ConfigMode;
import com.urntt.hiteffect.config.EffectSettings;
import com.urntt.hiteffect.config.HitEffectConfig;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.config.SettingKeys;
import com.urntt.hiteffect.config.HurtColors.Channel;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

/**
 * The configuration screen, built from vanilla widgets. The mode button comes first. The settings below it can only
 * be edited in custom mode; otherwise they are greyed out and show the values that currently apply.
 */
public final class HitEffectConfigScreen extends OptionsSubScreen {
	private static final int INVALID_TEXT_COLOR = 0xFFFF5555;
	private static final int HEX_MAX_LENGTH = 7;

	private final HitEffectConfig config = HitEffectClient.config();
	private final List<AbstractWidget> colorInputs = new ArrayList<>();
	private final Map<Channel, IntSlider> channelSliders = new EnumMap<>(Channel.class);
	private @Nullable EditBox hexBox;
	private boolean syncingColor;

	public HitEffectConfigScreen(final @Nullable Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable(SettingKeys.TITLE));
	}

	@Override
	protected void addOptions() {
		ConfigMode mode = this.config.effectiveMode();
		boolean editable = mode == ConfigMode.CUSTOM;
		EffectSettings shown = this.config.effective();

		this.list.addBig(this.createModeButton(mode));
		if (mode == ConfigMode.ONECONFIG) {
			this.list.addBig(Button.builder(Component.translatable(SettingKeys.OPEN_ONECONFIG), button -> OneConfigBridge.openOneConfig()).build());
		}

		this.list.addHeader(Component.translatable(SettingKeys.HURT_FLASH));
		this.list.addSmall(
				this.toggle(SettingKeys.ARMOR_FLASH, shown.armorFlash(), editable, this.config::setArmorFlash),
				this.toggle(SettingKeys.ITEM_FLASH, shown.itemFlash(), editable, this.config::setItemFlash));
		this.list.addBig(this.toggle(SettingKeys.FIRST_PERSON_ARM_FLASH, shown.firstPersonArmFlash(), editable, this.config::setFirstPersonArmFlash));

		this.addColorOptions(shown, editable);

		this.list.addHeader(Component.translatable(SettingKeys.HIT_PARTICLES));
		this.list.addBig(this.toggle(SettingKeys.SHARPNESS_PARTICLES, shown.sharpnessParticles(), editable, this.config::setSharpnessParticles));
		this.list.addBig(this.toggle(SettingKeys.CRIT_PARTICLES, shown.critParticles(), editable, this.config::setCritParticles));
	}

	private CycleButton<ConfigMode> createModeButton(final ConfigMode mode) {
		List<ConfigMode> modes = OneConfigBridge.isAvailable()
				? List.of(ConfigMode.values())
				: List.of(ConfigMode.DISABLED, ConfigMode.CUSTOM);
		boolean oneConfigMissing = this.config.storedMode() == ConfigMode.ONECONFIG && !OneConfigBridge.isAvailable();
		return CycleButton.builder(ConfigMode::label, mode)
				.withValues(modes)
				.withTooltip(value -> Tooltip.create(oneConfigMissing && value == mode
						? Component.translatable(SettingKeys.ONECONFIG_MISSING)
						: value.description()))
				.create(Component.translatable(SettingKeys.MODE), (button, value) -> {
					this.config.setMode(value);
					// The settings below change their values and whether they can be edited, so start over.
					this.minecraft.gui.setScreen(new HitEffectConfigScreen(this.lastScreen));
				});
	}

	private void addColorOptions(final EffectSettings shown, final boolean editable) {
		int color = shown.hurtColor();
		this.list.addHeader(Component.translatable(SettingKeys.HURT_COLOR));

		this.hexBox = new EditBox(this.font, 150, 20, Component.translatable(SettingKeys.HURT_COLOR_HEX));
		this.hexBox.setMaxLength(HEX_MAX_LENGTH);
		this.hexBox.setValue(HurtColors.toHex(color));
		this.hexBox.setTooltip(Tooltip.create(Component.translatable(SettingKeys.HURT_COLOR_HEX + SettingKeys.DESCRIPTION)));
		this.hexBox.setResponder(this::onHexChanged);
		this.colorInputs.add(this.hexBox);
		this.list.addSmall(this.hexBox, new ColorSwatchWidget(Component.translatable(SettingKeys.HURT_COLOR_PREVIEW), HitEffectConfigScreen::previewColor));

		ColorPresetsWidget presets = new ColorPresetsWidget(
				Component.translatable(SettingKeys.HURT_COLOR_PRESETS), HurtColors.PRESETS, rgb -> this.setColor(rgb, null));
		presets.setTooltip(Tooltip.create(Component.translatable(SettingKeys.HURT_COLOR_PRESETS + SettingKeys.DESCRIPTION)));
		this.colorInputs.add(presets);
		this.list.addBig(presets);

		IntSlider red = this.channelSlider(Channel.RED, SettingKeys.RED, color);
		IntSlider green = this.channelSlider(Channel.GREEN, SettingKeys.GREEN, color);
		IntSlider blue = this.channelSlider(Channel.BLUE, SettingKeys.BLUE, color);
		CycleButton<Boolean> rainbow = this.toggle(SettingKeys.RAINBOW, shown.rainbow(), editable, value -> {
			this.config.setRainbow(value);
			this.updateColorInputs(true);
		});
		this.list.addSmall(red, green);
		this.list.addSmall(blue, rainbow);

		IntSlider strength = new IntSlider(
				HurtColors.MIN_STRENGTH,
				HurtColors.MAX_STRENGTH,
				shown.hurtStrength(),
				value -> Component.translatable("options.percent_value", Component.translatable(SettingKeys.HURT_STRENGTH), value),
				this.config::setHurtStrength);
		strength.setTooltip(Tooltip.create(Component.translatable(SettingKeys.HURT_STRENGTH + SettingKeys.DESCRIPTION)));
		strength.active = editable;
		this.list.addBig(strength);

		this.updateColorInputs(editable);
	}

	private IntSlider channelSlider(final Channel channel, final String key, final int color) {
		IntSlider slider = new IntSlider(
				0,
				Channel.MAX,
				channel.get(color),
				value -> CommonComponents.optionNameValue(Component.translatable(key), Component.literal(Integer.toString(value))),
				value -> this.setColor(channel.with(this.config.hurtColor(), value), null));
		this.channelSliders.put(channel, slider);
		this.colorInputs.add(slider);
		return slider;
	}

	private void onHexChanged(final String text) {
		if (this.syncingColor || this.hexBox == null) {
			return;
		}
		HurtColors.parseHex(text).ifPresentOrElse(
				rgb -> {
					this.hexBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
					this.setColor(rgb, this.hexBox);
				},
				() -> this.hexBox.setTextColor(INVALID_TEXT_COLOR));
	}

	/**
	 * Saves a new hurt color and shows it in every color input except {@code source}, which already shows it.
	 */
	private void setColor(final int rgb, final @Nullable AbstractWidget source) {
		this.config.setHurtColor(rgb);
		this.syncingColor = true;
		try {
			if (this.hexBox != null && source != this.hexBox) {
				this.hexBox.setValue(HurtColors.toHex(rgb));
				this.hexBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
			}
			this.channelSliders.forEach((channel, slider) -> slider.setIntValue(channel.get(rgb)));
		} finally {
			this.syncingColor = false;
		}
	}

	/** The color inputs only apply while the rainbow is off, so they are greyed out while it is on. */
	private void updateColorInputs(final boolean editable) {
		boolean active = editable && !this.config.effective().rainbow();
		for (AbstractWidget input : this.colorInputs) {
			input.active = active;
		}
		if (this.hexBox != null) {
			this.hexBox.setEditable(active);
		}
	}

	private CycleButton<Boolean> toggle(final String key, final boolean value, final boolean editable, final Consumer<Boolean> setter) {
		Tooltip tooltip = Tooltip.create(Component.translatable(key + SettingKeys.DESCRIPTION));
		CycleButton<Boolean> button = CycleButton.onOffBuilder(value)
				.withTooltip(ignored -> tooltip)
				.create(Component.translatable(key), (cycleButton, newValue) -> setter.accept(newValue));
		button.active = editable;
		return button;
	}

	/** The preview follows the settings that apply, including the rainbow. */
	private static int previewColor() {
		EffectSettings settings = HitEffectClient.settings();
		return settings.rainbow() ? HurtColors.rainbow(Util.getMillis()) : settings.hurtColor();
	}

	/** Shows the screen on top of whatever screen is open. */
	public static void open(final Minecraft minecraft) {
		minecraft.gui.setScreen(new HitEffectConfigScreen(minecraft.gui.screen()));
	}
}
