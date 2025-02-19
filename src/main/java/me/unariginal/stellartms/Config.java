package me.unariginal.stellartms;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class Config {
    public Settings settings;

    public Config() {
        try {
            checkFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadConfig();
    }

    private void checkFiles() throws IOException {
        Path rootFolder = FabricLoader.getInstance().getConfigDir().resolve("StellarTMs");
        File rootFile = rootFolder.toFile();
        if (!rootFile.exists()) {
            try {
                rootFile.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("StellarTMs/config.json").toFile();
        if (!configFile.exists()) {
            configFile.createNewFile();

            InputStream in = StellarTMs.class.getResourceAsStream("/stellartms_config/config.json");
            OutputStream out = new FileOutputStream(configFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    private void loadConfig() {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("StellarTMs/config.json").toFile();

        JsonElement root = null;
        try {
            root = JsonParser.parseReader(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert root != null;
        JsonObject rootObject = root.getAsJsonObject();
        JsonObject tmsObject = rootObject.getAsJsonObject("tms");
        JsonObject trsObject = rootObject.getAsJsonObject("trs");
        String gui_title = rootObject.get("gui_title").getAsString();

        String tm_item_path = tmsObject.get("item").getAsString();
        int tm_blank = tmsObject.get("blank_model_data").getAsInt();
        int tm_normal = tmsObject.get("normal_model_data").getAsInt();
        int tm_fire = tmsObject.get("fire_model_data").getAsInt();
        int tm_water = tmsObject.get("water_model_data").getAsInt();
        int tm_electric = tmsObject.get("electric_model_data").getAsInt();
        int tm_grass = tmsObject.get("grass_model_data").getAsInt();
        int tm_ice = tmsObject.get("ice_model_data").getAsInt();
        int tm_fighting = tmsObject.get("fighting_model_data").getAsInt();
        int tm_poison = tmsObject.get("poison_model_data").getAsInt();
        int tm_ground = tmsObject.get("ground_model_data").getAsInt();
        int tm_flying = tmsObject.get("flying_model_data").getAsInt();
        int tm_psychic = tmsObject.get("psychic_model_data").getAsInt();
        int tm_bug = tmsObject.get("bug_model_data").getAsInt();
        int tm_rock = tmsObject.get("rock_model_data").getAsInt();
        int tm_ghost = tmsObject.get("ghost_model_data").getAsInt();
        int tm_dragon = tmsObject.get("dragon_model_data").getAsInt();
        int tm_dark = tmsObject.get("dark_model_data").getAsInt();
        int tm_steel = tmsObject.get("steel_model_data").getAsInt();
        int tm_fairy = tmsObject.get("fairy_model_data").getAsInt();

        String tr_item_path = trsObject.get("item").getAsString();
        int tr_blank = trsObject.get("blank_model_data").getAsInt();
        int tr_normal = trsObject.get("normal_model_data").getAsInt();
        int tr_fire = trsObject.get("fire_model_data").getAsInt();
        int tr_water = trsObject.get("water_model_data").getAsInt();
        int tr_electric = trsObject.get("electric_model_data").getAsInt();
        int tr_grass = trsObject.get("grass_model_data").getAsInt();
        int tr_ice = trsObject.get("ice_model_data").getAsInt();
        int tr_fighting = trsObject.get("fighting_model_data").getAsInt();
        int tr_poison = trsObject.get("poison_model_data").getAsInt();
        int tr_ground = trsObject.get("ground_model_data").getAsInt();
        int tr_flying = trsObject.get("flying_model_data").getAsInt();
        int tr_psychic = trsObject.get("psychic_model_data").getAsInt();
        int tr_bug = trsObject.get("bug_model_data").getAsInt();
        int tr_rock = trsObject.get("rock_model_data").getAsInt();
        int tr_ghost = trsObject.get("ghost_model_data").getAsInt();
        int tr_dragon = trsObject.get("dragon_model_data").getAsInt();
        int tr_dark = trsObject.get("dark_model_data").getAsInt();
        int tr_steel = trsObject.get("steel_model_data").getAsInt();
        int tr_fairy = trsObject.get("fairy_model_data").getAsInt();

        settings = new Settings(tm_item_path, tm_blank, tm_normal, tm_fire, tm_water, tm_electric, tm_grass, tm_ice, tm_fighting, tm_poison, tm_ground, tm_flying, tm_psychic, tm_bug, tm_rock, tm_ghost, tm_dragon, tm_dark, tm_steel, tm_fairy, tr_item_path, tr_blank, tr_normal, tr_fire, tr_water, tr_electric, tr_grass, tr_ice, tr_fighting, tr_poison, tr_ground, tr_flying, tr_psychic, tr_bug, tr_rock, tr_ghost, tr_dragon, tr_dark, tr_steel, tr_fairy, gui_title);
    }
}
