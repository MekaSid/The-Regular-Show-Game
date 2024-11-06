import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import processing.core.PImage;

public final class ImageStore {
    private Map<String, List<PImage>> images;
    private List<PImage> defaultImages;

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    public List<PImage> getImageList(String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }

    public Map<String, List<PImage>> getImages() {
        return images;
    }

    public void setImages(Map<String, List<PImage>> images) {
        this.images = images;
    }

    public List<PImage> getDefaultImages() {
        return defaultImages;
    }

    public void setDefaultImages(List<PImage> defaultImages) {
        this.defaultImages = defaultImages;
    }
}