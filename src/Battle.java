import java.util.*;

public class Battle {

    private final Scanner scanner;
    private final List<Hero> heroes;
    private final List<Monster> monsters;
    private final Random random = new Random();

    // ANSI colors for console
    private static final String RED   = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    // ========= COLORED MESSAGE HELPERS =========
    private void printPositive(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    private void printNegative(String msg) {
        System.out.println(RED + msg + RESET);
    }

    // ==================== MAIN BATTLE LOOP ====================

    public Battle(Scanner scanner, List<Hero> heroes, List<Monster> monsters) {
        this.scanner = scanner;
        this.heroes = heroes;
        this.monsters = monsters;
    }

    public boolean fight() {
        System.out.println("\n=== Battle Start ===");

        while (!allHeroesFainted() && !allMonstersDead()) {
            printStatus();
            heroTurn();
            if (allMonstersDead()) break;
            monsterTurn();
            endOfRoundRegen();
        }

        if (allMonstersDead()) {
            printPositive("Heroes won the battle!");
            handleVictory();
            return true;
        } else {
            printNegative("Heroes were defeated...");
            return false;
        }
    }

    private boolean allHeroesFainted() {
        return heroes.stream().allMatch(Hero::isFainted);
    }

    private boolean allMonstersDead() {
        return monsters.stream().allMatch(Monster::isDead);
    }

    // ==================== STATUS DISPLAY ====================

    private void printStatus() {
        System.out.println("\n--- Heroes ---");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.printf(
                    "%d) %s (HP:%d, MP:%d%s)%n",
                    i + 1,
                    h.getName(),
                    h.getHp(),
                    h.getMana(),
                    h.isFainted() ? ", Fainted:true" : ""
            );
        }

        System.out.println("--- Monsters ---");
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            System.out.printf(
                    "%d) %s (HP:%d, Dmg:%d, Def:%d)%n",
                    i + 1,
                    m.getName(),
                    m.getHp(),
                    m.getBaseDamage(),
                    m.getDefense()
            );
        }
    }

    // ==================== HERO TURN ====================

    /**
     * Each non-fainted hero gets a turn.
     * Turn is consumed only when a real action happens:
     *  - valid attack / spell / potion
     *  - or skip (0)
     * Equip gear does NOT consume a turn.
     */
    private void heroTurn() {
        for (Hero h : heroes) {
            if (h.isFainted() || h.isDead()) continue;

            boolean done = false;
            while (!done) {
                System.out.println("\nAction for " + h.getName());
                System.out.println("1) Attack");
                System.out.println("2) Cast Spell");
                System.out.println("3) Use Potion");
                System.out.println("4) Equip Gear (Weapon/Armor)");
                System.out.println("0) Skip");
                System.out.print("Choice: ");

                int c = readInt();
                switch (c) {
                    case 1:
                        if (heroAttack(h)) done = true;
                        break;
                    case 2:
                        if (heroCastSpell(h)) done = true;
                        break;
                    case 3:
                        if (heroUsePotion(h)) done = true;
                        break;
                    case 4:
                        heroEquipGear(h); // no turn consumption
                        break;
                    case 0:
                        done = true; // skip
                        break;
                    default:
                        printNegative("Invalid option. Please choose again.");
                }
            }
        }
    }

    // ==================== HERO ATTACK ====================

    /**
     * Returns true if the attack was actually performed
     * (hit or dodge). Returns false only when player cancels
     * target selection.
     */
    private boolean heroAttack(Hero h) {
        Monster target = chooseMonster(true); // allow cancel
        if (target == null) {
            printNegative("Attack cancelled.");
            return false;
        }

        // Check dodge first
        if (random.nextDouble() < target.dodgeChance()) {
            printNegative(target.getName() + " dodged the attack!");
            return true; // turn is still consumed
        }

        // Base damage from hero/weapon + level scaling
        int weaponBase = h.weaponDamage();
        int levelBonus = 5 * h.getLevel();      // extra damage per level
        int base = weaponBase + levelBonus;

        // Safety fallback if weaponDamage is tiny or 0
        if (base <= 0) {
            base = 15 + 5 * h.getLevel();
        }

        // Defense now reduces damage proportionally instead of fully subtracting
        // Example: def = 200 → factor ≈ 100 / 300 ≈ 0.33
        double defenseFactor = 100.0 / (100.0 + target.getDefense());
        int effective = (int) Math.round(base * defenseFactor);

        // Ensure a meaningful hit if the attack lands
        if (effective < 5) {
            effective = 5;
        }

        target.setHp(target.getHp() - effective);

        printPositive(String.format(
                "%s attacked %s for %d damage!",
                h.getName(), target.getName(), effective
        ));

        return true;
    }

    // ==================== HERO SPELL CAST ====================

    private boolean heroCastSpell(Hero h) {
        // Gather spells
        List<Spell> spells = new ArrayList<>();
        for (Item item : h.getInventory()) {
            if (item instanceof Spell) spells.add((Spell) item);
        }

        if (spells.isEmpty()) {
            printNegative("No spells in inventory.");
            return false;
        }

        while (true) {
            System.out.println("Spells:");
            for (int i = 0; i < spells.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, spells.get(i));
            }
            System.out.print("Choose spell (0 to cancel): ");

            int c = readInt();
            if (c == 0) {
                printNegative("Spell selection cancelled.");
                return false;
            }
            if (c < 1 || c > spells.size()) {
                printNegative("Invalid spell choice. Please choose again.");
                continue;
            }

            Spell s = spells.get(c - 1);

            if (h.getMana() < s.getManaCost()) {
                printNegative("Not enough mana for that spell. Choose another.");
                continue;
            }

            Monster target = chooseMonster(true);
            if (target == null) {
                printNegative("Spell cancelled (no valid target chosen).");
                return false;
            }

            // Spend mana
            h.setMana(h.getMana() - s.getManaCost());

            // Dodge check
            if (random.nextDouble() < target.dodgeChance()) {
                printNegative(target.getName() + " dodged the spell!");
                h.getInventory().remove(s); // consume spell
                return true;
            }

            int base = h.spellDamage(s);
            if (base <= 0) base = 10 + h.getLevel() * 3;

            int raw = base - target.getDefense();
            int effective = Math.max(1, raw); // still okay for spells

            target.setHp(target.getHp() - effective);

            printPositive(String.format(
                    "%s cast %s on %s for %d damage!",
                    h.getName(), s.getName(), target.getName(), effective
            ));

            // Apply spell-specific debuffs (good for hero)
            switch (s.getType()) {
                case FIRE:
                    target.setDefense((int) (target.getDefense() * 0.9));
                    printPositive(target.getName() + "'s defense was reduced!");
                    break;
                case ICE:
                    target.setBaseDamage((int) (target.getBaseDamage() * 0.9));
                    printPositive(target.getName() + "'s damage was reduced!");
                    break;
                case LIGHTNING:
                    target.reduceDodgeChance();
                    printPositive(target.getName() + "'s dodge chance was reduced!");
                    break;
            }

            // Single-use spell
            h.getInventory().remove(s);
            return true;
        }
    }

    // ==================== HERO POTION USE ====================

    private boolean heroUsePotion(Hero h) {
        List<Potion> pots = new ArrayList<>();
        for (Item item : h.getInventory()) {
            if (item instanceof Potion) pots.add((Potion) item);
        }

        if (pots.isEmpty()) {
            printNegative("No potions in inventory.");
            return false;
        }

        while (true) {
            System.out.println("Potions:");
            for (int i = 0; i < pots.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, pots.get(i));
            }

            System.out.print("Choose potion (0 to cancel): ");
            int c = readInt();

            if (c == 0) {
                printNegative("Potion use cancelled.");
                return false;
            }
            if (c < 1 || c > pots.size()) {
                printNegative("Invalid potion choice. Please choose again.");
                continue;
            }

            Potion p = pots.get(c - 1);
            p.applyTo(h);
            h.getInventory().remove(p);

            printPositive(h.getName() + " used potion " + p.getName());
            return true;
        }
    }

    // ==================== HERO EQUIP GEAR ====================

    private void heroEquipGear(Hero h) {
        while (true) {
            System.out.println("\nEquip Gear for " + h.getName());
            System.out.println("1) Equip Weapon");
            System.out.println("2) Equip Armor");
            System.out.println("0) Back");
            System.out.print("Choice: ");

            int c = readInt();
            switch (c) {
                case 1:
                    heroEquipWeapon(h);
                    break;
                case 2:
                    heroEquipArmor(h);
                    break;
                case 0:
                    return;
                default:
                    printNegative("Invalid choice. Please try again.");
            }
        }
    }

    private void heroEquipWeapon(Hero h) {
        List<Weapon> ws = new ArrayList<>();
        for (Item item : h.getInventory()) {
            if (item instanceof Weapon) ws.add((Weapon) item);
        }

        if (ws.isEmpty()) {
            printNegative("No weapons in inventory.");
            return;
        }

        System.out.println("Weapons:");
        for (int i = 0; i < ws.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, ws.get(i));
        }

        System.out.print("Choose weapon (0 to cancel): ");
        int c = readInt();
        if (c == 0) return;
        if (c < 1 || c > ws.size()) {
            printNegative("Invalid choice.");
            return;
        }

        Weapon w = ws.get(c - 1);
        h.equipWeapon(w);
        printPositive(h.getName() + " equipped " + w.getName());
    }

    private void heroEquipArmor(Hero h) {
        List<Armor> as = new ArrayList<>();
        for (Item item : h.getInventory()) {
            if (item instanceof Armor) as.add((Armor) item);
        }

        if (as.isEmpty()) {
            printNegative("No armor in inventory.");
            return;
        }

        System.out.println("Armor:");
        for (int i = 0; i < as.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, as.get(i));
        }

        System.out.print("Choose armor (0 to cancel): ");
        int c = readInt();
        if (c == 0) return;
        if (c < 1 || c > as.size()) {
            printNegative("Invalid choice.");
            return;
        }

        Armor a = as.get(c - 1);
        h.equipArmor(a);
        printPositive(h.getName() + " equipped " + a.getName());
    }

    // ==================== TARGET SELECTION ====================

    /**
     * Helper to choose a monster.
     *  - If only 1 alive → auto-target.
     *  - If allowCancel and 0 → return null.
     *  - Invalid input → re-prompt.
     */
    private Monster chooseMonster(boolean allowCancel) {
        while (true) {
            List<Monster> alive = new ArrayList<>();
            for (Monster m : monsters) {
                if (!m.isDead()) alive.add(m);
            }

            if (alive.isEmpty()) {
                printNegative("No monsters to target.");
                return null;
            }

            if (alive.size() == 1) {
                Monster only = alive.get(0);
                System.out.println("Only one target: "
                        + only.getName()
                        + " (HP:" + only.getHp() + ") → auto-targeted.");
                return only;
            }

            System.out.println("Choose target:");
            for (int i = 0; i < alive.size(); i++) {
                Monster m = alive.get(i);
                System.out.printf("%d) %s (HP:%d)%n", i + 1, m.getName(), m.getHp());
            }

            if (allowCancel) {
                System.out.print("Choice (0 to cancel): ");
            } else {
                System.out.print("Choice: ");
            }

            int c = readInt();
            if (allowCancel && c == 0) return null;
            if (c < 1 || c > alive.size()) {
                printNegative("Invalid target choice. Please choose again.");
                continue;
            }

            return alive.get(c - 1);
        }
    }

    // ==================== MONSTER TURN ====================

    private void monsterTurn() {
        System.out.println("\n--- Monsters' Turn ---");

        List<Hero> aliveHeroes = new ArrayList<>();
        for (Hero h : heroes) {
            if (!h.isFainted()) aliveHeroes.add(h);
        }
        if (aliveHeroes.isEmpty()) return;

        for (Monster m : monsters) {
            if (m.isDead()) continue;

            Hero target = aliveHeroes.get(random.nextInt(aliveHeroes.size()));

            // Dodge
            if (random.nextDouble() < target.dodgeChance()) {
                printPositive(target.getName()
                        + " dodged the attack from " + m.getName() + "!");
                continue;
            }

            int dmg = m.getBaseDamage();
            int reduction = (target.getArmor() != null)
                    ? target.getArmor().getDamageReduction()
                    : 0;

            int effective = Math.max(0, dmg - reduction);
            if (effective <= 0) {
                printPositive(String.format(
                        "%s attacked %s but dealt no damage!",
                        m.getName(), target.getName()
                ));
                continue;
            }

            int scaled = scaleMonsterDamage(effective, target);

            target.setHp(target.getHp() - scaled);
            // *** CHANGED: removed "(scaled from %d)" from the message ***
            printNegative(String.format(
                    "%s attacked %s for %d damage!",
                    m.getName(), target.getName(), scaled
            ));

            if (target.getHp() <= 0) {
                target.setFainted(true);
                printNegative(target.getName() + " fainted!");
            }
        }
    }

    /**
     * Scale monster damage so fights are fairer:
     *  - Monsters do about 10%–20% of their effective damage.
     *  - Capped to at most ~20% of hero's max HP.
     *  - Still at least 2 damage so they don't feel harmless.
     */
    private int scaleMonsterDamage(int baseDamage, Hero target) {
        // Softer factor: 0.10–0.20 instead of 0.15–0.30
        double factor = 0.10 + random.nextDouble() * 0.10; // 0.10–0.20
        int scaled = (int) Math.round(baseDamage * factor);

        // Hero max HP based on level
        int heroMaxHp = target.getLevel() * 100;

        // Cap at ~20% of hero's max HP
        int cap = Math.max(1, heroMaxHp / 5); // 1/5 = 20%
        scaled = Math.min(scaled, cap);

        // Monsters should still chip away, but not chunk too hard
        return Math.max(2, scaled); // at least 2 damage
    }

    // ==================== END OF ROUND & VICTORY ====================

    private void endOfRoundRegen() {
        for (Hero h : heroes) {
            if (!h.isFainted()) {
                h.setHp((int) (h.getHp() * 1.1));
                h.setMana((int) (h.getMana() * 1.1));
            }
        }
    }

    private void handleVictory() {
        int numMonsters = monsters.size();
        int expPerHero = numMonsters * 2;

        int maxMonsterLevel = monsters.stream()
                .mapToInt(Monster::getLevel)
                .max()
                .orElse(1);

        int totalGold = maxMonsterLevel * 100 * numMonsters;

        List<Hero> activeHeroes = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.isFainted() && !h.isDead()) {
                // fainted heroes get revived later, but are not "active"
            } else if (!h.isDead()) {
                activeHeroes.add(h);
            }
        }

        int goldPerHero = activeHeroes.isEmpty()
                ? 0
                : totalGold / activeHeroes.size();

        for (Hero h : heroes) {
            if (h.isFainted()) {
                h.setHp(h.getLevel() * 50);
                h.setMana(h.getMana() / 2);
                h.setFainted(false);
                printNegative(h.getName()
                        + " was revived but gained no gold or exp.");
            } else if (!h.isDead()) {
                h.gainExpAndGold(expPerHero, goldPerHero);
                printPositive(String.format(
                        "%s gained %d exp and %d gold.",
                        h.getName(), expPerHero, goldPerHero
                ));
            } else {
                printNegative(h.getName()
                        + " is dead and gains no rewards.");
            }
        }
    }

    // ==================== INPUT HELPERS ====================

    private int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (Exception e) {
                System.out.print("Enter a number: ");
            }
        }
    }
}
