/**
 * An action that can be taken by an entity
 */
public final class Animation implements Action{
    private Entity entity;
    private int repeatCount;

    public Animation( Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }



    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, Functions.createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }



}