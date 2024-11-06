import java.awt.*;
import java.util.List;

public abstract class EntityMethods {


    public Point nextPosition(WorldModel world, Point destPos, Point position) {
        PathingStrategy strat = new AStarPathingStrategy();
        List <Point> path;
        if (this.getClass() == Rigby.class) {
            path = strat.computePath(position, destPos,
                    x -> (!(world.isOccupied(x)) && world.withinBounds(x)),
                    (y, z) -> (z.adjacent(y)),
                    PathingStrategy.CARDINAL_NEIGHBORS
            );
            if (path == null) {
                return position;
            } else if (path.size() == 0) {
                return position;
            }
            return path.get(0);
        } else {
            path = strat.computePath(position,
                    destPos, x -> ((world.isOccupied(x)
                            && world.getOccupancyCell(x).getClass() == Stump.class && world.withinBounds(x))
                            || !(world.isOccupied(x)) && world.withinBounds(x))
                    ,
                    (z, y) -> (z.adjacent(y))
                    ,
                    PathingStrategy.CARDINAL_NEIGHBORS
            );
            if (path == null) {
                return position;
            } else if (path.size() == 0) {
                return position;
            }
            return path.get(0);

        }
    }

//        int horiz = Integer.signum(destPos.x - position.x);
//        Point newPos = new Point(position.x + horiz, position.y);
//
//        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
//            int vert = Integer.signum(destPos.y - position.y);
//            newPos = new Point(position.x, position.y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
//                newPos = position;
//            }
//        }
//
//        return newPos;

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler, Point position){
        Point nextPos = this.nextPosition(world, target.getPosition(), position);

        if (!position.equals(nextPos)) {
            world.moveEntity(scheduler, (Entity) this, nextPos);
        }
        return false;

    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler, double actionPeriod){
        scheduler.scheduleEvent((Entity) this, Functions.createActivityAction((Entity) this, world, imageStore), actionPeriod);
    }


}
