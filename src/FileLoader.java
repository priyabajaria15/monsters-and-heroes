import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

public class FileLoader {

    private static BufferedReader readerFor(String filename) throws IOException {
        return new BufferedReader(new FileReader(filename));
    }

    // HEROES --------------------------------------------------

    public static void loadWarriors(String filename, List<Hero> heroes) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int mana = Integer.parseInt(st.nextToken());
                int strength = Integer.parseInt(st.nextToken());
                int agility = Integer.parseInt(st.nextToken());
                int dexterity = Integer.parseInt(st.nextToken());
                int money = Integer.parseInt(st.nextToken());
                int exp = Integer.parseInt(st.nextToken());
                heroes.add(new Warrior(name, 1, mana, strength, dexterity, agility, money, exp));
            }
        }
    }

    public static void loadSorcerers(String filename, List<Hero> heroes) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int mana = Integer.parseInt(st.nextToken());
                int strength = Integer.parseInt(st.nextToken());
                int agility = Integer.parseInt(st.nextToken());
                int dexterity = Integer.parseInt(st.nextToken());
                int money = Integer.parseInt(st.nextToken());
                int exp = Integer.parseInt(st.nextToken());
                heroes.add(new Sorcerer(name, 1, mana, strength, dexterity, agility, money, exp));
            }
        }
    }

    public static void loadPaladins(String filename, List<Hero> heroes) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int mana = Integer.parseInt(st.nextToken());
                int strength = Integer.parseInt(st.nextToken());
                int agility = Integer.parseInt(st.nextToken());
                int dexterity = Integer.parseInt(st.nextToken());
                int money = Integer.parseInt(st.nextToken());
                int exp = Integer.parseInt(st.nextToken());
                heroes.add(new Paladin(name, 1, mana, strength, dexterity, agility, money, exp));
            }
        }
    }

    // MONSTERS -------------------------------------------------

    public static void loadDragons(String filename, List<Monster> monsters) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int level = Integer.parseInt(st.nextToken());
                int damage = Integer.parseInt(st.nextToken());
                int defense = Integer.parseInt(st.nextToken());
                int dodgeChance = Integer.parseInt(st.nextToken());
                monsters.add(new Dragon(name, level, damage, defense, dodgeChance));
            }
        }
    }

    public static void loadExoskeletons(String filename, List<Monster> monsters) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int level = Integer.parseInt(st.nextToken());
                int damage = Integer.parseInt(st.nextToken());
                int defense = Integer.parseInt(st.nextToken());
                int dodgeChance = Integer.parseInt(st.nextToken());
                monsters.add(new Exoskeleton(name, level, damage, defense, dodgeChance));
            }
        }
    }

    public static void loadSpiritMonsters(String filename, List<Monster> monsters) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int level = Integer.parseInt(st.nextToken());
                int damage = Integer.parseInt(st.nextToken());
                int defense = Integer.parseInt(st.nextToken());
                int dodgeChance = Integer.parseInt(st.nextToken());
                monsters.add(new SpiritMonster(name, level, damage, defense, dodgeChance));
            }
        }
    }

    // ITEMS ----------------------------------------------------

    public static void loadWeapons(String filename, List<Weapon> list) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int cost = Integer.parseInt(st.nextToken());
                int level = Integer.parseInt(st.nextToken());
                int damage = Integer.parseInt(st.nextToken());
                int hands = Integer.parseInt(st.nextToken());
                list.add(new Weapon(name, cost, level, damage, hands));
            }
        }
    }

    public static void loadArmors(String filename, List<Armor> list) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int cost = Integer.parseInt(st.nextToken());
                int level = Integer.parseInt(st.nextToken());
                int reduction = Integer.parseInt(st.nextToken());
                list.add(new Armor(name, cost, level, reduction));
            }
        }
    }

    public static void loadPotions(String filename, List<Potion> list) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int cost = Integer.parseInt(st.nextToken());
                int level = Integer.parseInt(st.nextToken());
                int amount = Integer.parseInt(st.nextToken());
                StringBuilder attr = new StringBuilder();
                while (st.hasMoreTokens()) {
                    if (attr.length() > 0) attr.append("/");
                    attr.append(st.nextToken());
                }
                list.add(new Potion(name, cost, level, amount, attr.toString()));
            }
        }
    }

    public static void loadFireSpells(String filename, List<Spell> list) throws IOException {
        loadSpellsWithType(filename, list, SpellType.FIRE);
    }

    public static void loadIceSpells(String filename, List<Spell> list) throws IOException {
        loadSpellsWithType(filename, list, SpellType.ICE);
    }

    public static void loadLightningSpells(String filename, List<Spell> list) throws IOException {
        loadSpellsWithType(filename, list, SpellType.LIGHTNING);
    }

    private static void loadSpellsWithType(String filename, List<Spell> list, SpellType type) throws IOException {
        try (BufferedReader br = readerFor(filename)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                int cost = Integer.parseInt(st.nextToken());
                int level = Integer.parseInt(st.nextToken());
                int damage = Integer.parseInt(st.nextToken());
                int manaCost = Integer.parseInt(st.nextToken());
                list.add(new Spell(name, cost, level, damage, manaCost, type));
            }
        }
    }
}
