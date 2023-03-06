import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LevelMap {
    private static final boolean DEBUG_MODE = false;
    private int currentTimestep;

    private int sizeX;
    private int sizeY;

    private Cell[][] cells;
    private Map<Integer, List<Cell>> barbers; // map with key = timestamp, value is locations of barbers
    private List<Cell> walls;
    private Cell scandro; // location of Scandro
    private Cell start; // location of start
    private Cell end; // location of end
    private boolean pathNotFound;

    static LevelMap fromFile(String path) {
        File file = new File(path);
        int sizeX = 0;
        int sizeY = 0;
        Cell start = null;
        Cell end = null;
        List<Cell> walls = new ArrayList<Cell>();

        try {
            // read entire file
            Scanner sc = new Scanner(file);
            String c;
            int x, y;
            while (sc.hasNextLine()) {

                c = sc.next();

                // to get map size
                if (c.equals("M")) {
                    sizeY = sc.nextInt();
                    sizeX = sc.nextInt();
                }

                // to get starting cell
                else if (c.equals("S")) {
                    y = sc.nextInt();
                    x = sc.nextInt();
                    start = new Cell(x, y);
                    start.setStart(true);
                }
                // to get goal cell
                else if (c.equals("G")) {
                    y = sc.nextInt();
                    x = sc.nextInt();
                    end = new Cell(x, y);
                    end.setGoal(true);
                }
                // to get walls
                else if (c.equals("W")) {
                    y = sc.nextInt();
                    x = sc.nextInt();
                    Cell cell = new Cell(x, y);
                    cell.setOccupant(Entity.WALL);
                    walls.add(cell);
                } else if (c.equals("E")) {
                    break;
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (DEBUG_MODE)
            System.out.println("Data successfully read from map file...");

        return new LevelMap(sizeX, sizeY, start, end, walls);
    }

    public LevelMap(int sizeX, int sizeY, Cell start, Cell end, List<Cell> walls) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.start = start;
        this.end = end;
        this.walls = walls;
        this.cells = new Cell[sizeX][sizeY];
        this.currentTimestep = 0;

        // initialize cell objects
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }

        // set start, end cells
        cells[start.x][start.y] = start;
        cells[end.x][end.y] = end;

        // set walls
        for (Cell wall : walls) {
            cells[wall.x][wall.y] = wall;
        }

        if (DEBUG_MODE)
            System.out.println("LevelMap successfully created...");
    }

    // load the barber file (convert file data to Map<timestep, List<Cells>>)
    public void addBarberFile(String path) {
        File file = new File(path);
        barbers = new HashMap<Integer, List<Cell>>();

        try {
            // read entire file
            Scanner sc = new Scanner(file);
            int timestep, x, y;
            while (sc.hasNextLine()) {

                // get timestep that a specific barber is at
                timestep = sc.nextInt();
                if (timestep == -1)
                    break;

                // get location of the barber
                y = sc.nextInt();
                x = sc.nextInt();

                // create cell
                Cell barber = new Cell(x, y);
                barber.setOccupant(Entity.BARBER);

                // initialize list at timestep if it isn't already
                if (!barbers.containsKey(timestep)) {
                    List<Cell> list = new ArrayList<Cell>();
                    barbers.put(timestep, list);
                }

                // append barber list in map at key=timestep
                barbers.get(timestep).add(barber);

            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        }

        if (DEBUG_MODE)
            System.out.println("Barber file successfully loaded...");
    }

    // sets the barbers locaitons for the current step
    private void setBarbers() {

        // remove old barbers
        List<Cell> currentBarbers = barbers.get(currentTimestep - 1);

        // if barbers were found in the previous time step, remove them from grid
        if ((currentBarbers == null)) {
            if (DEBUG_MODE)
                System.out.println("No barbers to remove from the previous step.");
        } else {
            for (Cell barber : currentBarbers) {
                cells[barber.x][barber.y].removeOccupant();
            }

            if (DEBUG_MODE)
                System.out.println("Barbers removed from timestep " + (currentTimestep - 1));
        }

        // get the list of barbers at current timestep
        currentBarbers = barbers.get(currentTimestep);
        if (currentBarbers == null) {
            if (DEBUG_MODE)
                System.out.println("No barbers at this timestep");
            return;
        }

        // add barbers to grid
        for (Cell barber : currentBarbers) {
            cells[barber.x][barber.y] = barber;
        }

        if (DEBUG_MODE)
            System.out.println("Barbers loaded for timestep " + currentTimestep);
    }

    // print the current map's state
    private void print() {

        // print current timestep
        System.out.println("Step: " + currentTimestep);

        // print horizontal bar above
        System.out.println("-".repeat(cells[0].length * 2 + 3));

        // print | on sides of map, along with all the cells inside
        // individual cells have a display() method that defines what they should print
        for (int i = 0; i < cells.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < cells[0].length; j++) {
                System.out.print(cells[i][j].display() + " ");
            }
            System.out.println("|");
        }

        // print horizontal bar below
        System.out.println("-".repeat(cells[0].length * 2 + 3) + "\n\n");

    }

    private void astar() {

        // initialize the open list with starting node
        List<Cell> openList = new ArrayList<Cell>();
        Cell currCell = scandro;
        currCell.f = 0;
        openList.add(currCell);

        // initialize the closed list
        boolean[][] closedList = new boolean[sizeX][sizeY];

        // while the open list is not empty
        while (!openList.isEmpty()) {

            // find node with lowest f in open list "q"
            Cell q = openList.get(0);
            for (Cell c : openList) {
                if (c.f < q.f) {
                    q = c;
                }
            }

            // pop q off open list
            openList.remove(q);
            closedList[q.x][q.y] = true;

            // generate q's 4 successors and set their parents to q
            double gNew, hNew, fNew;
            Cell up = new Cell(q.x, q.y + 1);
            up.parent = q;
            Cell down = new Cell(q.x, q.y - 1);
            down.parent = q;
            Cell right = new Cell(q.x + 1, q.y);
            right.parent = q;
            Cell left = new Cell(q.x - 1, q.y);
            left.parent = q;

            // check cell above
            if (isValid(up)) {
                // if it is the destination
                if (isDestination(up)) {
                    if (DEBUG_MODE)
                        System.out.println("Destination found.");
                    up.setGoal(true);
                    cells[up.x][up.y] = up;
                    setPath();
                    return;
                }
                // if it is not on the closed list and is not blocked by wall/barber
                else if (!closedList[up.x][up.y] && cells[up.x][up.y].isTraversable()) {
                    gNew = q.g + 1;
                    hNew = calculateHValue(up);
                    fNew = gNew + hNew;

                    // if the new f value is better than a previously found f value
                    // (defaut f is -1, so always do this on new cells)
                    if (cells[up.x][up.y].f < fNew) {
                        openList.add(up);

                        if (cells[up.x][up.y].getIsStart())
                            up.setStart(true);

                        // apply new f,g,h, and parent to the grid cell
                        cells[up.x][up.y] = up;

                    }
                }
                // else means the cell is already on the closed list, or it is blocked, nothing
                // needs to be done
            }
            // check cell below
            if (isValid(down)) {
                // if it is the destination
                if (isDestination(down)) {
                    if (DEBUG_MODE)
                        System.out.println("Destination found.");
                    down.setGoal(true);
                    cells[down.x][down.y] = down;
                    setPath();
                    return;
                }
                // if it is not on the closed list and is not blocked by wall/barber
                else if (!closedList[down.x][down.y] && cells[down.x][down.y].isTraversable()) {
                    gNew = q.g + 1;
                    hNew = calculateHValue(down);
                    fNew = gNew + hNew;

                    // if the new f value is better than a previously found f value
                    // (defaut f is -1, so always do this on new cells)
                    if (cells[down.x][down.y].f < fNew) {
                        openList.add(down);

                        if (cells[down.x][down.y].getIsStart())
                            down.setStart(true);

                        // apply new f,g,h, and parent to the grid cell
                        cells[down.x][down.y] = down;
                    }
                }
                // else means the cell is already on the closed list, or it is blocked, nothing
                // needs to be done
            }
            // check cell to the right
            if (isValid(right)) {
                // if it is the destination
                if (isDestination(right)) {
                    if (DEBUG_MODE)
                        System.out.println("Destination found.");
                    right.setGoal(true);
                    cells[right.x][right.y] = right;
                    setPath();
                    return;
                }
                // if it is not on the closed list and is not blocked by wall/barber
                else if (!closedList[right.x][right.y] && cells[right.x][right.y].isTraversable()) {
                    gNew = q.g + 1;
                    hNew = calculateHValue(right);
                    fNew = gNew + hNew;

                    // if the new f value is better than a previously found f value
                    // (defaut f is -1, so always do this on new cells)
                    if (cells[right.x][right.y].f < fNew) {
                        openList.add(right);

                        if (cells[right.x][right.y].getIsStart())
                            right.setStart(true);

                        // apply new f,g,h, and parent to the grid cell
                        cells[right.x][right.y] = right;
                    }
                }
                // else means the cell is already on the closed list, or it is blocked, nothing
                // needs to be done
            }
            // check cell to the left
            if (isValid(left)) {
                // if it is the destination
                if (isDestination(left)) {
                    if (DEBUG_MODE)
                        System.out.println("Destination found.");
                    left.setGoal(true);
                    cells[left.x][left.y] = left;
                    setPath();
                    return;
                }
                // if it is not on the closed list and is not blocked by wall/barber
                else if (!closedList[left.x][left.y] && cells[left.x][left.y].isTraversable()) {
                    gNew = q.g + 1;
                    hNew = calculateHValue(left);
                    fNew = gNew + hNew;

                    // if the new f value is better than a previously found f value
                    // (defaut f is -1, so always do this on new cells)
                    if (cells[left.x][left.y].f < fNew) {
                        openList.add(left);

                        if (cells[left.x][left.y].getIsStart())
                            left.setStart(true);

                        // apply new f,g,h, and parent to the grid cell
                        cells[left.x][left.y] = left;
                    }
                }
                // else means the cell is already on the closed list, or it is blocked, nothing
                // needs to be done
            }

        }

        pathNotFound = true;
        return;

    }

    private boolean isDestination(Cell c) {
        return (c.x == end.x && c.y == end.y);
    }

    private boolean isValid(Cell c) {
        return (c.x >= 0 && c.x < sizeX && c.y >= 0 && c.y < sizeY);
    }

    private int calculateHValue(Cell c) {
        return Math.abs(c.x - end.x) + Math.abs(c.y - end.y);
    }

    // apply the current path object to the map
    private void setPath() {
        Cell currCell = cells[end.x][end.y];
        while (currCell.hasParent()) {
            currCell.isPath = true;
            currCell = cells[currCell.parent.x][currCell.parent.y];
        }
    }

    // clear isPath status from all cells, along with f,g, parent values
    private void resetBoard() {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cells[i][j].isPath = false;
                cells[i][j].f = -1;
                cells[i][j].g = -1;
                cells[i][j].parent = null;
            }
        }
    }

    private void moveScandro() {
        try {
            Cell up = cells[scandro.x][scandro.y + 1];
            if (up.isPath) {
                cells[scandro.x][scandro.y].setOccupant(Entity.EMPTY);
                cells[up.x][up.y].setOccupant(Entity.SCANDRO);
                scandro = up;
                return;
            }
        } catch (Exception e) {
        }
        try {
            Cell down = cells[scandro.x][scandro.y - 1];
            if (down.isPath) {
                cells[scandro.x][scandro.y].setOccupant(Entity.EMPTY);
                cells[down.x][down.y].setOccupant(Entity.SCANDRO);
                scandro = down;
                return;
            }
        } catch (Exception e) {
        }
        try {
            Cell right = cells[scandro.x + 1][scandro.y];
            if (right.isPath) {
                cells[scandro.x][scandro.y].setOccupant(Entity.EMPTY);
                cells[right.x][right.y].setOccupant(Entity.SCANDRO);
                scandro = right;
                return;
            }
        } catch (Exception e) {
        }
        try {
            Cell left = cells[scandro.x - 1][scandro.y];
            if (left.isPath) {
                cells[scandro.x][scandro.y].setOccupant(Entity.EMPTY);
                cells[left.x][left.y].setOccupant(Entity.SCANDRO);
                scandro = left;
                return;
            }
        } catch (Exception e) {
        }
    }

    // run the entire simulation, stepping until finished and printing map after
    // each step
    void run() {
        System.out.println("");

        // initialize things and print map
        scandro = start;
        cells[start.x][start.y] = scandro;
        setBarbers();
        print();

        // sets barbers, scandro at timestep i and print the map
        while (!pathNotFound && !(scandro.x == end.x && scandro.y == end.y)) {
            currentTimestep++;
            resetBoard();
            setBarbers();
            astar();
            moveScandro();
            print();
        }

        if (pathNotFound) {
            System.out.println("NO PATH");
        }
        // loop
        // step
        // print()
    }
}
