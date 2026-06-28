package cintlex.thirdpersondeath;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

public class BedrockDeathScreen extends Screen {
    private static final int TITLE_SCALE = 2;

    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;

    // Manual darker "ore-ui" style buttons (positions computed in init)
    private int btn1X, btn1Y, btn1W, btn1H;
    private int btn2X, btn2Y, btn2W, btn2H;
    private boolean buttonsActive = false;

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
        int baseY = this.height / 4;

        this.btn1X = cx - bw / 2; this.btn1Y = baseY + 95; this.btn1W = bw; this.btn1H = bh;
        this.btn2X = cx - bw / 2; this.btn2Y = baseY + 120; this.btn2W = bw; this.btn2H = bh;
    }

    private void doRespawn() {
        if (this.minecraft != null && this.minecraft.player != null) {
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
        this.delayTicker++;
        if (this.delayTicker == 20) {
            this.buttonsActive = true;
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        if (!buttonsActive) return super.mouseClicked(event, doubleClick);
        double mx = event.x();
        double my = event.y();
        if (isInside(mx, my, btn1X, btn1Y, btn1W, btn1H)) {
            doRespawn();
            return true;
        }
        if (isInside(mx, my, btn2X, btn2Y, btn2W, btn2H)) {
            doExitToTitle();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private static boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
        extractDeathBackground(gfx, this.width, this.height);
        drawDarkButtons(gfx);
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

    /** Draw the darker ore-ui style button backgrounds (called from background extraction). */
    private void drawDarkButtons(GuiGraphicsExtractor gfx) {
        int dark = 0xFF3A3A3A;      // dark gray like Bedrock ore-ui
        int border = 0xFF1F1F1F;
        // Button 1 (Respawn)
        gfx.fill(btn1X - 1, btn1Y - 1, btn1X + btn1W + 1, btn1Y + btn1H + 1, border);
        gfx.fill(btn1X, btn1Y, btn1X + btn1W, btn1Y + btn1H, dark);
        // Button 2 (Game menu)
        gfx.fill(btn2X - 1, btn2Y - 1, btn2X + btn2W + 1, btn2Y + btn2H + 1, border);
        gfx.fill(btn2X, btn2Y, btn2X + btn2W, btn2Y + btn2H, dark);
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

        // "YOU DIED!" in a distinct style (larger scale) so it stands out from the subtitle like Bedrock
        net.minecraft.network.chat.MutableComponent title = Component.literal("YOU DIED!");
        collector.defaultParameters(base.withScale(TITLE_SCALE));
        collector.accept(TextAlignment.CENTER, cx / 2, 28, title);

        // Reset scale for subtitle
        collector.defaultParameters(base);

        // Cause of death as subtitle (positioned like Bedrock)
        if (this.causeOfDeath != null) {
            collector.accept(TextAlignment.CENTER, cx, 82, this.causeOfDeath);
        }

        // Button labels drawn on top of the dark rects we drew in background (ore-ui dark style)
        if (buttonsActive || delayTicker > 5) {
            // center text vertically inside 20px tall bars
            collector.accept(TextAlignment.CENTER, cx, btn1Y + 5, Component.literal("Respawn"));
            collector.accept(TextAlignment.CENTER, cx, btn2Y + 5, Component.literal("Game menu"));
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
