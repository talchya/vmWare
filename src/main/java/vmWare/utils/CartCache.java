package vmWare.utils;

import vmWare.models.basic.Item;
import vmWare.models.basic.QuantityDiscount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartCache {

    private final HashMap<String, Object[]> cache = new HashMap<>();

    public void put(String key, long timeToLive) {
        timeToLive = System.currentTimeMillis() + timeToLive * 1000;
        if(key == null) throw new RuntimeException("Key cannot be null!");
        cache.put(key, new Object[]{timeToLive, new ArrayList<Item>(), 0});
    }

    public double update(String key, Item item, QuantityDiscount discount) {
        if (cache.containsKey(key)) {
            Long expires = (Long) cache.get(key)[0];
            if (expires - System.currentTimeMillis() > 0) {
                ((List<Item>)cache.get(key)[1]).add(item);
                if(discount == null) {
                    cache.get(key)[2] = (double) cache.get(key)[2] + item.getPrice() * item.getQuantity();
                } else {
                    int discountQuantity = item.getQuantity()/discount.getQuantity();
                    int withoutDiscountQuantity = item.getQuantity()/discount.getQuantity();
                    cache.get(key)[2] = (double) cache.get(key)[2] + discount.getPrice() * discountQuantity
                            + item.getPrice() * withoutDiscountQuantity;
                }
                return (double) cache.get(key)[2];
            } else {
                cache.remove(key);
                return -1;
            }
        }
        return -1;
    }

    public double get(String key) {
        if (cache.containsKey(key)) {
            Long expires = (Long) cache.get(key)[0];
            if (expires - System.currentTimeMillis() > 0) {
                return (double) cache.get(key)[2];
            } else {
                cache.remove(key);
                return -1;
            }
        }
        return -1;
    }

    public void remove(String key) {
        cache.remove(key);
    }

}
