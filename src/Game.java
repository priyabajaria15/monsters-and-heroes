import java.io.IOException;
import java.util.*;

public class Game {

    // Removed: private final Scanner scanner = new Scanner(System.in);
    private final List<Hero> availableHeroes = new ArrayList<>();
    private final List<Hero> party = new ArrayList<>();

    private final List<Monster> dragonPool = new ArrayList<>();
    private final List<Monster> exoskeletonPool = new ArrayList<>();
    private final List<Monster> spiritPool = new ArrayList<>();

    private final List<Weapon> weapons = new ArrayList<>();
    private final List<Armor> armors = new ArrayList<>();
    private final List<Potion> potions = new ArrayList<>();
    private final List<Spell> spells = new ArrayList<>();

    private GameMap map;
    private int heroRow = 0;
    private int heroCol = 0;
    private final Random random = new Random();

    public Game() throws IOException {
        loadData();
        // Map is created later in startNewGame() after player chooses size
    }

    // ========= DATA LOADING =========

    private void loadData() throws IOException {
        FileLoader.loadWarriors("Warriors.txt", availableHeroes);
        FileLoader.loadSorcerers("Sorcerers.txt", availableHeroes);
        FileLoader.loadPaladins("Paladins.txt", availableHeroes);

        FileLoader.loadDragons("Dragons.txt", dragonPool);
        FileLoader.loadExoskeletons("Exoskeletons.txt", exoskeletonPool);
        FileLoader.loadSpiritMonsters("Spirits.txt", spiritPool);

        FileLoader.loadWeapons("Weaponry.txt", weapons);
        FileLoader.loadArmors("Armory.txt", armors);
        FileLoader.loadPotions("Potions.txt", potions);
        FileLoader.loadFireSpells("FireSpells.txt", spells);
        FileLoader.loadIceSpells("IceSpells.txt", spells);
        FileLoader.loadLightningSpells("LightningSpells.txt", spells);
    }

    // ========= MAIN ENTRY =========

