/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "DiscordRPC", description = "Show/Disable your client presence and custom here", category = ModuleCategory.RENDER)
class DiscordRPC : Module() {

    var richpresence = LiquidBounce.clientRichPresence
    override fun onEnable() {
        richpresence.setup()
        richpresence.update()
    }
    override fun onDisable() {
        richpresence.shutdown()
    }
}