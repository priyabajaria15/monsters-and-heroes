public class Warrior extends Hero {
    public Warrior(String name, int level, int mana, int strength, int dexterity,
                   int agility, int gold, int experience) {
        super(name, level, mana, strength, dexterity, agility, gold, experience);
    }

    @Override
    protected void applyFavoredStatsOnLevelUp() {
        // Warriors favored: strength, agility :contentReference[oaicite:14]{index=14}
        strength = (int) (strength * 1.05);
        agility = (int) (agility * 1.05);
    }

    @Override
    public Hero copy() {
        Warrior w = new Warrior(name, level, mana, strength, dexterity, agility, gold, experience);
        w.hp = this.hp;
        return w;
    }
}
