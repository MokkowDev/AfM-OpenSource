/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.misc.HttpUtils.download
import net.ccbluex.liquidbounce.utils.misc.MiscUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiOpenClient : WrappedGuiScreen() {

    override fun initGui() {
        val j = representedScreen.height / 4 + 48

        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 + 5, j + 24 * 2, 98, 20, "Login"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        Fonts.rise.drawCenteredString("Press \"Login\" to open main menu screen.", representedScreen.width / 2.0f, representedScreen.height / 8.0f + 80 + Fonts.font35.fontHeight, 0xffffff)

        super.drawScreen(mouseX, mouseY, partialTicks)

        // Title
        Fonts.rise.drawCenteredString("Welcome to client!", representedScreen.width / 4.0f, representedScreen.height / 16.0f + 20, RenderUtils.testRainbow(1,1F,0.9F,5.0).rgb, true)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            1 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiMainMenu()))
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode)
            return

        super.keyTyped(typedChar, keyCode)
    }
}
