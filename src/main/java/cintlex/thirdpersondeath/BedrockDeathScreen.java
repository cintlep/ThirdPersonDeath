package cintlex.thirdpersondeath;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class BedrockDeathScreen extends Screen {
    private static final int TITLE_SCALE = 2;

    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;

    // Manual darker "ore-ui" style buttons (positions computed in init)
    private int btn1X, btn1Y, btn1W, btn1H;
    private int btn2X, btn2Y, btn2W, btn2H;
    private boolean buttonsActive = false;
    private long uiFadeStart = 0;
    private boolean buttonsAdded = false;

    private static final long FADE_START_DELAY_MS = 1000; // reduced delay before UI fade in starts

    public BedrockDeathScreen(Component causeOfDeath, boolean hardcore) {
        super(Component.translatable(hardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = causeOfDeath;
        this.hardcore = hardcore;
    }

    @Override
    protected void init() {
        super.init();
        this.delayTicker = 0;
        this.buttonsActive = false;

        int bw = 200;
        int bh = 20;
        int cx = this.width / 2;
        int baseY = this.height - 85; // slightly above the hunger bar

        this.btn1X = cx - bw / 2; this.btn1Y = baseY; this.btn1W = bw; this.btn1H = bh;
        this.btn2X = cx - bw / 2; this.btn2Y = baseY + 25; this.btn2W = bw; this.btn2H = bh;
    }

    private void doRespawn() {
        if (this.minecraft != null && this.minecraft.getConnection() != null) {
            // Proper way: send the respawn command packet (like vanilla DeathScreen)
            this.minecraft.getConnection().send(
                new net.minecraft.network.protocol.game.ServerboundClientCommandPacket(
                    net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action.PERFORM_RESPAWN
                )
            );
        } else if (this.minecraft != null && this.minecraft.player != null) {
            // Fallback
            this.minecraft.player.respawn();
        }
    }

    private void doExitToTitle() {
        if (this.minecraft == null) return;
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        }
        this.minecraft.disconnectWithSavingScreen();
        this.minecraft.setScreen(new TitleScreen());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.uiFadeStart == 0) {
            this.uiFadeStart = System.currentTimeMillis();
        }
        // Add vanilla buttons after delay (for text and clicks)
        if (this.uiFadeStart > 0 && !this.buttonsAdded) {
            long elapsed = System.currentTimeMillis() - this.uiFadeStart;
            if (elapsed > FADE_START_DELAY_MS) {
                Component respawnButtonText = this.hardcore 
                    ? Component.translatable("deathScreen.spectate")
                    : Component.literal("Respawn");
                this.addRenderableWidget(Button.builder(respawnButtonText, b -> doRespawn()).bounds(this.btn1X, this.btn1Y, this.btn1W, this.btn1H).build());
                this.addRenderableWidget(Button.builder(Component.literal("Game menu"), b -> doExitToTitle()).bounds(this.btn2X, this.btn2Y, this.btn2W, this.btn2H).build());
                this.buttonsAdded = true;
                this.buttonsActive = true; // enable for any manual if needed
            }
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    private static boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
        extractDeathBackground(gfx, this.width, this.height);
        // Vanilla buttons - no custom drawing (texture packs can override colors)
    }

    private static void extractDeathBackground(GuiGraphicsExtractor gfx, int w, int h) {
        // Darker base with red wash (Bedrock-like red fading from edges)
        gfx.fillGradient(0, 0, w, h, 0xCC0A0A0A, 0xDD1A0505);
        // Red edge vignette from sides
        int red = 0x44220000;
        gfx.fillGradient(0, 0, w, 70, red, 0);
        gfx.fillGradient(0, h - 70, w, h, 0, red);
        gfx.fillGradient(0, 0, 55, h, red, 0);
        gfx.fillGradient(w - 55, 0, w, h, 0, red);
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

        // Show title and subtitle after the delay (classic fade-in by delayed appearance, no scale animation)
        if (this.uiFadeStart > 0) {
            long elapsed = System.currentTimeMillis() - this.uiFadeStart;
            if (elapsed > FADE_START_DELAY_MS) {
                // Title full scale, centered
                Component titleComp = this.hardcore
                    ? Component.translatable("deathScreen.title.hardcore")
                        .setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("thirdpersondeath", "death_title"))))
                    : Component.literal("YOU DIED!")
                        .setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("thirdpersondeath", "death_title"))));
                collector.defaultParameters(base.withScale(TITLE_SCALE));
                collector.accept(TextAlignment.CENTER, cx / 2, 30, titleComp);

                // Reset parameters after scaled title
                collector.defaultParameters(base);

                // Subtitle together with title, positioned properly below (not overlaying)
                if (this.causeOfDeath != null) {
                    collector.accept(TextAlignment.CENTER, cx, 82, this.causeOfDeath);
                }
            }
        }

        // Button labels are provided by the vanilla Button widgets (added after delay)
        // No collector for button text
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
