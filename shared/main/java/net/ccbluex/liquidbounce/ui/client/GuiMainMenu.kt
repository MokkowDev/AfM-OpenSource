/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils

class GuiMainMenu : WrappedGuiScreen() {

    override fun initGui() {
        val defaultHeight = representedScreen.height / 4 + 48

        // Mossware
        representedScreen.buttonList.add(classProvider.createGuiButton(100, representedScreen.width / 2 - 50, defaultHeight + 24*2, 100, 20, "Alts Manager"))
        representedScreen.buttonList.add(classProvider.createGuiButton(103, representedScreen.width / 2 - 50, defaultHeight + 24*3, 100, 20, "Client"))
        representedScreen.buttonList.add(classProvider.createGuiButton(102, representedScreen.width / 2 - 50, defaultHeight + 24*4, 100, 20, "Background"))
        // Minecraft
        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 - 50, defaultHeight, 100, 20, "Single"))
        representedScreen.buttonList.add(classProvider.createGuiButton(2, representedScreen.width / 2 - 50, defaultHeight + 24, 100, 20, "Servers"))
        // Minecraft v2
        representedScreen.buttonList.add(classProvider.createGuiButton(0, representedScreen.width / 2 - 50, defaultHeight + 24*5, 100, 20, "Game Settings"))
        representedScreen.buttonList.add(classProvider.createGuiButton(4, representedScreen.width / 2 - 50, defaultHeight + 24*6, 100, 20, "Quit Game"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        Fonts.risemainmenu.drawCenteredString("AfM", representedScreen.width / 2F, representedScreen.height / 8F, RenderUtils.testRainbow(1,1F,0.9F,5.0).rgb, true)

        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            1 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            4 -> {
                LiquidBounce.fileManager.saveAllConfigs()
                mc.shutdown()
            }
            100 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            102 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiBackground(this.representedScreen)))
            103 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiModsMenu(this.representedScreen)))
        }
    }
}