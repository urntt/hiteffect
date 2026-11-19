package com.urntt.hiteffect.gametest;

import static com.urntt.hiteffect.gametest.GameTestSupport.check;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.compat.OneConfigBridge;
import com.urntt.hiteffect.config.ConfigMode;
import com.urntt.hiteffect.config.EffectValues;
import com.urntt.hiteffect.config.HitEffectConfig;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.gui.HitEffectConfigScreen;
import com.urntt.hiteffect.gui.IntSlider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks the defaults, the configuration modes and the color inputs of the configuration screen. OneConfig is not
 * installed in the test client, so only the disabled and custom modes are offered.
 */
@SuppressWarnings("UnstableApiUsage")
public final class ConfigScreenGameTest implements FabricClientGameTest {
	private static final Logger LOGGER = LoggerFactory.getLogger("hiteffect-gametest");

	@Override
	public void runTest(final ClientGameTestContext context) {
		// The run directory is deleted before each run, so this is the state of a fresh installation.
		HitEffectConfig config = HitEffectClient.config();
		check(config.storedMode() == ConfigMode.CUSTOM, "the default mode should be custom, was " + config.storedMode());
		check(EffectValues.copyOf(config).equals(EffectValues.DEFAULTS), "the custom settings should start at the defaults, were " + EffectValues.copyOf(config));
		HitEffectConfig saved = HitEffectConfig.load(HitEffectConfig.defaultPath());
		check(saved.storedMode() == ConfigMode.CUSTOM && EffectValues.copyOf(saved).equals(EffectValues.DEFAULTS),
				"the config file should be created with the defaults");
		check(!OneConfigBridge.isAvailable(), "OneConfig is not installed in the test client");

		context.setScreen(() -> new HitEffectConfigScreen(null));
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-custom");
		check(settingsActive(context), "settings should be editable in custom mode");

		checkColorInputs(context, config);

		clickModeButton(context);
		check(config.storedMode() == ConfigMode.DISABLED, "the mode button should switch to disabled, was " + config.storedMode());
		check(HitEffectConfig.load(HitEffectConfig.defaultPath()).storedMode() == ConfigMode.DISABLED, "the disabled mode should be saved");
		check(settingsInactive(context), "settings should be greyed out in disabled mode");
		check(EffectValues.copyOf(HitEffectClient.settings()).equals(EffectValues.DISABLED), "disabled mode should apply vanilla settings");
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-disabled");

		clickModeButton(context);
		check(config.storedMode() == ConfigMode.CUSTOM,
				"without OneConfig the mode button should cycle back to custom, was " + config.storedMode());
		check(settingsActive(context), "settings should be editable again in custom mode");

		for (String language : List.of("en_us", "zh_cn")) {
			setLanguage(context, language);
			screenshotAllOptions(context, language);
		}
		setLanguage(context, "en_us");
		context.setScreen(() -> null);
	}

	/** Shows the whole screen in two screenshots, scrolled to the top and to the bottom. */
	private static void screenshotAllOptions(final ClientGameTestContext context, final String language) {
		context.setScreen(() -> new HitEffectConfigScreen(null));
		parkCursor(context);
		context.takeScreenshot("hiteffect-config-" + language + "-top");
		double[] center = context.computeOnClient(client -> {
			Window window = client.getWindow();
			return new double[] {window.getScreenWidth() / 2.0, window.getScreenHeight() / 2.0};
		});
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

	private static void clickModeButton(final ClientGameTestContext context) {
		double[] position = context.computeOnClient(client -> {
			AbstractWidget button = widgets(client).stream()
					.filter(widget -> widget instanceof CycleButton<?> cycle && cycle.getValue() instanceof ConfigMode)
					.findFirst()
					.orElseThrow(() -> new AssertionError("mode button not found"));
			Window window = client.getWindow();
			double scale = window.getScreenWidth() / (double) window.getGuiScaledWidth();
			return new double[] {(button.getX() + button.getWidth() / 2.0) * scale, (button.getY() + button.getHeight() / 2.0) * scale};
		});
		context.getInput().setCursorPos(position[0], position[1]);
		context.getInput().pressMouse(InputConstants.MOUSE_BUTTON_LEFT);
		context.waitTick();
		context.waitForScreen(HitEffectConfigScreen.class);
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
		check(settings.size() == 11, "expected 6 toggles, 4 sliders and the hex box, found " + settings.size());
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
