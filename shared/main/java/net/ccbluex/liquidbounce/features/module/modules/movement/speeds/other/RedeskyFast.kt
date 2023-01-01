package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class RedeskyFast : SpeedMode("RedeskyFast") {
    override fun onMotion() {
        if (MovementUtils.isMoving) {
            if (mc.thePlayer!!.onGround) mc.thePlayer!!.jump() else MovementUtils.strafe(0.2550f)
        } else {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        }
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {}
}