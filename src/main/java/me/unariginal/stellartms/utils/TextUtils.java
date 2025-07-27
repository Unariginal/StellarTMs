package me.unariginal.stellartms.utils;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.stellartms.StellarTMs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TextUtils {
    public static Text deserialize(String text) {
        return StellarTMs.INSTANCE.audiences.toNative(MiniMessage.miniMessage().deserialize("<!i>" + text));
    }

    public static String parse(String text) {
        return text.replaceAll("%prefix%", StellarTMs.INSTANCE.config.prefix);
    }

    public static String parse(String text, MoveTemplate move) {
        text = parse(text);
        return text
                .replaceAll("%move%", move != null ? move.getDisplayName().getString() : "<gray>Blank")
                .replaceAll("%move_color%", getColorByType(move));
    }

    public static String parse(String text, Pokemon pokemon) {
        text = parse(text);
        return text.replaceAll("%pokemon%", pokemon.getDisplayName().getString());
    }

    public static String parse(String text, ServerPlayerEntity player) {
        text = parse(text);
        return text.replaceAll("%player%", player.getNameForScoreboard());
    }

    public static String getColorByType(MoveTemplate moveTemplate) {
        if (moveTemplate != null) {
            String type = Integer.toHexString(moveTemplate.getElementalType().getHue());
            return "<#" + type.toLowerCase() + ">";
        }
        return "";
    }
}
