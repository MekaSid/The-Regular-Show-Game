import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class MordFull extends EntityMethods implements Entity{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private double actionPeriod;
    private double animationPeriod;
    private int health;

    public MordFull(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = 2;
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
            target.setHealth(target.getHealth()+2);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition(), position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, (Entity) this, nextPos);
            }
            return false;
        }
    }

    public double getActionPeriod() {return this.actionPeriod; }

    public void setHealth(int health){
        this.health = health;
    }
    public void transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {

        String img = "mord";
        if(this.id.equals("rigby")){ img = "rigbygood";}


        Entity mord = Functions.createMordNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, imageStore.getImageList(img));

        world.removeEntity(scheduler, this);

        world.addEntity(mord);
        scheduler.scheduleActions(mord, world, imageStore);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));
        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        } else {
            super.executeActivity(world, imageStore, scheduler, actionPeriod);
            //scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
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


    public int getHealth() {
        return health;
    }


}