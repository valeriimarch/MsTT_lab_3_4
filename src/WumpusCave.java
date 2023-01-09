import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class WumpusCave {

    private final int caveXDimension; // starts bottom left -> right
    private final int caveYDimension; // starts bottom left ^ up

    private AgentPosition start = new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH);
    private Room wumpus;
    private Room gold;
    private final Set<Room> pits = new LinkedHashSet<>();

    private final Set<Room> allowedRooms;

    // Default Constructor. Create a Wumpus Case of default dimensions 4x4.
    public WumpusCave() {
        this(4, 4);
    }

    // Create a grid of rooms of dimensions x and y, representing the wumpus's cave.
    // @param caveXDimension - the cave's x dimension.
    // @param caveYDimension - the cave's y dimension.

    public WumpusCave(int caveXDimension, int caveYDimension) {
        if (caveXDimension < 1)
            throw new IllegalArgumentException("Cave must have x dimension >= 1");
        if (caveYDimension < 1)
            throw new IllegalArgumentException("Case must have y dimension >= 1");
        this.caveXDimension = caveXDimension;
        this.caveYDimension = caveYDimension;
        allowedRooms = getAllRooms();
    }

    // Create a grid of rooms of dimensions x and y, representing the wumpus's cave.
    // @param caveXDimension - the cave's x dimension.
    // @param caveYDimension - the cave's y dimension.
    // @param config - cave specification - two character per square (unfortunately a Wumpus can reside on top of a pit),
    // first line, then second line etc. Mapping: S=start, W=Wumpus, G=gold, P=pit.

    public WumpusCave(int caveXDimension, int caveYDimension, String config) {
        this(caveXDimension, caveYDimension);
        if (config.length() != 2 * caveXDimension * caveYDimension)
            throw new IllegalStateException("Wrong configuration length.");
        for (int i = 0; i < config.length(); i++) {
            char c = config.charAt(i);
            Room r = new Room(i / 2 % caveXDimension + 1, caveYDimension - i / 2 / caveXDimension);
            switch (c) {
                case 'S' -> start = new AgentPosition(r.getX(), r.getY(), AgentPosition.Orientation.FACING_NORTH);
                case 'W' -> wumpus = r;
                case 'G' -> gold = r;
                case 'P' -> pits.add(r);
            }
        }
    }

    // Limits possible movement within the cave (for search).
    // @param allowedRooms - the set of legal rooms that can be reached within the cave.
    public WumpusCave setAllowed(Set<Room> allowedRooms) {
        this.allowedRooms.clear();
        this.allowedRooms.addAll(allowedRooms);
        return this;
    }

    public void setWumpus(Room room) {
        wumpus = room;
    }

    public void setGold(Room room) {
        gold = room;
    }

    public void setPit(Room room, boolean b) {
        if (!b)
            pits.remove(room);
        else if (!room.equals(start.getRoom()) && !room.equals(gold))
            pits.add(room);
    }

    public int getCaveXDimension() {
        return caveXDimension;
    }

    public int getCaveYDimension() {
        return caveYDimension;
    }

    public AgentPosition getStart() {
        return start;
    }

    public Room getWumpus() {
        return wumpus;
    }

    public Room getGold() {
        return gold;
    }

    public boolean isPit(Room room) {
        return pits.contains(room);
    }

    public AgentPosition moveForward(AgentPosition position) {
        int x = position.getX();
        int y = position.getY();
        switch (position.getOrientation()) {
            case FACING_NORTH -> y++;
            case FACING_SOUTH -> y--;
            case FACING_EAST -> x++;
            case FACING_WEST -> x--;
        }
        Room room = new Room(x, y);
        start = allowedRooms.contains(room) ? new AgentPosition(x, y, position.getOrientation()) : position;
        return start;
    }

    public AgentPosition turnLeft(AgentPosition position) {
        AgentPosition.Orientation orientation = switch (position.getOrientation()) {
            case FACING_NORTH -> AgentPosition.Orientation.FACING_WEST;
            case FACING_SOUTH -> AgentPosition.Orientation.FACING_EAST;
            case FACING_EAST -> AgentPosition.Orientation.FACING_NORTH;
            case FACING_WEST -> AgentPosition.Orientation.FACING_SOUTH;
        };
        start = new AgentPosition(position.getX(), position.getY(), orientation);
        return start;
    }

    public AgentPosition turnRight(AgentPosition position) {
        AgentPosition.Orientation orientation = switch (position.getOrientation()) {
            case FACING_NORTH -> AgentPosition.Orientation.FACING_EAST;
            case FACING_SOUTH -> AgentPosition.Orientation.FACING_WEST;
            case FACING_EAST -> AgentPosition.Orientation.FACING_SOUTH;
            case FACING_WEST -> AgentPosition.Orientation.FACING_NORTH;
        };
        start = new AgentPosition(position.getX(), position.getY(), orientation);
        return start;
    }

    public Set<Room> getAllRooms() {
        Set<Room> allowedRooms = new HashSet<>();
        for (int x = 1; x <= caveXDimension; x++)
            for (int y = 1; y <= caveYDimension; y++)
                allowedRooms.add(new Room(x, y));
        return allowedRooms;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = caveYDimension; y >= 1; y--) {
            for (int x = 1; x <= caveXDimension; x++) {
                Room r = new Room(x, y);
                String txt = "";
                if (r.equals(start.getRoom()))
                    txt += "S";
                if (r.equals(gold))
                    txt += "G";
                if (r.equals(wumpus))
                    txt += "W";
                if (isPit(r))
                    txt += "P";

                if (txt.isEmpty())
                    txt = ". ";
                else if (txt.length() == 1)
                    txt += " ";
                else if ( txt.length() > 2) // cannot represent...
                    txt = txt.substring(0, 2);
                builder.append(txt);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}