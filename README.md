# AStarPathFinder
A Java console application that demonstrates the a star path finding algorithm

### Description of app

- Super Scandro is a hero in video game with long flowing rock star hair.    
- Super Scandro starts at his garage and his goal is to get to the Justin Bieber concert.  
- The map consists of obstacles blocking Super Scandro where he cannot proceed (walls).  
- Additionally, there are barbers on the map that will cut his long flowing hair if he gets too close.  
- Your job is to find the short path that will not get Super Scandro's hair cut.  
- Super Scandro must therefore avoid barbers and walls.  
- The obstacles (walls) on the map will not change through out the simulation.  
- However,  The barbers can move about the screen through time.

--- 

### To run the app

With Java installed, simply clone the repository and run the astar.java file by executing the following commands:
`javac *.java`
`java astar`

---

### Map Files

The input files (map data) can be found in the InputFiles directory.

Map data follows a specific format: 
- M- Map Size followed by X,Y coordinate:   Eg:  M 6 8
- S- Start location followed by X,Y coordinate: Eg: S 2 3
- W- Wall followed by XY coordinate:             Eg:  W 2 3
- E- End of file
- All X Y coordinates start at index of 0.  The smallest X coordinate is at the left of the screen.  The smallest Y coordinate is at the top of the screen.

an example file looks like:
```
M NumberOfX NumberOfY
S X Y
G X Y
W X Y
W X Y
W X Y
...
E
```

---

### Output


These maps, as well as the path and position of everything is visualized in the OUTPUT.txt file

This file is separated into a few parts, each showing the results of a different input map.

For the map visualizations:
- '-' and '|' represent the map boundaries
- X is the starting position
- S is Scandro's current location
- '*' is the path that was found by A* which Scandro will move along
- 'W' are walls
- 'B' are barbers (basically walls that may move)
- 'G' is the goal.
