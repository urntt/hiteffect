package com.urntt.hiteffect.gametest;

import static com.urntt.hiteffect.gametest.GameTestSupport.check;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.compat.OneConfigBridge;
import com.urntt.hiteffect.config.ConfigMode;
import com.urntt.hiteffect.config.EffectValues;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.config.HitEffectConfig;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.config.SettingKeys;
import com.urntt.hiteffect.gui.HitEffectConfigScreen;
import com.urntt.hiteffect.gui.IntSlider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks the defaults, the configuration modes and the color inputs of the configuration screen. The test client runs
 * without OneConfig by default and with it when Gradle is given {@code -PoneConfigRuntime=true}; the checks follow
 * whichever modes are offered.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ConfigScreenGameTest implements FabricClientGameTest {
	private static final Logger LOGGER = LoggerFactory.getLogger("hiteffect-gametest");

	/** Mouse wheel steps that scroll the screen past its first page, to the item toggles. */
	private static final int MIDDLE_SCROLL = 8;

	@Override
	public void runTest(final ClientGameTestContext context) {
		// The run directory is deleted before each run, so this is the state of a fresh installation.
		HitEffectConfig config = HitEffectClient.config();
		check(config.storedMode() == ConfigMode.ONECONFIG, "the default mode should be OneConfig, was " + config.storedMode());
		check(EffectValues.copyOf(config).equals(EffectValues.DEFAULTS), "the custom settings should start at the defaults, were " + EffectValues.copyOf(config));
		HitEffectConfig saved = HitEffectConfig.load(HitEffectConfig.defaultPath());
		check(saved.storedMode() == ConfigMode.ONECONFIG && EffectValues.copyOf(saved).equals(EffectValues.DEFAULTS),
				"the config file should be created with the defaults");
		boolean oneConfigInstalled = FabricLoader.getInstance().isModLoaded(OneConfigBridge.MOD_ID);
		check(OneConfigBridge.isAvailable() == oneConfigInstalled,
				"OneConfig mode should be available exactly when OneConfig is installed (installed: " + oneConfigInstalled + ")");
		checkDefaultMode(context, config, oneConfigInstalled);

		context.runOnClient(client -> config.setMode(ConfigMode.CUSTOM));
		context.setScreen(() -> new HitEffectConfigScreen(null));
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-custom");
		check(settingsActive(context), "settings should be editable in custom mode");

		checkColorInputs(context, config);
		checkModeCycle(context, config, oneConfigInstalled);

		for (String language : List.of("en_us", "zh_cn")) {
			setLanguage(context, language);
			screenshotAllOptions(context, language);
		}
		setLanguage(context, "en_us");
		context.setScreen(() -> null);
	}

	/**
	 * A new installation is in OneConfig mode. Without OneConfig, that mode applies the mod's own settings, and the
	 * mode button shows custom mode, as the only offered modes are disabled and custom.
	 */
	private static void checkDefaultMode(final ClientGameTestContext context, final HitEffectConfig config, final boolean oneConfigInstalled) {
		ConfigMode expected = oneConfigInstalled ? ConfigMode.ONECONFIG : ConfigMode.CUSTOM;
		check(config.effectiveMode() == expected, "the default mode should apply as " + expected + ", applied as " + config.effectiveMode());
		if (!oneConfigInstalled) {
			check(HitEffectClient.settings() == config, "without OneConfig, the default mode should apply the mod's own settings");
		}

		context.setScreen(() -> new HitEffectConfigScreen(null));
		parkCursor(context);
		ConfigMode shown = context.computeOnClient(client -> (ConfigMode) widgets(client).stream()
				.filter(widget -> widget instanceof CycleButton<?> cycle && cycle.getValue() instanceof ConfigMode)
				.map(widget -> ((CycleButton<?>) widget).getValue())
				.findFirst()
				.orElseThrow(() -> new AssertionError("mode button not found")));
		check(shown == expected, "the mode button should show " + expected + " by default, showed " + shown);
		if (expected == ConfigMode.CUSTOM) {
			check(settingsActive(context), "settings should be editable when the default mode applies the mod's own settings");
		} else {
			check(settingsInactive(context), "settings should be greyed out in the default OneConfig mode");
		}
		context.takeScreenshot("hiteffect-config-default");
	}

	/**
	 * Clicks the mode button through every offered mode and back to custom. OneConfig mode is only offered while
	 * OneConfig is installed.
	 */
	private static void checkModeCycle(final ClientGameTestContext context, final HitEffectConfig config, final boolean oneConfigInstalled) {
		List<ConfigMode> modes = oneConfigInstalled
				? List.of(ConfigMode.CUSTOM, ConfigMode.ONECONFIG, ConfigMode.DISABLED)
				: List.of(ConfigMode.CUSTOM, ConfigMode.DISABLED);
		// A custom value that differs from OneConfig's default shows which settings apply in OneConfig mode.
		context.runOnClient(client -> config.setFlashes(FlashPart.ARMOR, false));

		for (int i = 1; i <= modes.size(); i++) {
			ConfigMode expected = modes.get(i % modes.size());
			clickWidget(context, widget -> widget instanceof CycleButton<?> cycle && cycle.getValue() instanceof ConfigMode);
			context.waitForScreen(HitEffectConfigScreen.class);
			check(config.storedMode() == expected, "the mode button should switch to " + expected + ", was " + config.storedMode());
			check(HitEffectConfig.load(HitEffectConfig.defaultPath()).storedMode() == expected, "the " + expected + " mode should be saved");
			if (expected == ConfigMode.CUSTOM) {
				check(settingsActive(context), "settings should be editable again in custom mode");
			} else {
				check(settingsInactive(context), "settings should be greyed out in " + expected + " mode");
			}
			parkCursor(context);
			context.takeScreenshot("hiteffect-config-" + expected.name().toLowerCase(Locale.ROOT));

			if (expected == ConfigMode.DISABLED) {
				check(EffectValues.copyOf(HitEffectClient.settings()).equals(EffectValues.DISABLED), "disabled mode should apply vanilla settings");
			} else if (expected == ConfigMode.ONECONFIG) {
				checkOneConfigMode(context);
			}
		}

		context.runOnClient(client -> config.setFlashes(FlashPart.ARMOR, EffectValues.DEFAULTS.flashes(FlashPart.ARMOR)));
	}

	/** In OneConfig mode, OneConfig's settings apply and the screen links to the mod's OneConfig page. */
	private static void checkOneConfigMode(final ClientGameTestContext context) {
		check(HitEffectClient.settings().flashes(FlashPart.ARMOR), "OneConfig mode should apply OneConfig's settings instead of the custom ones");

		Component openLabel = Component.translatable(SettingKeys.OPEN_ONECONFIG);
		clickWidget(context, widget -> widget instanceof Button && widget.getMessage().equals(openLabel));
		context.waitFor(client -> client.gui.screen() != null && !(client.gui.screen() instanceof HitEffectConfigScreen));
		context.waitTicks(20);
		context.takeScreenshot("hiteffect-oneconfig-page");
		String screen = context.computeOnClient(client -> client.gui.screen().getClass().getName());
		LOGGER.info("Open in OneConfig shows {}", screen);
		context.setScreen(() -> new HitEffectConfigScreen(null));
		// The option list positions its widgets while rendering, so let it render before clicking.
		context.waitTicks(2);
	}

	/** Shows the whole screen in three screenshots: scrolled to the top, partway down, and to the bottom. */
	private static void screenshotAllOptions(final ClientGameTestContext context, final String language) {
		context.setScreen(() -> new HitEffectConfigScreen(null));
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-" + language + "-top");
		double[] center = context.computeOnClient(client -> {
			Window window = client.getWindow();
			return new double[] {window.getScreenWidth() / 2.0, window.getScreenHeight() / 2.0};
		});
		context.getInput().setCursorPos(center[0], center[1]);
		context.getInput().scroll(-MIDDLE_SCROLL);
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-" + language + "-middle");
		context.getInput().setCursorPos(center[0], center[1]);
		context.getInput().scroll(-100);
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-" + language + "-bottom");
	}

	private static void setLanguage(final ClientGameTestContext context, final String code) {
		CompletableFuture<Void> reload = context.computeOnClient(client -> {
			client.getLanguageManager().setSelected(code);
			client.options.languageCode = code;
			return client.reloadResourcePacks();
		});
		context.waitFor(client -> reload.isDone() && client.gui.overlay() == null);
	}

	/** Types colors into the hex box and checks that the setting and the RGB sliders follow. */
	private static void checkColorInputs(final ClientGameTestContext context, final HitEffectConfig config) {
		int green = 0x00FF00;
		List<Integer> channels = context.computeOnClient(client -> {
			hexBox(client).setValue(HurtColors.toHex(green));
			return sliderValues(client);
		});
		check(config.hurtColor() == green, "typing a hex color should set the hurt color, was " + HurtColors.toHex(config.hurtColor()));
		check(channels.subList(0, 3).equals(List.of(0, 255, 0)), "the RGB sliders should follow the hex box, were " + channels);
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-green");

		context.runOnClient(client -> hexBox(client).setValue("#12"));
		check(config.hurtColor() == green, "an incomplete hex color should not change the hurt color");

		context.runOnClient(client -> hexBox(client).setValue(HurtColors.toHex(EffectValues.DEFAULTS.hurtColor())));
		check(config.hurtColor() == EffectValues.DEFAULTS.hurtColor(), "the hurt color should be back at the default");
		LOGGER.info("Hex box and RGB sliders stay in sync: {} -> {}", HurtColors.toHex(green), channels);
	}

	/** Clicks the first widget of the open screen that matches, with the real mouse. */
	private static void clickWidget(final ClientGameTestContext context, final Predicate<AbstractWidget> matches) {
		double[] position = context.computeOnClient(client -> {
			AbstractWidget widget = widgets(client).stream()
					.filter(matches)
					.findFirst()
					.orElseThrow(() -> new AssertionError("widget to click not found"));
			Window window = client.getWindow();
			double scale = window.getScreenWidth() / (double) window.getGuiScaledWidth();
			return new double[] {(widget.getX() + widget.getWidth() / 2.0) * scale, (widget.getY() + widget.getHeight() / 2.0) * scale};
		});
		context.getInput().setCursorPos(position[0], position[1]);
		context.getInput().pressMouse(InputConstants.MOUSE_BUTTON_LEFT);
		context.waitTick();
	}

	/** Moves the cursor into a corner, so that no tooltip covers the screenshot. */
	private static void parkCursor(final ClientGameTestContext context) {
		context.getInput().setCursorPos(0, 0);
		context.waitTicks(2);
	}

	private static boolean settingsActive(final ClientGameTestContext context) {
		return context.computeOnClient(client -> settingWidgets(client).stream().allMatch(widget -> widget.active));
	}

	private static boolean settingsInactive(final ClientGameTestContext context) {
		return context.computeOnClient(client -> settingWidgets(client).stream().noneMatch(widget -> widget.active));
	}

	/** Every widget that edits a setting: toggles, sliders and the hex box, but not the mode button. */
	private static List<AbstractWidget> settingWidgets(final Minecraft client) {
		List<AbstractWidget> settings = widgets(client).stream()
				.filter(widget -> widget instanceof CycleButton<?> cycle && cycle.getValue() instanceof Boolean
						|| widget instanceof IntSlider
						|| widget instanceof EditBox)
				.toList();
		// A toggle per flash part, the rainbow and the two particle toggles; red, green, blue and strength.
		int expected = FlashPart.values().length + 3 + 4 + 1;
		check(settings.size() == expected, "expected " + expected + " toggles, sliders and the hex box, found " + settings.size());
		return settings;
	}

	private static EditBox hexBox(final Minecraft client) {
		return widgets(client).stream()
				.filter(EditBox.class::isInstance)
				.map(EditBox.class::cast)
				.findFirst()
				.orElseThrow(() -> new AssertionError("hex box not found"));
	}

	/** The values of the red, green, blue and strength sliders, in screen order. */
	private static List<Integer> sliderValues(final Minecraft client) {
		return widgets(client).stream()
				.filter(IntSlider.class::isInstance)
				.map(widget -> ((IntSlider) widget).getIntValue())
				.toList();
	}

	private static List<AbstractWidget> widgets(final Minecraft client) {
		Screen screen = Objects.requireNonNull(client.gui.screen(), "no screen is open");
		List<AbstractWidget> widgets = new ArrayList<>();
		collect(screen.children(), widgets);
		return widgets;
	}

	private static void collect(final List<? extends GuiEventListener> listeners, final List<AbstractWidget> widgets) {
		for (GuiEventListener listener : listeners) {
			if (listener instanceof AbstractWidget widget) {
				widgets.add(widget);
			}
			if (listener instanceof ContainerEventHandler container) {
				collect(container.children(), widgets);
			}
		}
	}
}
