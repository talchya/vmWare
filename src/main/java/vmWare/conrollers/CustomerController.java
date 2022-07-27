package vmWare.conrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vmWare.models.basic.Customer;
import vmWare.models.requests.AuthorizationRequest;
import vmWare.models.requests.customer.BuyItemRequest;
import vmWare.services.Authorizer;
import vmWare.services.CustomerOperator;
import vmWare.services.ElasticOperator;

import java.util.Map;

public class CustomerController {

    @Autowired
    Authorizer authorizer;
    @Autowired
    ElasticOperator operator;
    @Autowired
    CustomerOperator customerOperator;

    @PostMapping("/customer/{shopName}/signup")
    public Object customerSignUP(@PathVariable String shopName, @RequestBody Customer customer) {
        try{
            operator.createCustomer(shopName, customer);
            return "create succesfully";
        }catch (Exception e) {
            return "error occured";
        }
    }

    @PostMapping("/customer/{shopName}/login")
    public Object customerLogin(@PathVariable String shopName, @RequestBody AuthorizationRequest request) {
        try{
            Customer customer = operator.getCustomer(shopName, request.getUser());
            if (customer.getPassword().equals(request.getPassword())) {
                String token = authorizer.getToken(1000000, shopName, request.getUser());
                customerOperator.createCart(token, 1000000);
                return token;
            }
            return "password is incorrect";
        }catch (Exception e ) {
            return "error occured";
        }
    }

    @GetMapping("/customer/{shopName}/token")
    public Object checkLogin(@PathVariable String shopName, @RequestHeader Map<String, String> headers) {
        if (headers.containsKey("token") && headers.containsKey("user")) {
            return authorizer.checkToken(headers.get("token"), headers.get("user"));
        }
        return "token or user is missing";
    }

    @PostMapping("/customer/{shopName}/add")
    public Object addToCart(@PathVariable String shopName, @RequestHeader Map<String, String> headers,
                            @RequestBody BuyItemRequest request) {
        if (headers.containsKey("token") && headers.containsKey("user") &&
                authorizer.checkToken(headers.get("token"), headers.get("user"))) {
            return customerOperator.addToCart(headers.get("token"), shopName, request);
        }
        return "token or user is missing";
    }

    @PostMapping("/customer/{shopName}/pay")
    public Object pay(@PathVariable String shopName, @RequestHeader Map<String, String> headers) {
        if (headers.containsKey("token") && headers.containsKey("user") &&
                authorizer.checkToken(headers.get("token"), headers.get("user"))) {
            return customerOperator.pay(headers.get("token"), operator.getCustomer(shopName, headers.get("user")));
        }
        return "token or user is missing";
    }
}
