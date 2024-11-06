import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class MordNotFull extends EntityMethods implements Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;
    private int health;

    public MordNotFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = 2;
        this.resourceCount = resourceCount;
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

public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
    if (this.position.adjacent(target.getPosition())) {

        world.removeEntity(scheduler, target);
        target.setHealth(target.getHealth()-1);
        resourceCount++;

        return true;
    } else {
        Point nextPos = this.nextPosition(world, target.getPosition(), position);

        if (!position.equals(nextPos)) {
            world.moveEntity(scheduler, this, nextPos);
        }
        return false;
        //return moveTo(world, target, scheduler, position);
    }
}

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            String img = "mordburr";
            if(this.id.equals("rigby")) {img = "rigburr";}

            Entity helper = Functions.createMordFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, imageStore.getImageList(img));

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity((Entity) helper);
            scheduler.scheduleActions((Entity) helper, world, imageStore);

            return true;
        }

        return false;
    }

    public void setHealth(int health){
        this.health = health;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Burrito.class)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            super.executeActivity(world, imageStore, scheduler, actionPeriod);
            //scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public double getActionPeriod() {return this.actionPeriod; }
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

    public int getHealth() {
        return health;
    }


}