public class Potion implements Item {
    private final String name;
    private final int cost;
    private final int level;
    private final int amount;
    private final String affectedAttributes; // ex: "Health", "Mana", "Health/Mana/Strength" etc.

    public Potion(String name, int cost, int level, int amount, String affectedAttributes) {
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.amount = amount;
        this.affectedAttributes = affectedAttributes;
    }

    @Override public String getName() { return name; }
    @Override public int getCost() { return cost; }
    @Override public int getRequiredLevel() { return level; }

    public void applyTo(Hero h) {
        String[] attrs = affectedAttributes.split("/");
        for (String a : attrs) {
            a = a.trim().toLowerCase();
            switch (a) {
                case "health":
                    h.setHp(h.getHp() + amount);
                    break;
                case "mana":
                    h.setMana(h.getMana() + amount);
                    break;
                case "strength":
                    // no setter for strength, but we can hack via reflection or extend.
                    // For simplicity, we won't change base stats here except HP/MP.
                    break;
                case "agility":
                case "dexterity":
                case "defense":
                    // Could be implemented similarly by adding setters to Hero.
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s (L%d, cost:%d, +%d to %s)",
                name, level, cost, amount, affectedAttributes);
    }
}
