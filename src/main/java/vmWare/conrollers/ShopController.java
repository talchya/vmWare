package vmWare.conrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vmWare.models.basic.Owner;
import vmWare.services.ElasticOperator;

import java.util.List;

@RestController
public class ShopController {

    @Autowired
    ElasticOperator operator;

    @PostMapping("/shop/{shopName}")
    public void configureShop(@PathVariable String shopName, @RequestBody List<Owner> owners) {
        operator.setShop(shopName, owners);
    }

}
