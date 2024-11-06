import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {

    void tryAddEntity(WorldModel world);

    void nextImage();

    PImage getCurrentImage();

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */



    String log();

    double getActionPeriod();

    String getId();

    Point getPosition();

    void setPosition(Point position);

    void setHealth(int health);

    int getHealth();

    double getAnimationPeriod();
    /**
     +tryAddEntity(): void
     +getAnimationPeriod(): double
     +getHealth(): int
     +setHealth(): void
     +setPosition(): void
     +getPosition(): Position
     +getId(): String
     +getActionPeriod: double
     +log(): String
     +executeActivity(): void
     +getCurrentImage(): PImage
     +nextImage(): void

     */

}