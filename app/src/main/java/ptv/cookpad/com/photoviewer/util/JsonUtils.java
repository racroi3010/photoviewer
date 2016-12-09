package ptv.cookpad.com.photoviewer.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ptv.cookpad.com.photoviewer.model.ImageItem;
import ptv.cookpad.com.photoviewer.model.ImageItemList;

/**
 * Created by VS on 12/7/2016.
 */

public class JsonUtils {
    public static ImageItemList parseJson(String json) throws JSONException {
        ImageItemList rs = new ImageItemList();
        List<ImageItem> items = new ArrayList<ImageItem>();
        if(json == null || json.isEmpty()) return rs;
        JSONObject jsonObject = new JSONObject(json);

        JSONObject jsonQuery = jsonObject.optJSONObject(Constants.JSON_QUERY);
        if(jsonQuery != null){
            JSONArray jsonRequest = jsonQuery.optJSONArray(Constants.JSON_REQUEST);
            if(jsonRequest != null){
                int total = jsonRequest.getJSONObject(0).getInt(Constants.JSON_TOTAL);
                rs.setTotal(total);
            }
        }

        JSONArray jsonItems = jsonObject.optJSONArray(Constants.JSON_ITEM);
        if(jsonItems != null){
            for(int i = 0; i < jsonItems.length(); i ++){
                JSONObject jsonItem = jsonItems.getJSONObject(i);
                JSONObject jsonPage = jsonItem.getJSONObject(Constants.JSON_PAGE);
                JSONArray jsonThumb = jsonPage.optJSONArray(Constants.JSON_THUMB);

                String thumb = null, url = null;

                if(jsonThumb != null && jsonThumb.length() > 0){
                    thumb = jsonThumb.getJSONObject(0).getString(Constants.JSON_SRC);
                }
                JSONArray jsonImage = jsonPage.optJSONArray(Constants.JSON_IMGOBJ);
                if(jsonImage != null && jsonImage.length() > 0){
                    url = jsonImage.getJSONObject(0).getString(Constants.JSON_SRC);
                }

                if(thumb == null || thumb.isEmpty()){
                    thumb = url;
                }

                if(thumb != null && !thumb.isEmpty() && url != null && !url.isEmpty()){
                    ImageItem item = new ImageItem();
                    item.setThumb(thumb);
                    item.setPath(url);
                    items.add(item);
                }



            }
        }

        rs.setItems(items);

        return rs;
    }
}
