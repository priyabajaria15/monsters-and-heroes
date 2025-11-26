public class Armor implements Item {
    private final String name;
    private final int cost;
    private final int level;
    private final int damageReduction;

    public Armor(String name, int cost, int level, int damageReduction) {
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() { return damageReduction; }

    @Override public String getName() { return name; }
    @Override public int getCost() { return cost; }
    @Override public int getRequiredLevel() { return level; }

    @Override
    public String toString() {
        return String.format("%s (L%d, cost:%d, reduction:%d)",
                name, level, cost, damageReduction);
    }
}
