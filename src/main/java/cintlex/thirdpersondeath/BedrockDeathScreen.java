package cintlex.thirdpersondeath;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;

public class BedrockDeathScreen extends Screen {
    private static final int TITLE_SCALE = 2;

    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;

    private final List<Button> exitButtons = new ArrayList<>();
    private Button exitToTitleButton;

    public BedrockDeathScreen(Component causeOfDeath, boolean hardcore) {
        super(Component.translatable(hardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = causeOfDeath;
        this.hardcore = hardcore;
    }

    @Override
    protected void init() {
        super.init();
        this.delayTicker = 0;

        this.exitButtons.clear();

        Component respawnText = this.hardcore
                ? Component.translatable("deathScreen.spectate")
                : Component.literal("Respawn");

        Button respawnBtn = Button.builder(respawnText, this::onRespawn)
                .bounds(this.width / 2 - 100, this.height / 4 + 100, 200, 20)
                .build();
        this.addRenderableWidget(respawnBtn);
        this.exitButtons.add(respawnBtn);

        this.exitToTitleButton = Button.builder(Component.literal("Main Menu"), this::onExitToTitle)
                .bounds(this.width / 2 - 100, this.height / 4 + 124, 200, 20)
                .build();
        this.addRenderableWidget(this.exitToTitleButton);
        this.exitButtons.add(this.exitToTitleButton);

        this.setButtonsActive(false);
    }

    private void onRespawn(Button btn) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.respawn();
            btn.active = false;
        }
    }

    private void onExitToTitle(Button ignored) {
        // For simplicity go straight to menu (skip vanilla's confirm dialog for "Main Menu" feel)
        this.exitToTitleScreen();
    }

    private void exitToTitleScreen() {
        if (this.minecraft == null) return;

        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        }
        this.minecraft.disconnectWithSavingScreen();
        this.minecraft.setScreen(new TitleScreen());
    }

    private void setButtonsActive(boolean active) {
        for (Button b : this.exitButtons) {
            b.active = active;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.delayTicker++;
        if (this.delayTicker == 20) {
            this.setButtonsActive(true);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
        extractDeathBackground(gfx, this.width, this.height);
    }

    private static void extractDeathBackground(GuiGraphicsExtractor gfx, int w, int h) {
        gfx.fillGradient(0, 0, w, h, 1615855616, -1602211792);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(gfx, mouseX, mouseY, partialTick);

        ActiveTextCollector collector = gfx.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        this.visitText(collector);
    }

    private void visitText(ActiveTextCollector collector) {
        ActiveTextCollector.Parameters base = collector.defaultParameters();
        int cx = this.width / 2;

        // Large "You Died!" title (Bedrock style)
        collector.defaultParameters(base.withScale(TITLE_SCALE));
        collector.accept(TextAlignment.CENTER, cx / 2, 30, Component.literal("You Died!"));

        // Reset scale for subtitle
        collector.defaultParameters(base);

        // Cause of death subtitle directly under title
        if (this.causeOfDeath != null) {
            collector.accept(TextAlignment.CENTER, cx, 85, this.causeOfDeath);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
