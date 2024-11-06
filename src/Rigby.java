import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Rigby extends EntityMethods implements Entity{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;
    private int health;


    public Rigby(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
    }
    public void tryAddEntity(WorldModel world) {
        if (world.isOccupied(this.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }
        world.addEntity(this);

    }



    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler, ImageStore imageStore) {
        if (this.position.adjacent(target.getPosition())) {

            world.removeEntity(scheduler, target);

            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + target.getId(), target.getPosition(), imageStore.getImageList(Functions.STUMP_KEY));
            world.addEntity(stump);


            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition(), position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }
    public void transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        String x = "rigbygood";
        Entity helper = Functions.createMordNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, 2, imageStore.getImageList(x));

        world.removeEntity(scheduler, this);

        world.addEntity(helper);
        scheduler.scheduleActions(helper, world, imageStore);
    }

    public boolean moveToHouse(WorldModel world, Entity target, EventScheduler scheduler, ImageStore imageStore) {
        if (this.position.adjacent(target.getPosition())) {
            target.setHealth(target.getHealth()-4);

            this.transform(world, scheduler, imageStore);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition(), position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Tree.class)));
        Optional<Entity> houseTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

        if(houseTarget.get().getHealth() >= 4){
            this.moveToHouse(world, houseTarget.get(), scheduler, imageStore);
        }
        else if(fairyTarget.isPresent()) {

            this.moveTo(world, fairyTarget.get(), scheduler, imageStore);
        }
        super.executeActivity(world, imageStore, scheduler, actionPeriod);

    }



    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }
    public PImage getCurrentImage() {

        return this.images.get(this.imageIndex % this.images.size());

    }
    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }


    public String getId() {
        return id;
    }


    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getActionPeriod() {return this.actionPeriod; }


    public int getHealth() {
        return health;
    }
    public void setHealth(int health){
        this.health = health;
    }


}