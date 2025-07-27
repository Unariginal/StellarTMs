package me.unariginal.stellartms.config;

import com.google.gson.*;
import me.unariginal.stellartms.StellarTMs;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public boolean ignore_learnset = false;
    public String tm_base_item = "minecraft:brick";
    public String tm_item_name = "%move_color%TM: %move%";
    public List<String> tm_item_lore = new ArrayList<>();
    public String blank_tm_item_name = "<gray>Blank TM";
    public List<String> blank_tm_item_lore = new ArrayList<>();
    public String tr_base_item = "minecraft:nether_brick";
    public String tr_item_name = "%move_color%TR: %move%";
    public List<String> tr_item_lore = new ArrayList<>();
    public String blank_tr_item_name = "<gray>Blank TR";
    public List<String> blank_tr_item_lore = new ArrayList<>();
    public String gui_title = "<gold>Pick A Move!";
    public String move_learned_overlay = "<green>%pokemon% has learned %move_color%%move%<green>!";
    public String unable_to_learn_overlay = "<red>%pokemon% cannot learn %move_color%%move%<red>!";
    public String already_learned_overlay = "<gray>%pokemon% already knows %move_color%%move%<gray>!";
    public String prefix = "<dark_gray>[<light_purple>StellarTMs<dark_gray>]";
    public String reload_message = "%prefix% <green>Reloaded!";
    public String tm_give_message = "%prefix% <green>Gave %move_color%TM: %move% <green>to %player%!";
    public String tr_give_message = "%prefix% <green>Gave %move_color%TR: %move% <green>to %player%!";

    public Config() {
        try {
            loadConfig();
        } catch (IOException e) {
            StellarTMs.LOGGER.error("Could not load config file!", e);
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("StellarTMs").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("StellarTMs/config.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        }

        if (root.has("debug")) {
            StellarTMs.DEBUG = root.get("debug").getAsBoolean();
        }
        newRoot.addProperty("debug", StellarTMs.DEBUG);

        if (root.has("ignore_learnset")) {
            ignore_learnset = root.get("ignore_learnset").getAsBoolean();
        }
        newRoot.addProperty("ignore_learnset", ignore_learnset);

        if (root.has("tm_base_item")) {
            tm_base_item = root.get("tm_base_item").getAsString();
        }
        newRoot.addProperty("tm_base_item", tm_base_item);

        if (root.has("tm_item_name")) {
            tm_item_name = root.get("tm_item_name").getAsString();
        }
        newRoot.addProperty("tm_item_name", tm_item_name);

        if (root.has("tm_item_lore")) {
            tm_item_lore.clear();
            JsonArray lore = root.get("tm_item_lore").getAsJsonArray();
            for (JsonElement element : lore) {
                tm_item_lore.add(element.getAsString());
            }
        }
        JsonArray lore = new JsonArray();
        for (String line : tm_item_lore) {
            lore.add(line);
        }
        newRoot.add("tm_item_lore", lore);

        if (root.has("blank_tm_item_name")) {
            blank_tm_item_name = root.get("blank_tm_item_name").getAsString();
        }
        newRoot.addProperty("blank_tm_item_name", blank_tm_item_name);

        if (root.has("blank_tm_item_lore")) {
            blank_tm_item_lore.clear();
            lore = root.get("blank_tm_item_lore").getAsJsonArray();
            for (JsonElement element : lore) {
                blank_tm_item_lore.add(element.getAsString());
            }
        }
        lore = new JsonArray();
        for (String line : blank_tm_item_lore) {
            lore.add(line);
        }
        newRoot.add("blank_tm_item_lore", lore);

        if (root.has("tr_base_item")) {
            tr_base_item = root.get("tr_base_item").getAsString();
        }
        newRoot.addProperty("tr_base_item", tr_base_item);

        if (root.has("tr_item_name")) {
            tr_item_name = root.get("tr_item_name").getAsString();
        }
        newRoot.addProperty("tr_item_name", tr_item_name);

        if (root.has("tr_item_lore")) {
            tr_item_lore.clear();
            lore = root.get("tr_item_lore").getAsJsonArray();
            for (JsonElement element : lore) {
                tr_item_lore.add(element.getAsString());
            }
        }
        lore = new JsonArray();
        for (String line : tr_item_lore) {
            lore.add(line);
        }
        newRoot.add("tr_item_lore", lore);

        if (root.has("blank_tr_item_name")) {
            blank_tr_item_name = root.get("blank_tr_item_name").getAsString();
        }
        newRoot.addProperty("blank_tr_item_name", blank_tr_item_name);

        if (root.has("blank_tr_item_lore")) {
            blank_tr_item_lore.clear();
            lore = root.get("blank_tr_item_lore").getAsJsonArray();
            for (JsonElement element : lore) {
                blank_tr_item_lore.add(element.getAsString());
            }
        }
        lore = new JsonArray();
        for (String line : blank_tr_item_lore) {
            lore.add(line);
        }
        newRoot.add("blank_tr_item_lore", lore);

        if (root.has("gui_title")) {
            gui_title = root.get("gui_title").getAsString();
        }
        newRoot.addProperty("gui_title", gui_title);

        if (root.has("move_learned_overlay")) {
            move_learned_overlay = root.get("move_learned_overlay").getAsString();
        }
        newRoot.addProperty("move_learned_overlay", move_learned_overlay);

        if (root.has("unable_to_learn_overlay")) {
            unable_to_learn_overlay = root.get("unable_to_learn_overlay").getAsString();
        }
        newRoot.addProperty("unable_to_learn_overlay", unable_to_learn_overlay);

        if (root.has("already_learned_overlay")) {
            already_learned_overlay = root.get("already_learned_overlay").getAsString();
        }
        newRoot.addProperty("already_learned_overlay", already_learned_overlay);

        if (root.has("prefix")) {
            prefix = root.get("prefix").getAsString();
        }
        newRoot.addProperty("prefix", prefix);

        if (root.has("reload_message")) {
            reload_message = root.get("reload_message").getAsString();
        }
        newRoot.addProperty("reload_message", reload_message);

        if (root.has("tm_give_message")) {
            tm_give_message = root.get("tm_give_message").getAsString();
        }
        newRoot.addProperty("tm_give_message", tm_give_message);

        if (root.has("tr_give_message")) {
            tr_give_message = root.get("tr_give_message").getAsString();
        }
        newRoot.addProperty("tr_give_message", tr_give_message);

        configFile.delete();
        configFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Writer writer = new FileWriter(configFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }
}
