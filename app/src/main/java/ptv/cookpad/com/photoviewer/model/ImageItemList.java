package ptv.cookpad.com.photoviewer.model;

import java.util.List;

/**
 * Created by VS on 12/7/2016.
 */

public class ImageItemList {
    private List<ImageItem> items;
    private int total;



    public List<ImageItem> getItems() {
        return items;
    }

    public void setItems(List<ImageItem> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
