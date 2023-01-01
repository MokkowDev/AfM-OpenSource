/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "WTap", description = "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT)
class WTap : Module() {

    private val legitValue = BoolValue("Legit", true)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            if (event.targetEntity!!.asEntityLivingBase().hurtTime > 10)
                return

            val player = mc.thePlayer ?: return

            if(!legitValue.get()) {
                if (player.sprinting)
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketEntityAction(
                            player,
                            ICPacketEntityAction.WAction.STOP_SPRINTING
                        )
                    )

                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketEntityAction(
                        player,
                        ICPacketEntityAction.WAction.START_SPRINTING
                    )
                )
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketEntityAction(
                        player,
                        ICPacketEntityAction.WAction.STOP_SPRINTING
                    )
                )
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketEntityAction(
                        player,
                        ICPacketEntityAction.WAction.START_SPRINTING
                    )
                )
                player.sprinting = true
                player.serverSprintState = true
            }else{
                player.sprinting = !(player.sprinting && mc.gameSettings.keyBindAttack.isKeyDown)
            }
        }
    }

}