    public void start() {
        boolean exit = false;
        while (!exit) {
            GameIO.println("=== Legends: Monsters and Heroes ===");
            GameIO.println("1) Start Game");
            GameIO.println("2) How to Play");
            GameIO.println("3) Exit");

            int choice = GameIO.readIntOrQuit("Choose an option (1-3, or Q to quit): ");
            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    printHowToPlay();
                    break;
                case 3:
                    GameIO.println("Goodbye!");
                    exit = true;
                    break;
                default:
                    GameIO.println("Invalid choice, please enter 1, 2, or 3.");
            }
        }
    }

    private void startNewGame() {
        // Ask for map size (square: size x size)
        int size = 0;
        while (size < 5 || size > 15) {
            size = GameIO.readIntOrQuit("Enter map size (5-15, or Q to quit): ");
            if (size < 5 || size > 15) {
                GameIO.println("Please choose a size between 5 and 15.");
            }
        }

        // Create map with chosen size
        map = new GameMap(size, size);

        // Reset hero position and party
        heroRow = 0;
        heroCol = 0;
        party.clear();

        printIntro();
        chooseParty();
        gameLoop();
        GameIO.println("Thanks for playing! Returning to main menu...\n");
    }

    // ========= INFO / HELP =========

    private void printIntro() {
        GameIO.println("\nStarting a new adventure!");
        GameIO.println("Controls: W/A/S/D to move, I for info, M for market, Q to quit current game");
        GameIO.println("You can have between 1 and 3 heroes in your party.");
        GameIO.println("(You can also type Q instead of a number at any prompt to exit completely.)");
    }

    private void printHowToPlay() {
        GameIO.println("\n=== How to Play ===");
        GameIO.println("- You control a party of 1–3 heroes.");
        GameIO.println("- Each hero has HP, Mana, Strength, Dexterity, Agility, Gold, and Experience.");
        GameIO.println("");
        GameIO.println("On the map:");
        GameIO.println("  H = Your heroes");
        GameIO.println("  M = Market (buy/sell weapons, armor, potions, spells)");
        GameIO.println("  - = Common land (you may encounter monsters)");
        GameIO.println("  X = Inaccessible tile (you cannot step there)");
        GameIO.println("");
        GameIO.println("Controls during exploration:");
        GameIO.println("  W = move up");
        GameIO.println("  A = move left");
        GameIO.println("  S = move down");
        GameIO.println("  D = move right");
        GameIO.println("  M = enter market if you are on a Market tile");
        GameIO.println("  I = show party info (stats, equipment, gold)");
        GameIO.println("  Q = quit the current game and return to the main menu");
        GameIO.println("");
        GameIO.println("During battles (in Battle class):");
        GameIO.println("  1) Attack with your weapon");
        GameIO.println("  2) Cast a spell (if you have spells and enough mana)");
        GameIO.println("  3) Use a potion (heal or buff stats)");
        GameIO.println("  4) Equip a weapon from your inventory");
        GameIO.println("  5) Equip armor from your inventory");
        GameIO.println("  0) Skip the hero's turn");
        GameIO.println("");
        GameIO.println("Goal:");
        GameIO.println("- Explore the map, defeat monsters, earn gold and experience,");
        GameIO.println("  buy better gear from markets, and level up your heroes.");
        GameIO.println("====================\n");
    }

    // ========= PARTY SETUP =========

    private void chooseParty() {
        // temp list so we can remove chosen heroes without touching availableHeroes
        List<Hero> selectableHeroes = new ArrayList<>(availableHeroes);

        int maxParty = 0;
        while (maxParty < 1 || maxParty > 3) {
            maxParty = GameIO.readIntOrQuit("How many heroes in your party (1-3, or Q to quit): ");
        }

        while (party.size() < maxParty && !selectableHeroes.isEmpty()) {
            GameIO.println("\nAvailable Heroes:");
            for (int i = 0; i < selectableHeroes.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, selectableHeroes.get(i).shortStats());
            }

            int idx = GameIO.readIntOrQuit(
                    "Choose hero #" + (party.size() + 1) + " (or Q to quit): "
            ) - 1;

            if (idx >= 0 && idx < selectableHeroes.size()) {
                Hero baseHero = selectableHeroes.get(idx);
                Hero chosen = baseHero.copy();
                party.add(chosen);
                GameIO.println("Added: " + chosen.getName());

                // remove from selectable list so it can't be chosen again
                selectableHeroes.remove(idx);
            } else {
                GameIO.println("Invalid choice.");
            }
        }
    }

    // ========= MAIN GAME LOOP =========

    private void gameLoop() {
        boolean running = true;
        while (running) {
            GameIO.println("");
            map.print(heroRow, heroCol);

            String cmd = GameIO.readString("Command (W/A/S/D, I, M, Q): ").toUpperCase(Locale.ROOT);
            switch (cmd) {
                case "W": move(-1, 0); break;
                case "A": move(0, -1); break;
                case "S": move(1, 0); break;
                case "D": move(0, 1); break;
                case "I": printPartyInfo(); break;
                case "M": enterMarket(); break;
                case "Q": running = false; break; // back to main menu
                default: GameIO.println("Unknown command.");
            }
        }
    }

    private void move(int dr, int dc) {
        int nr = heroRow + dr;
        int nc = heroCol + dc;
        if (!map.inBounds(nr, nc)) {
            GameIO.println("Can't move off the map.");
            return;
        }
        if (map.getTile(nr, nc).getType() == TileType.INACCESSIBLE) {
            GameIO.println("That tile is inaccessible.");
            return;
        }
        heroRow = nr;
        heroCol = nc;
        TileType type = map.getTile(heroRow, heroCol).getType();
        if (type == TileType.COMMON) {
            if (random.nextDouble() < 0.3) {
                GameIO.println("You encountered monsters!");
                startBattle();
            }
        } else if (type == TileType.MARKET) {
            GameIO.println("You arrived at a market.");
        }
    }

    private void printPartyInfo() {
        GameIO.println("\n--- Party Info ---");
        for (Hero h : party) {
            GameIO.println(h.toString());
        }
    }

    // ========= MARKET LOGIC =========

    private void enterMarket() {
        if (map.getTile(heroRow, heroCol).getType() != TileType.MARKET) {
            GameIO.println("You are not on a market tile.");
            return;
        }
        boolean inMarket = true;
        while (inMarket) {
            GameIO.println("\n=== Market ===");
            GameIO.println("Select a hero to access the market for them.");
            GameIO.println("They can buy or sell items using their own gold.");
            GameIO.println("Party:");
            for (int i = 0; i < party.size(); i++) {
                System.out.printf("%d) %s (Gold: %d)%n",
                        i + 1, party.get(i).getName(), party.get(i).getGold());
            }
            GameIO.println("0) Exit market");
            int choice = GameIO.readIntOrQuit("Choose hero number to enter market, or 0 to exit (or Q to quit): ");
            if (choice == 0) {
                inMarket = false;
            } else if (choice > 0 && choice <= party.size()) {
                marketForHero(party.get(choice - 1));
            } else {
                GameIO.println("Invalid choice.");
            }
        }
    }

    private void marketForHero(Hero hero) {
        boolean done = false;
        while (!done) {
            GameIO.println("\nMarket for " + hero.getName() +
                    " (Gold: " + hero.getGold() + ")");
            GameIO.println("Choose an option for this hero:");
            GameIO.println("1) Buy Weapon");
            GameIO.println("2) Buy Armor");
            GameIO.println("3) Buy Potion");
            GameIO.println("4) Buy Spell");
            GameIO.println("5) Sell Item");
            GameIO.println("0) Back to hero selection");
            int c = GameIO.readIntOrQuit("Choice (or Q to quit): ");
            switch (c) {
                case 1: buyItem(hero, weapons); break;
                case 2: buyItem(hero, armors); break;
                case 3: buyItem(hero, potions); break;
                case 4: buyItem(hero, spells); break;
                case 5: sellItem(hero); break;
                case 0: done = true; break;
                default: GameIO.println("Invalid.");
            }
        }
    }

    private <T extends Item> void buyItem(Hero hero, List<T> list) {
        GameIO.println("Available items:");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, list.get(i));
        }
        int choice = GameIO.readIntOrQuit("Choose item (0 to cancel, or Q to quit): ");
        if (choice == 0) return;
        if (choice < 1 || choice > list.size()) {
            GameIO.println("Invalid choice.");
            return;
        }
        Item item = list.get(choice - 1);
        if (hero.getLevel() < item.getRequiredLevel()) {
            GameIO.println("Level too low for this item.");
            return;
        }
        if (hero.getGold() < item.getCost()) {
            GameIO.println("Not enough gold.");
            return;
        }
        hero.setGold(hero.getGold() - item.getCost());
        hero.getInventory().add(item);
        GameIO.println(hero.getName() + " bought " + item.getName());
    }

    private void sellItem(Hero hero) {
        List<Item> inv = hero.getInventory();
        if (inv.isEmpty()) {
            GameIO.println("Inventory empty.");
            return;
        }
        GameIO.println("Inventory:");
        for (int i = 0; i < inv.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, inv.get(i));
        }
        int c = GameIO.readIntOrQuit("Choose item to sell (0 to cancel, or Q to quit): ");
        if (c == 0) return;
        if (c < 1 || c > inv.size()) {
            GameIO.println("Invalid choice.");
            return;
        }
        Item item = inv.remove(c - 1);
        int sellPrice = item.getCost() / 2; // items sell for half price
        hero.setGold(hero.getGold() + sellPrice);
        GameIO.println("Sold " + item.getName() + " for " + sellPrice);
    }

    // ========= BATTLE LOGIC =========

    private void startBattle() {
        if (party.stream().allMatch(Hero::isFainted)) {
            GameIO.println("All heroes already fainted. Game over.");
            return;
        }
        int numHeroes = (int) party.stream().filter(h -> !h.isFainted()).count();
        if (numHeroes == 0) numHeroes = party.size();
        int highestLevel = party.stream().mapToInt(Hero::getLevel).max().orElse(1);

        List<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < numHeroes; i++) {
            monsters.add(randomMonsterOfLevel(highestLevel));
        }

        // Battle no longer needs Scanner from Game; it has its own or uses GameIO
        Battle battle = new Battle(new Scanner(System.in), party, monsters);
        boolean heroesWon = battle.fight();

        if (!heroesWon) {
            GameIO.println("The monsters have defeated the heroes... Game over.");
            System.exit(0);
        }
    }

    /**
     * Pick a monster whose level matches the heroes:
     *  - If hero level == 1  -> only monsters of level 1
     *  - Else               -> monsters within ±1 level if possible
     */
    private Monster randomMonsterOfLevel(int level) {
        List<List<Monster>> pools = Arrays.asList(dragonPool, exoskeletonPool, spiritPool);
        List<Monster> candidates = new ArrayList<>();

        // If heroes are level 1: ONLY allow level-1 monsters
        if (level == 1) {
            for (List<Monster> pool : pools) {
                for (Monster m : pool) {
                    if (m.getLevel() == 1) {
                        candidates.add(m);
                    }
                }
            }
        } else {
            // For level >= 2: prefer monsters whose level is close (±1)
            for (List<Monster> pool : pools) {
                for (Monster m : pool) {
                    if (Math.abs(m.getLevel() - level) <= 1) {
                        candidates.add(m);
                    }
                }
            }
        }

        // Fallback: if no candidates found, fall back to closest level overall
        if (candidates.isEmpty()) {
            Monster best = null;
            int bestDiff = Integer.MAX_VALUE;
            for (List<Monster> pool : pools) {
                for (Monster m : pool) {
                    int diff = Math.abs(m.getLevel() - level);
                    if (diff < bestDiff) {
                        bestDiff = diff;
                        best = m;
                    }
                }
            }
            // best should never be null if pools are non-empty
            candidates.add(best);
        }

        // Pick a random monster from our candidate list and return a copy
        Monster chosen = candidates.get(random.nextInt(candidates.size()));
        return chosen.copy();
    }
}
