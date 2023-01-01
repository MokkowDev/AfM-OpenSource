package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer

class BlockDropAbuse : SpeedMode("BlockDropAbuse") {

    var timer = MSTimer()

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        super.onDisable()
    }

    override fun onMotion() {}
    override fun onUpdate() {
        if(MovementUtils.isMoving) {
            if(!this.timer.hasTimePassed(400L)) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, true))
                mc.timer.timerSpeed = 2.696969420f
            } else {
                if(!this.timer.hasTimePassed(400L)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.69420, mc.thePlayer!!.posZ, true))
                    mc.timer.timerSpeed = 0.20000000298023224f
                }
            }
        }
    }

    override fun onMove(event: MoveEvent) {}
}