/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiScreen
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import org.lwjgl.input.Keyboard
import java.awt.Color
import kotlin.concurrent.thread

class GuiModsMenu(private val prevGui: IGuiScreen) : WrappedGuiScreen() {

    override fun initGui() {
        representedScreen.buttonList.add(classProvider.createGuiButton(0, representedScreen.width / 2 - 100, representedScreen.height / 4 + 48 + 25, "Forge Mods"))
        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 - 100, representedScreen.height / 4 + 48 + 50, "Scripts"))
        representedScreen.buttonList.add(classProvider.createGuiButton(3, representedScreen.width / 2 - 100, representedScreen.height / 4 + 48 + 75, "Back"))
    }

    override fun actionPerformed(button: IGuiButton) {
        when (val id = button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiModList(this.representedScreen))
            1 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiScripts(this.representedScreen)))
            3 -> mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        Fonts.risemainmenu.drawCenteredString("Client", representedScreen.width / 2F, representedScreen.height / 8F + 5F, Color(0,255,0, 80).rgb, false)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}