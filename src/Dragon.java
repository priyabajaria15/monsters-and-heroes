public class Dragon extends Monster {
    public Dragon(String name, int level, int baseDamage, int defense, int dodgeChance) {
        super(name, level, baseDamage, defense, dodgeChance);
    }

    @Override
    public Monster copy() {
        Dragon d = new Dragon(name, level, baseDamage, defense, dodgeChance);
        d.hp = this.hp;
        return d;
    }
}
