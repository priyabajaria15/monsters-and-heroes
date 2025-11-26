public abstract class Living {
    protected String name;
    protected int level;
    protected int hp;

    public Living(String name, int level) {
        this.name = name;
        this.level = level;
        this.hp = level * 100; // spec formula :contentReference[oaicite:8]{index=8}
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); }
    public boolean isDead() { return hp <= 0; }
}
