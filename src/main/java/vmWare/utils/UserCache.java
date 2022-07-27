package vmWare.utils;

import java.util.HashMap;

public class UserCache {

    private final HashMap<String, Object[]> cache = new HashMap<>();

    public void put(String key, long timeToLive, String user) {
        timeToLive = System.currentTimeMillis() + timeToLive * 1000;
        if(key == null) throw new RuntimeException("Key cannot be null!");
        cache.put(key, new Object[]{timeToLive, user});
    }

    public boolean check(String key, String user) {
        if (cache.containsKey(key)) {
            Long expires = (Long) cache.get(key)[0];
            if (expires - System.currentTimeMillis() > 0 &&
                    (cache.get(key)[1]).equals(user)) {
                return true;
            } else {
                cache.remove(key);
                return false;
            }
        }
        return false;
    }

    public void remove(String key) {
        cache.remove(key);
    }
}
