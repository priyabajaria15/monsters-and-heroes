public class SpiritMonster extends Monster {
    public SpiritMonster(String name, int level, int baseDamage, int defense, int dodgeChance) {
        super(name, level, baseDamage, defense, dodgeChance);
    }

    @Override
    public Monster copy() {
        SpiritMonster s = new SpiritMonster(name, level, baseDamage, defense, dodgeChance);
        s.hp = this.hp;
        return s;
    }
}
