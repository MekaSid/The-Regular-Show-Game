import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler {
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double currentTime;

    public EventScheduler() {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.currentTime = 0;
    }

    public void scheduleActions(Entity entity, WorldModel world, ImageStore imageStore) {
        if (entity.getClass().equals(MordFull.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        } else if (entity.getClass().equals(MordNotFull.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        } else if (entity.getClass().equals(Obstacle.class)) {
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        } else if (entity.getClass().equals(Rigby.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        } else if (entity.getClass().equals(Sapling.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        } else if (entity.getClass().equals(Tree.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        }else if (entity.getClass().equals(Burrito.class)) {
            scheduleEvent(entity, Functions.createActivityAction(entity, world, imageStore), entity.getActionPeriod());
            scheduleEvent(entity, Functions.createAnimationAction(entity, 0), entity.getAnimationPeriod());
        }
    }

    public void updateOnTime(double time) {
        double stopTime = this.currentTime + time;
        while (!this.eventQueue.isEmpty() && this.eventQueue.peek().getTime() <= stopTime) {
            Event next = this.eventQueue.poll();
            removePendingEvent(next);
            this.currentTime = next.getTime();
            next.getAction().executeAction(this);
        }
        this.currentTime = stopTime;
    }

    public void removePendingEvent(Event event) {
        List<Event> pending = this.pendingEvents.get(event.getEntity());

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void unscheduleAllEvents(Entity entity) {
        List<Event> pending = this.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                this.eventQueue.remove(event);
            }
        }
    }

    public void scheduleEvent(Entity entity, Action action, double afterPeriod) {
        double time = this.currentTime + afterPeriod;

        Event event = new Event(action, time, entity);

        this.eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = this.pendingEvents.getOrDefault(entity, new LinkedList<>());
        pending.add(event);
        this.pendingEvents.put(entity, pending);
    }

    public PriorityQueue<Event> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(PriorityQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    public Map<Entity, List<Event>> getPendingEvents() {
        return pendingEvents;
    }

    public void setPendingEvents(Map<Entity, List<Event>> pendingEvents) {
        this.pendingEvents = pendingEvents;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }
}