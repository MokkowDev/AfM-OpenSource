package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.timer.MSTimer

class BlockDrop : SpeedMode("BlockDrop") {

    var timer = MSTimer()
    private var counter = 0


    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        super.onDisable()
    }

    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.onGround) {
            mc.thePlayer!!.jump()
            counter = 4
        } else {
            if (this.counter < 6) {
                mc.timer.timerSpeed = 0.2f
            } else {
                mc.timer.timerSpeed = 1.3f
            }
            ++counter
        }
        counter = 0
    }

    override fun onMove(event: MoveEvent) {}
}