package vmWare.conrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vmWare.models.basic.Owner;
import vmWare.models.requests.AuthorizationRequest;
import vmWare.models.requests.owner.ItemChangeRequest;
import vmWare.services.Authorizer;
import vmWare.services.ElasticOperator;

import java.util.List;
import java.util.Map;

@RestController
public class OwnerController {

    @Autowired
    Authorizer authorizer;
    @Autowired
    ElasticOperator operator;

    @PostMapping("/owners/{shopName}/login")
    public Object ownerLogin(@PathVariable String shopName, @RequestBody AuthorizationRequest request) {
        try{
            Owner owner = operator.getOwner(shopName, request.getUser());
            if (owner.getPassword().equals(request.getPassword())) {
                return authorizer.getToken(1000, shopName, request.getUser());
            }
        }catch (Exception e ) {
            return null;
        }
        return null;
    }

    @GetMapping("/owners/{shopName}/token")
    public Object checkLogin(@PathVariable String shopName, @RequestHeader Map<String, String> headers) {
        if (headers.containsKey("token") && headers.containsKey("user")) {
            return authorizer.checkToken(headers.get("token"), headers.get("user"));
        }
        return "token or user is missing";
    }

    @PostMapping("/owners/{shopName}/add")
    public Object add(@PathVariable String shopName, @RequestHeader Map<String, String> headers,
                      @RequestBody List<ItemChangeRequest> requestList) {
        if (headers.containsKey("token") && headers.containsKey("user")) {
            if (authorizer.checkToken(headers.get("token"), headers.get("user"))) {
                operator.addItems(requestList, shopName);
                return "done";
            }
            return "not authorized";
        }
        return "token or user is missing";
    }

    @PostMapping("/owners/{shopName}/update")
    public Object update(@PathVariable String shopName, @RequestHeader Map<String, String> headers,
                         @RequestBody List<ItemChangeRequest> requestList) {
        if (headers.containsKey("token") && headers.containsKey("user")) {
            if (authorizer.checkToken(headers.get("token"), headers.get("user"))) {
                operator.updateItems(requestList, shopName);
            }
            return "not authorized";
        }
        return "token or user is missing";
    }

    @PostMapping("/owners/{shopName}/remove")
    public Object remove(@PathVariable String shopName, @RequestHeader Map<String, String> headers,
                         @RequestBody List<ItemChangeRequest> requestList) {
        if (headers.containsKey("token") && headers.containsKey("user")) {
            if (authorizer.checkToken(headers.get("token"), headers.get("user"))) {
                operator.removeItems(requestList, shopName);
            }
            return "not authorized";
        }
        return "token or user is missing";
    }
}
