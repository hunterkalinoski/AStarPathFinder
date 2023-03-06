public class Cell {
    protected int x;
    protected int y;
    protected int f, g;
    private Entity occupant;
    private boolean isStart;
    private boolean isGoal;
    protected boolean isPath;
    protected Cell parent;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.f = -1;
        this.isStart = false;
        this.isGoal = false;
        occupant = Entity.EMPTY;
    }

    public void setGoal(boolean isGoal) {
        this.isGoal = isGoal;
    }

    public boolean getIsStart() {
        return this.isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity entity) {
        if (occupant == Entity.EMPTY) {
        }
        occupant = entity;
    }

    public void removeOccupant() {
        occupant = Entity.EMPTY;
    }

    public boolean isTraversable() {
        return this.occupant == Entity.EMPTY;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    // how the cell is displayed in the console
    public String display() {
        String symbol = "";

        if (this.isPath)
            symbol = "*";

        if (this.isStart)
            symbol = "X";

        if (this.isGoal)
            symbol = "G";

        switch (occupant) {
            case EMPTY:
                // when occupant is empty, should not override the start/goal display
                if (symbol == "")
                    symbol = " ";
                break;
            case BARBER:
                symbol = "B";
                break;
            case SCANDRO:
                symbol = "S";
                break;
            case WALL:
                symbol = "W";
                break;
            default:
                // should be unreachable
                break;
        }
        return symbol;
    }

    @Override
    public String toString() {
        return String.format("Cell at %d, %d with occupant: %s, isPath: %b, isGoal: %b, isStart: %b, hasParent: %b", x,
                y, occupant,
                isPath, isGoal, isStart, hasParent());
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass())
            return false;
        Cell c = (Cell) o;

        return x == c.x && y == c.y && isStart == c.isStart && isGoal == c.isGoal && occupant == c.occupant;
    }
}
