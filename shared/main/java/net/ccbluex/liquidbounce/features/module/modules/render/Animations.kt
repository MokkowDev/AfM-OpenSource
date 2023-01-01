package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;


@ModuleInfo(name = "Animations", description = "Change the block animation", category = ModuleCategory.RENDER)
class Animations : Module() {
    val presetValue = ListValue(
        "Animation", arrayOf(
            "Default", "Akrien", "Avatar", "ETB", "Exhibition", "Push", "Reverse",
            "Shield", "SigmaNew", "SigmaOld", "Slide", "VisionFX",
            "Swong", "Jello",  "Rotate", "None"
        ),
        "Slide"
    )

    var translateX = FloatValue("TranslateX", 0.0f, 0.0f, 1.5f)
    var translateY = FloatValue("TranslateY", 0.0f, 0.0f, 0.5f)
    var translateZ = FloatValue("TranslateZ", 0.0f, 0.0f, -2.0f)
    val itemPosX = FloatValue("ItemPosX", 0.56F, -1.0F, 1.0F)
    val itemPosY = FloatValue("ItemPosY", -0.52F, -1.0F, 1.0F)
    val itemPosZ = FloatValue("ItemPosZ", -0.71999997F, -1.0F, 1.0F)
    var itemScale = FloatValue("ItemScale", 0.4f, 0.0f, 2.0f)
    var swingAnim = BoolValue("SwingAnimation",false)
    var SlowHand = IntegerValue("SlowHand", 4, 0, 20)

    override val tag: String
        get() = presetValue.get()
}