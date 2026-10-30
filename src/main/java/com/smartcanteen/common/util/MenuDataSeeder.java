package com.smartcanteen.common.util;

import com.smartcanteen.model.MenuItem;
import com.smartcanteen.backend.service.MenuService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuDataSeeder {

    private static final MenuService menuService = new MenuService();

    private MenuDataSeeder() {
        // Utility class
    }

    /**
     * Seeds or updates the comprehensive 8-category college canteen catalog
     * without inserting duplicates.
     */
    public static void seedMenuCatalog() {
        try {
            List<MenuItem> catalog = getFullCatalog();
            for (MenuItem item : catalog) {
                MenuItem existing = menuService.getByName(item.getItemName());
                if (existing != null) {
                    existing.setCategory(item.getCategory());
                    existing.setPrice(item.getPrice());
                    existing.setDescription(item.getDescription());
                    existing.setAvailable(item.isAvailable());
                    menuService.updateItem(existing);
                } else {
                    menuService.addItem(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Menu catalog automatic seed skipped: " + e.getMessage());
        }
    }

    public static List<MenuItem> getFullCatalog() {
        List<MenuItem> list = new ArrayList<>();

        // ----------------------------------------------------
        // CATEGORY 1 — NORTH INDIAN MEALS (18 items)
        // ----------------------------------------------------
        list.add(createItem("Chole Bhature", "North Indian Meals", "80.00", "Two fluffy fried bhaturas served with spicy Punjabi chole, pickle and onion"));
        list.add(createItem("Rajma Chawal", "North Indian Meals", "75.00", "Classic slow-cooked kidney beans in rich spiced tomato gravy served with steamed basmati rice"));
        list.add(createItem("Chole Chawal", "North Indian Meals", "70.00", "Aromatic spiced chickpea curry served over hot steamed jeera rice with salad"));
        list.add(createItem("Kadhi Chawal", "North Indian Meals", "70.00", "Traditional Punjabi tangy yogurt kadhi with soft pakodas served with steamed rice"));
        list.add(createItem("Dal Tadka with Rice", "North Indian Meals", "75.00", "Yellow lentils tempered with cumin, garlic, and ghee served with steamed rice"));
        list.add(createItem("Dal Makhani with Rice", "North Indian Meals", "90.00", "Creamy slow-cooked black lentils simmered with butter and spices served with rice"));
        list.add(createItem("Paneer Butter Masala with Roti", "North Indian Meals", "110.00", "Cottage cheese cubes in rich buttery tomato gravy served with 3 soft butter rotis"));
        list.add(createItem("Shahi Paneer with Roti", "North Indian Meals", "115.00", "Royal Mughlai style paneer in creamy cashew-nut gravy served with 3 tawa rotis"));
        list.add(createItem("Kadhai Paneer with Roti", "North Indian Meals", "110.00", "Paneer tossed with bell peppers, onions, and freshly ground kadhai masala with 3 rotis"));
        list.add(createItem("Mix Veg with Roti", "North Indian Meals", "85.00", "Seasonal fresh vegetables cooked in homestyle spiced gravy served with 3 rotis"));
        list.add(createItem("Aloo Paratha", "North Indian Meals", "50.00", "Whole wheat tawa paratha stuffed with spiced mashed potatoes, served with pickle"));
        list.add(createItem("Paneer Paratha", "North Indian Meals", "70.00", "Wholesome wheat paratha stuffed with seasoned crumbled cottage cheese"));
        list.add(createItem("Gobi Paratha", "North Indian Meals", "55.00", "Crispy golden paratha stuffed with spiced grated cauliflower and herbs"));
        list.add(createItem("Stuffed Paratha Combo", "North Indian Meals", "95.00", "Two assorted stuffed parathas (Aloo & Paneer) served with curd and mint chutney"));
        list.add(createItem("Jeera Rice", "North Indian Meals", "60.00", "Fluffy basmati rice tempered with roasted cumin seeds and fresh coriander"));
        list.add(createItem("Veg Pulao", "North Indian Meals", "80.00", "Fragrant rice cooked with garden green peas, carrots, beans, and whole spices"));
        list.add(createItem("Veg Thali", "North Indian Meals", "100.00", "Complete meal: Dal, seasonal sabzi, 3 rotis, rice, salad, and pickle"));
        list.add(createItem("Special North Indian Thali", "North Indian Meals", "140.00", "Paneer Butter Masala, Dal Makhani, Mix Veg, Jeera Rice, 3 Butter Rotis, Gulab Jamun, and Salad"));

        // ----------------------------------------------------
        // CATEGORY 2 — CHAAT (10 items)
        // ----------------------------------------------------
        list.add(createItem("Pani Puri", "Chaat", "35.00", "8 crispy puris filled with spiced potato-sprouts mash, sweet tamarind chutney, and spicy mint water"));
        list.add(createItem("Aloo Tikki Chaat", "Chaat", "50.00", "Two crispy golden potato patties topped with warm chole, sweetened curd, and tangy chutneys"));
        list.add(createItem("Papdi Chaat", "Chaat", "50.00", "Crispy flour crisps layered with boiled potatoes, chickpeas, whipped yogurt, and spices"));
        list.add(createItem("Dahi Puri", "Chaat", "55.00", "Crispy puris stuffed with potato mash, chilled sweetened curd, sev, and sweet-sour chutneys"));
        list.add(createItem("Bhel Puri", "Chaat", "40.00", "Puffed rice tossed with onions, tomatoes, raw mango, chutneys, and crisp sev"));
        list.add(createItem("Sev Puri", "Chaat", "45.00", "Flat crisp papdis topped with spiced potatoes, onions, trio of chutneys, and nylon sev"));
        list.add(createItem("Raj Kachori", "Chaat", "75.00", "King-sized crisp kachori shell stuffed with bhallas, sprouts, yogurt, chutneys, and pomegranate"));
        list.add(createItem("Dahi Bhalla", "Chaat", "55.00", "Soft melt-in-mouth lentil dumplings immersed in spiced curd topped with roasted cumin and saunth"));
        list.add(createItem("Samosa Chaat", "Chaat", "50.00", "Crushed hot samosa drenched with Punjabi chole curry, whisked yogurt, and coriander chutney"));
        list.add(createItem("Chole Tikki Chaat", "Chaat", "60.00", "Crispy spiced aloo tikki drenched in rich Amritsari chole gravy with diced ginger and onions"));

        // ----------------------------------------------------
        // CATEGORY 3 — HEALTHY FOOD (12 items)
        // ----------------------------------------------------
        list.add(createItem("Sprouts Chaat", "Healthy Food", "50.00", "Protein-packed steamed moong and chana sprouts tossed with lemon juice, cucumber, and chaat masala"));
        list.add(createItem("Fresh Fruit Bowl", "Healthy Food", "60.00", "Freshly sliced seasonal fruits including apple, papaya, watermelon, pineapple, and pomegranate"));
        list.add(createItem("Mixed Fruit Chaat", "Healthy Food", "65.00", "Chilled seasonal fruits tossed with black salt, roasted cumin, and a splash of fresh lemon"));
        list.add(createItem("Vegetable Salad", "Healthy Food", "45.00", "Crunchy slices of cucumber, tomato, carrot, beetroot, and onion with lemon wedge"));
        list.add(createItem("Paneer Salad", "Healthy Food", "80.00", "Fresh cottage cheese cubes tossed with bell peppers, cucumbers, lettuce, and herb lemon dressing"));
        list.add(createItem("Grilled Vegetable Sandwich", "Healthy Food", "55.00", "Whole wheat bread grilled with zucchini, capsicum, tomato, and mint yogurt spread"));
        list.add(createItem("Grilled Paneer Sandwich", "Healthy Food", "70.00", "Golden grilled multigrain sandwich stuffed with spiced cottage cheese and crunch veggies"));
        list.add(createItem("Vegetable Wrap", "Healthy Food", "60.00", "Soft whole wheat tortilla rolled with spiced sautéed veggies, shredded lettuce, and hung curd spread"));
        list.add(createItem("Paneer Wrap", "Healthy Food", "80.00", "Whole wheat wrap stuffed with grilled marinated paneer tikka strips and crisp bell peppers"));
        list.add(createItem("Oats Bowl", "Healthy Food", "50.00", "Warm nutritious rolled oats cooked in milk with honey, sliced bananas, and chia seeds"));
        list.add(createItem("Yogurt and Fruit Bowl", "Healthy Food", "65.00", "Probiotic Greek-style curd topped with honey, fresh seasonal berries, and fruit chunks"));
        list.add(createItem("Corn and Vegetable Bowl", "Healthy Food", "55.00", "Steamed sweet corn kernels tossed with steamed veggies, herbs, butter, and pepper"));

        // ----------------------------------------------------
        // CATEGORY 4 — BREAKFAST (9 items)
        // ----------------------------------------------------
        list.add(createItem("Poha", "Breakfast", "35.00", "Light and fluffy flattened rice tempered with mustard seeds, curry leaves, onions, and crunchy peanuts"));
        list.add(createItem("Upma", "Breakfast", "35.00", "Traditional savory semolina dish tempered with ginger, mustard seeds, and garden vegetables"));
        list.add(createItem("Aloo Paratha (Breakfast)", "Breakfast", "50.00", "Hot tawa paratha stuffed with seasoned potato mash served with curd and butter"));
        list.add(createItem("Paneer Paratha (Breakfast)", "Breakfast", "70.00", "Protein-rich tawa paratha stuffed with spiced grated paneer and herbs"));
        list.add(createItem("Vegetable Sandwich", "Breakfast", "40.00", "Classic sandwich layered with cucumber, tomato, boiled potato, and green chutney"));
        list.add(createItem("Idli Sambar", "Breakfast", "35.00", "Two steamed fluffy rice cakes served with hot vegetable sambar and fresh coconut chutney"));
        list.add(createItem("Masala Dosa", "Breakfast", "55.00", "Crispy golden fermented crepe stuffed with spiced potato masala, served with sambar and chutney"));
        list.add(createItem("Besan Chilla", "Breakfast", "45.00", "Two savory gram flour pancakes loaded with finely chopped onions, tomatoes, and green chilies"));
        list.add(createItem("Moong Dal Chilla", "Breakfast", "50.00", "Healthy and protein-rich yellow lentil crepes served with mint and tamarind chutneys"));

        // ----------------------------------------------------
        // CATEGORY 5 — SNACKS (10 items)
        // ----------------------------------------------------
        list.add(createItem("Samosa", "Snacks", "20.00", "Two crispy golden triangular pastries filled with spiced potatoes, peas, and coriander"));
        list.add(createItem("Paneer Samosa", "Snacks", "30.00", "Crispy puff pastry stuffed with spicy crumbled paneer and herbs"));
        list.add(createItem("Veg Cutlet", "Snacks", "35.00", "Two crispy crumb-coated vegetable patties packed with beetroot, peas, and potatoes"));
        list.add(createItem("Bread Pakora", "Snacks", "25.00", "Spiced potato sandwich coated in seasoned gram flour batter and fried till golden"));
        list.add(createItem("Kachori", "Snacks", "25.00", "Flaky crispy pastry stuffed with spiced moong dal filling served with tangy saunth"));
        list.add(createItem("Dhokla", "Snacks", "35.00", "Steamed spongy fermented gram flour cakes tempered with mustard seeds and green chilies"));
        list.add(createItem("Paneer Tikka", "Snacks", "90.00", "Skewered cubes of cottage cheese and capsicum marinated in spiced yogurt and grilled"));
        list.add(createItem("French Fries", "Snacks", "45.00", "Crispy golden potato fingers served hot with tomato ketchup"));
        list.add(createItem("Masala Fries", "Snacks", "55.00", "Crispy potato fries dusted with zesty peri-peri and chaat masala seasoning"));
        list.add(createItem("Veg Spring Roll", "Snacks", "60.00", "Crispy rolled wrappers stuffed with seasoned shredded cabbage, carrots, and glass noodles"));

        // ----------------------------------------------------
        // CATEGORY 6 — BEVERAGES (11 items)
        // ----------------------------------------------------
        list.add(createItem("Masala Chaas", "Beverages", "25.00", "Refreshing spiced buttermilk infused with roasted cumin, ginger, and fresh mint leaves"));
        list.add(createItem("Sweet Lassi", "Beverages", "40.00", "Thick creamy Punjabi yogurt drink churned with sugar and topped with malai"));
        list.add(createItem("Mango Lassi", "Beverages", "50.00", "Delicious smooth blend of sweet Alphonso mango pulp and chilled thick yogurt"));
        list.add(createItem("Fresh Lemon Water", "Beverages", "20.00", "Chilled lemonade with a balanced sweet and salted touch"));
        list.add(createItem("Mint Lemon Water", "Beverages", "25.00", "Icy lemon cooler blended with crushed mint leaves and black salt"));
        list.add(createItem("Fresh Orange Juice", "Beverages", "50.00", "Freshly squeezed pure orange juice without added preservatives"));
        list.add(createItem("Watermelon Juice", "Beverages", "40.00", "Hydrating fresh watermelon juice served chilled with a hint of rock salt"));
        list.add(createItem("Coconut Water", "Beverages", "45.00", "Natural pure tender coconut water served chilled"));
        list.add(createItem("Cold Coffee", "Beverages", "50.00", "Creamy blended iced coffee made with rich espresso, cold milk, and chocolate drizzle"));
        list.add(createItem("Masala Tea", "Beverages", "15.00", "Hot freshly brewed milk tea simmered with fresh crushed ginger and cardamom"));
        list.add(createItem("Green Tea", "Beverages", "20.00", "Soothing hot infusion of whole organic green tea leaves"));

        // ----------------------------------------------------
        // CATEGORY 7 — HEALTHY BEVERAGES (6 items)
        // ----------------------------------------------------
        list.add(createItem("Detox Lemon Mint", "Healthy Beverages", "35.00", "Infused detox water with cucumber, fresh lemon slices, and crushed mint sprigs"));
        list.add(createItem("Fruit Smoothie", "Healthy Beverages", "65.00", "Nutrient-dense thick smoothie blended with mixed berries, banana, and Greek yogurt"));
        list.add(createItem("Banana Smoothie", "Healthy Beverages", "50.00", "Energizing thick shake made with ripe bananas, honey, milk, and crushed almonds"));
        list.add(createItem("Mango Smoothie", "Healthy Beverages", "65.00", "Creamy tropical smoothie made with real mango chunks and probiotic yogurt"));
        list.add(createItem("Buttermilk", "Healthy Beverages", "25.00", "Traditional low-fat salted buttermilk tempered with curry leaves and hing"));
        list.add(createItem("Fresh Coconut Water", "Healthy Beverages", "45.00", "Naturally electrolyte-rich fresh tender coconut water served directly"));

        // ----------------------------------------------------
        // CATEGORY 8 — COMBOS (10 items)
        // ----------------------------------------------------
        list.add(createItem("Chole Bhature + Chaas", "Combos", "95.00", "Two hot bhaturas with rich Punjabi chole served with a glass of spiced masala chaas"));
        list.add(createItem("Rajma Chawal + Salad", "Combos", "90.00", "Generous portion of Rajma Chawal served with fresh garden green salad and pickle"));
        list.add(createItem("Veg Thali + Chaas", "Combos", "120.00", "Complete North Indian Thali served along with a refreshing glass of chilled masala chaas"));
        list.add(createItem("Aloo Paratha + Curd", "Combos", "65.00", "Two tawa aloo parathas served with a bowl of fresh chilled curd and mixed pickle"));
        list.add(createItem("Paneer Paratha + Lassi", "Combos", "105.00", "Two hearty stuffed paneer parathas accompanied with a tall glass of sweet Punjabi lassi"));
        list.add(createItem("Samosa + Masala Tea", "Combos", "30.00", "Classic student favorite: Two hot crispy samosas with a steaming cup of adrak masala chai"));
        list.add(createItem("Aloo Tikki + Lemon Water", "Combos", "65.00", "Crispy Aloo Tikki Chaat paired with a chilled glass of fresh mint lemon water"));
        list.add(createItem("Grilled Sandwich + Fresh Juice", "Combos", "90.00", "Grilled vegetable sandwich paired with fresh watermelon or orange juice"));
        list.add(createItem("Healthy Salad + Lemon Water", "Combos", "60.00", "Nutritious sprouts or vegetable salad served with chilled detox lemon water"));
        list.add(createItem("Paneer Wrap + Smoothie", "Combos", "135.00", "Grilled marinated paneer tikka wrap served with a rich banana or fruit smoothie"));

        return list;
    }

    private static MenuItem createItem(String name, String category, String price, String description) {
        MenuItem item = new MenuItem();
        item.setItemName(name);
        item.setCategory(category);
        item.setPrice(new BigDecimal(price));
        item.setAvailable(true);
        item.setDescription(description);
        return item;
    }
}
