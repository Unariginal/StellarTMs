package me.unariginal.stellartms.utils;

import me.unariginal.stellartms.StellarTMs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.text.Text;

public class TextUtils {
    public static Text deserialize(String text) {
        return StellarTMs.INSTANCE.audiences.toNative(MiniMessage.miniMessage().deserialize("<!i>" + text));
    }
}
