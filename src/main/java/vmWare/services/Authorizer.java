package vmWare.services;

import org.springframework.stereotype.Service;
import vmWare.utils.UserCache;

import java.util.UUID;

@Service
public class Authorizer {

    private static UserCache cache = new UserCache();

    public String getToken(long ttl, String shopName, String user) {
        UUID uuid = UUID.randomUUID();
        cache.put(shopName + "-" + uuid, ttl, user);
        return shopName + "-" + uuid;
    }

    public boolean checkToken(String token, String user) {
        return cache.check(token, user);
    }

    public void removeToken(String token) {
        cache.remove(token);
    }

}
