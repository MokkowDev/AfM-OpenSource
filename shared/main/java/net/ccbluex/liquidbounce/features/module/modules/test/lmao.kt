package net.ccbluex.liquidbounce.features.module.modules.test

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "lmao", description = "wtf is this", category = ModuleCategory.WORLD)
class lmao : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer!!.posY <= mc.thePlayer!!.posY) {
            mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketPlayer(true))
            mc.thePlayer!!.motionY = 0.42
        }
        @EventTarget
        fun onMotion(event: MotionEvent) {
            mc.thePlayer!!.cameraYaw = 0.13F
        }
    }
}

