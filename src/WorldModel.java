import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {

    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> parseEntity(line, imageStore);
                }
            }
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        this.parseSaveFile(saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        removeEntityAt(entity.getPosition());
    }

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Functions.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Functions.ENTITY_NUM_PROPERTIES) {
            String key = properties[Functions.PROPERTY_KEY];
            String id = properties[Functions.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Functions.PROPERTY_COL]), Integer.parseInt(properties[Functions.PROPERTY_ROW]));

            properties = properties.length == Functions.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Functions.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Functions.BURRITO_KEY -> parseBurrito(properties, pt, id, imageStore);

                case Functions.OBSTACLE_KEY -> parseObstacle(properties, pt, id, imageStore);
                case Functions.MORD_KEY -> parseMord(properties, pt, id, imageStore);
                case Functions.RIGBY_KEY -> parseRigby(properties, pt, id, imageStore);
                case Functions.HOUSE_KEY -> parseHouse(properties, pt, id, imageStore);
                case Functions.TREE_KEY -> parseTree(properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> parseSapling(properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.y][pos.x] = background;
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (withinBounds(pos)) {
            return Optional.of(getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.y][pos.x];
    }

    public void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.y][pos.x] = entity;
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

//    public void tryAddEntity(Entity entity) {
//        if (isOccupied(entity.getPosition())) {
//            // arguably the wrong type of exception, but we are not
//            // defining our own exceptions yet
//            throw new IllegalArgumentException("position occupied");
//        }
//
//        this.addEntity(entity);
//    }

    public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return Functions.nearestEntity(ofType, pos);
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.STUMP_NUM_PROPERTIES) {
            Entity entity = Functions.createStump(id, pt, imageStore.getImageList(Functions.STUMP_KEY));
            //this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.STUMP_KEY, Functions.STUMP_NUM_PROPERTIES));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.HOUSE_NUM_PROPERTIES) {
            Entity entity = Functions.createHouse(id, pt, imageStore.getImageList(Functions.HOUSE_KEY));
            //this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.HOUSE_KEY, Functions.HOUSE_NUM_PROPERTIES));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
            Entity entity = Functions.createObstacle(id, pt, Double.parseDouble(properties[Functions.OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(Functions.OBSTACLE_KEY));
            //this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.OBSTACLE_KEY, Functions.OBSTACLE_NUM_PROPERTIES));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.TREE_NUM_PROPERTIES) {
            Entity entity = Functions.createTree(id, pt, Double.parseDouble(properties[Functions.TREE_ACTION_PERIOD]), Double.parseDouble(properties[Functions.TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[Functions.TREE_HEALTH]), imageStore.getImageList(Functions.TREE_KEY));
            //this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.TREE_KEY, Functions.TREE_NUM_PROPERTIES));
        }
    }

    public void parseBurrito(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.BURRITO_NUM_PROPERTIES) {
            Entity entity = Functions.createStump(id, pt, imageStore.getImageList(Functions.BURRITO_KEY));
            //this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.BURRITO_KEY, Functions.BURRITO_NUM_PROPERTIES));
        }
    }


    public void parseRigby(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.RIGBY_NUM_PROPERTIES) {
            Entity entity = Functions.createRigby(id, pt, Double.parseDouble(properties[Functions.RIGBY_ACTION_PERIOD]), Double.parseDouble(properties[Functions.RIGBY_ANIMATION_PERIOD]), imageStore.getImageList(Functions.RIGBY_KEY));
            // this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.RIGBY_KEY, Functions.RIGBY_NUM_PROPERTIES));
        }
    }



    public void parseMord(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.MORD_NUM_PROPERTIES) {
            Entity entity = Functions.createMordNotFull(id, pt, Double.parseDouble(properties[Functions.MORD_ACTION_PERIOD]), Double.parseDouble(properties[Functions.MORD_ANIMATION_PERIOD]), Integer.parseInt(properties[Functions.MORD_LIMIT]), imageStore.getImageList(Functions.MORD_KEY));
//            this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.MORD_KEY, Functions.MORD_NUM_PROPERTIES));
        }
    }

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[Functions.SAPLING_HEALTH]);
            Entity entity = Functions.createSapling(id, pt, imageStore.getImageList(Functions.SAPLING_KEY), health);
            // this.tryAddEntity(entity);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.SAPLING_KEY, Functions.SAPLING_NUM_PROPERTIES));
        }
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public int getNumRows() {
        return numRows;
    }



    public int getNumCols() {
        return numCols;
    }


    public Set<Entity> getEntities() {
        return entities;
    }


}