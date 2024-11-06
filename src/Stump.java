import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Stump implements Entity {

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private int health;


    public Stump(String id, Point position, List<PImage> images, double actionPeriod,  int health) {

        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
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

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", this.getClass()));
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public double getAnimationPeriod() {

        throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", this.getClass()));

    }
    public double getActionPeriod() {return this.actionPeriod; }
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

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth() {
        return health;
    }


}