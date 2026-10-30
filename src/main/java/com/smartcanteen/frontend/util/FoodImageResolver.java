package com.smartcanteen.frontend.util;

import com.smartcanteen.model.MenuItem;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FoodImageResolver {

    private static final Map<String, Image> IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> EXACT_NAME_MAP = new HashMap<>();

    static {
        // North Indian Meals
        EXACT_NAME_MAP.put("chole bhature", "chole_bhature.jpg");
        EXACT_NAME_MAP.put("rajma chawal", "rajma_chawal.jpg");
        EXACT_NAME_MAP.put("chole chawal", "chole_chawal.jpg");
        EXACT_NAME_MAP.put("kadhi chawal", "kadhi_chawal.jpg");
        EXACT_NAME_MAP.put("dal tadka with rice", "dal_tadka.jpg");
        EXACT_NAME_MAP.put("dal makhani with rice", "dal_makhani.jpg");
        EXACT_NAME_MAP.put("paneer butter masala with roti", "paneer_butter_masala.jpg");
        EXACT_NAME_MAP.put("shahi paneer with roti", "shahi_paneer.jpg");
        EXACT_NAME_MAP.put("kadhai paneer with roti", "kadhai_paneer.jpg");
        EXACT_NAME_MAP.put("mix veg with roti", "mix_veg.jpg");
        EXACT_NAME_MAP.put("aloo paratha", "aloo_paratha.jpg");
        EXACT_NAME_MAP.put("paneer paratha", "paneer_paratha.jpg");
        EXACT_NAME_MAP.put("gobi paratha", "gobi_paratha.jpg");
        EXACT_NAME_MAP.put("stuffed paratha combo", "stuffed_paratha.jpg");
        EXACT_NAME_MAP.put("jeera rice", "jeera_rice.jpg");
        EXACT_NAME_MAP.put("veg pulao", "veg_pulao.jpg");
        EXACT_NAME_MAP.put("veg thali", "veg_thali.jpg");
        EXACT_NAME_MAP.put("special north indian thali", "special_thali.jpg");

        // Chaat
        EXACT_NAME_MAP.put("pani puri", "pani_puri.jpg");
        EXACT_NAME_MAP.put("aloo tikki chaat", "aloo_tikki.jpg");
        EXACT_NAME_MAP.put("papdi chaat", "papdi_chaat.jpg");
        EXACT_NAME_MAP.put("dahi puri", "dahi_puri.jpg");
        EXACT_NAME_MAP.put("bhel puri", "bhel_puri.jpg");
        EXACT_NAME_MAP.put("sev puri", "sev_puri.jpg");
        EXACT_NAME_MAP.put("raj kachori", "raj_kachori.jpg");
        EXACT_NAME_MAP.put("dahi bhalla", "dahi_bhalla.jpg");
        EXACT_NAME_MAP.put("samosa chaat", "samosa_chaat.jpg");
        EXACT_NAME_MAP.put("chole tikki chaat", "chole_tikki.jpg");

        // Healthy Food
        EXACT_NAME_MAP.put("sprouts chaat", "sprouts_chaat.jpg");
        EXACT_NAME_MAP.put("fresh fruit bowl", "fruit_bowl.jpg");
        EXACT_NAME_MAP.put("mixed fruit chaat", "fruit_chaat.jpg");
        EXACT_NAME_MAP.put("vegetable salad", "vegetable_salad.jpg");
        EXACT_NAME_MAP.put("paneer salad", "paneer_salad.jpg");
        EXACT_NAME_MAP.put("grilled vegetable sandwich", "grilled_veg_sandwich.jpg");
        EXACT_NAME_MAP.put("grilled paneer sandwich", "grilled_paneer_sandwich.jpg");
        EXACT_NAME_MAP.put("vegetable wrap", "veg_wrap.jpg");
        EXACT_NAME_MAP.put("paneer wrap", "paneer_wrap.jpg");
        EXACT_NAME_MAP.put("oats bowl", "oats_bowl.jpg");
        EXACT_NAME_MAP.put("yogurt and fruit bowl", "yogurt_fruit_bowl.jpg");
        EXACT_NAME_MAP.put("corn and vegetable bowl", "corn_bowl.jpg");

        // Breakfast
        EXACT_NAME_MAP.put("poha", "poha.jpg");
        EXACT_NAME_MAP.put("upma", "upma.jpg");
        EXACT_NAME_MAP.put("aloo paratha (breakfast)", "aloo_paratha.jpg");
        EXACT_NAME_MAP.put("paneer paratha (breakfast)", "paneer_paratha.jpg");
        EXACT_NAME_MAP.put("vegetable sandwich", "veg_sandwich.jpg");
        EXACT_NAME_MAP.put("idli sambar", "idli_sambar.jpg");
        EXACT_NAME_MAP.put("masala dosa", "masala_dosa.jpg");
        EXACT_NAME_MAP.put("besan chilla", "besan_chilla.jpg");
        EXACT_NAME_MAP.put("moong dal chilla", "moong_dal_chilla.jpg");

        // Snacks
        EXACT_NAME_MAP.put("samosa", "samosa.jpg");
        EXACT_NAME_MAP.put("paneer samosa", "paneer_samosa.jpg");
        EXACT_NAME_MAP.put("veg cutlet", "veg_cutlet.jpg");
        EXACT_NAME_MAP.put("bread pakora", "bread_pakora.jpg");
        EXACT_NAME_MAP.put("kachori", "kachori.jpg");
        EXACT_NAME_MAP.put("dhokla", "dhokla.jpg");
        EXACT_NAME_MAP.put("paneer tikka", "paneer_tikka.jpg");
        EXACT_NAME_MAP.put("french fries", "french_fries.jpg");
        EXACT_NAME_MAP.put("masala fries", "masala_fries.jpg");
        EXACT_NAME_MAP.put("veg spring roll", "spring_roll.jpg");

        // Beverages
        EXACT_NAME_MAP.put("masala chaas", "masala_chaas.jpg");
        EXACT_NAME_MAP.put("sweet lassi", "sweet_lassi.jpg");
        EXACT_NAME_MAP.put("mango lassi", "mango_lassi.jpg");
        EXACT_NAME_MAP.put("fresh lemon water", "lemon_water.jpg");
        EXACT_NAME_MAP.put("mint lemon water", "mint_lemon_water.jpg");
        EXACT_NAME_MAP.put("fresh orange juice", "orange_juice.jpg");
        EXACT_NAME_MAP.put("watermelon juice", "watermelon_juice.jpg");
        EXACT_NAME_MAP.put("coconut water", "coconut_water.jpg");
        EXACT_NAME_MAP.put("cold coffee", "cold_coffee.jpg");
        EXACT_NAME_MAP.put("masala tea", "masala_tea.jpg");
        EXACT_NAME_MAP.put("green tea", "green_tea.jpg");

        // Healthy Drinks
        EXACT_NAME_MAP.put("detox lemon mint", "detox_water.jpg");
        EXACT_NAME_MAP.put("fruit smoothie", "fruit_smoothie.jpg");
        EXACT_NAME_MAP.put("banana smoothie", "banana_smoothie.jpg");
        EXACT_NAME_MAP.put("mango smoothie", "mango_smoothie.jpg");
        EXACT_NAME_MAP.put("buttermilk", "buttermilk.jpg");
        EXACT_NAME_MAP.put("fresh coconut water", "coconut_water.jpg");

        // Combos
        EXACT_NAME_MAP.put("chole bhature + chaas", "chole_bhature_combo.jpg");
        EXACT_NAME_MAP.put("rajma chawal + salad", "rajma_combo.jpg");
        EXACT_NAME_MAP.put("veg thali + chaas", "thali_combo.jpg");
        EXACT_NAME_MAP.put("aloo paratha + curd", "paratha_curd.jpg");
        EXACT_NAME_MAP.put("paneer paratha + lassi", "paneer_paratha_lassi.jpg");
        EXACT_NAME_MAP.put("samosa + masala tea", "samosa_chai.jpg");
        EXACT_NAME_MAP.put("aloo tikki + lemon water", "tikki_lemonade.jpg");
        EXACT_NAME_MAP.put("grilled sandwich + fresh juice", "sandwich_juice.jpg");
        EXACT_NAME_MAP.put("healthy salad + lemon water", "salad_water.jpg");
        EXACT_NAME_MAP.put("paneer wrap + smoothie", "wrap_smoothie.jpg");
    }

    private FoodImageResolver() {
        // Utility
    }

    /**
     * Resolves and returns a high-quality cached Image for the given MenuItem.
     * Guaranteed to never return null.
     */
    public static Image getImageFor(MenuItem item) {
        if (item == null) {
            return loadResourceImage("default_food.jpg");
        }

        String itemName = (item.getItemName() != null) ? item.getItemName().trim().toLowerCase() : "";
        String category = (item.getCategory() != null) ? item.getCategory().trim().toLowerCase() : "";

        // 1. Direct match by exact name
        if (EXACT_NAME_MAP.containsKey(itemName)) {
            String filename = EXACT_NAME_MAP.get(itemName);
            Image img = loadResourceImage(filename);
            if (img != null) return img;
        }

        // 2. Keyword heuristic matching
        String filename = matchByKeyword(itemName, category);
        if (filename != null) {
            Image img = loadResourceImage(filename);
            if (img != null) return img;
        }

        // 3. Category fallback
        String categoryFallback = matchByCategory(category);
        if (categoryFallback != null) {
            Image img = loadResourceImage(categoryFallback);
            if (img != null) return img;
        }

        // 4. Default global fallback
        return loadResourceImage("default_food.jpg");
    }

    private static String matchByKeyword(String name, String category) {
        if (name.contains("chole") && name.contains("bhature")) return "chole_bhature.jpg";
        if (name.contains("rajma")) return "rajma_chawal.jpg";
        if (name.contains("chole")) return "chole_chawal.jpg";
        if (name.contains("kadhi")) return "kadhi_chawal.jpg";
        if (name.contains("dal") && name.contains("makhani")) return "dal_makhani.jpg";
        if (name.contains("dal")) return "dal_tadka.jpg";
        if (name.contains("paneer") && (name.contains("butter") || name.contains("masala"))) return "paneer_butter_masala.jpg";
        if (name.contains("shahi") && name.contains("paneer")) return "shahi_paneer.jpg";
        if (name.contains("kadhai") && name.contains("paneer")) return "kadhai_paneer.jpg";
        if (name.contains("paneer") && name.contains("tikka")) return "paneer_tikka.jpg";
        if (name.contains("paneer") && name.contains("paratha")) return "paneer_paratha.jpg";
        if (name.contains("aloo") && name.contains("paratha")) return "aloo_paratha.jpg";
        if (name.contains("gobi") && name.contains("paratha")) return "gobi_paratha.jpg";
        if (name.contains("paratha")) return "stuffed_paratha.jpg";
        if (name.contains("thali") && name.contains("special")) return "special_thali.jpg";
        if (name.contains("thali")) return "veg_thali.jpg";
        if (name.contains("pulao") || name.contains("biryani")) return "veg_pulao.jpg";
        if (name.contains("rice")) return "jeera_rice.jpg";
        if (name.contains("pani puri")) return "pani_puri.jpg";
        if (name.contains("tikki")) return "aloo_tikki.jpg";
        if (name.contains("papdi")) return "papdi_chaat.jpg";
        if (name.contains("dahi puri")) return "dahi_puri.jpg";
        if (name.contains("bhel")) return "bhel_puri.jpg";
        if (name.contains("sev puri")) return "sev_puri.jpg";
        if (name.contains("kachori") && name.contains("raj")) return "raj_kachori.jpg";
        if (name.contains("kachori")) return "kachori.jpg";
        if (name.contains("bhalla")) return "dahi_bhalla.jpg";
        if (name.contains("samosa") && name.contains("chaat")) return "samosa_chaat.jpg";
        if (name.contains("samosa")) return "samosa.jpg";
        if (name.contains("sprouts")) return "sprouts_chaat.jpg";
        if (name.contains("fruit")) return "fruit_bowl.jpg";
        if (name.contains("salad")) return "vegetable_salad.jpg";
        if (name.contains("sandwich") && name.contains("paneer")) return "grilled_paneer_sandwich.jpg";
        if (name.contains("sandwich")) return "grilled_veg_sandwich.jpg";
        if (name.contains("wrap") && name.contains("paneer")) return "paneer_wrap.jpg";
        if (name.contains("wrap")) return "veg_wrap.jpg";
        if (name.contains("oats")) return "oats_bowl.jpg";
        if (name.contains("yogurt")) return "yogurt_fruit_bowl.jpg";
        if (name.contains("corn")) return "corn_bowl.jpg";
        if (name.contains("poha")) return "poha.jpg";
        if (name.contains("upma")) return "upma.jpg";
        if (name.contains("idli") || name.contains("sambar")) return "idli_sambar.jpg";
        if (name.contains("dosa")) return "masala_dosa.jpg";
        if (name.contains("chilla")) return "besan_chilla.jpg";
        if (name.contains("cutlet")) return "veg_cutlet.jpg";
        if (name.contains("pakora")) return "bread_pakora.jpg";
        if (name.contains("dhokla")) return "dhokla.jpg";
        if (name.contains("fries")) return "french_fries.jpg";
        if (name.contains("roll")) return "spring_roll.jpg";
        if (name.contains("chaas")) return "masala_chaas.jpg";
        if (name.contains("lassi") && name.contains("mango")) return "mango_lassi.jpg";
        if (name.contains("lassi")) return "sweet_lassi.jpg";
        if (name.contains("lemon") || name.contains("lemonade")) return "lemon_water.jpg";
        if (name.contains("orange")) return "orange_juice.jpg";
        if (name.contains("watermelon")) return "watermelon_juice.jpg";
        if (name.contains("coconut")) return "coconut_water.jpg";
        if (name.contains("coffee")) return "cold_coffee.jpg";
        if (name.contains("tea") || name.contains("chai")) return "masala_tea.jpg";
        if (name.contains("smoothie")) return "fruit_smoothie.jpg";
        if (name.contains("buttermilk")) return "buttermilk.jpg";
        if (name.contains("combo")) return "thali_combo.jpg";
        return null;
    }

    private static String matchByCategory(String category) {
        if (category.contains("north") || category.contains("meal")) return "category_north_indian.jpg";
        if (category.contains("chaat")) return "category_chaat.jpg";
        if (category.contains("healthy") && category.contains("beverage")) return "category_healthy_drinks.jpg";
        if (category.contains("healthy")) return "category_healthy.jpg";
        if (category.contains("breakfast")) return "category_breakfast.jpg";
        if (category.contains("snack")) return "category_snacks.jpg";
        if (category.contains("beverage")) return "category_beverages.jpg";
        if (category.contains("combo")) return "category_combos.jpg";
        return null;
    }

    private static Image loadResourceImage(String filename) {
        return IMAGE_CACHE.computeIfAbsent(filename, key -> {
            try {
                String path = "/images/food/" + key;
                InputStream is = FoodImageResolver.class.getResourceAsStream(path);
                if (is != null) {
                    return new Image(is, 400, 240, false, true);
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not load food image " + key + ": " + e.getMessage());
            }
            return null;
        });
    }
}
