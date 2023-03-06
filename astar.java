import java.io.File;

public class astar {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                    "ERROR: You must provide the map name as an arg: {cup, goaround, goaroundfake}\n" +
                            "eg. '$ java astar cup'");
            return;
        }

        run(args[0]);
    }

    /**
     * 
     * @param name the name of the files to read (eg. for mapcup.txt and
     *             barbercup.txt, name=cup)
     */
    public static void run(String name) {
        String directory = "InputFiles/";
        String mapPath = String.format("%smap%s.txt", directory, name);
        String barberPath = String.format("%sbarber%s.txt", directory, name);

        // fix possible spelling mistake
        File mapFile = new File(mapPath);
        if (!mapFile.exists()) {
            if (name.equals("goaround"))
                name = "goaraound";
            else if (name.equals("goaroundfake"))
                name = "goaraoundfake";

            mapPath = String.format("%smap%s.txt", directory, name);
        }

        LevelMap map = LevelMap.fromFile(mapPath);
        map.addBarberFile(barberPath);
        map.run();
    }
}