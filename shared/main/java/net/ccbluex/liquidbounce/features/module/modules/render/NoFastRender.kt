package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils

@ModuleInfo(name = "NoFastRender", description = "Automatically turn off fastrender to fix most texture bug (if have optifine mod).", category = ModuleCategory.WORLD)
class NoFastRender : Module() {

    override fun onEnable() {
        LiquidBounce.hud.addNotification(Notification("Turning off fastrender! (need optifine mod to turn off)"))
        ClientUtils.getLogger().info("Mossware >> Send notification!, turning off fastrender")
        ClientUtils.disableFastRender()
    }
    override fun onDisable() {

    }
}
