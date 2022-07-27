package vmWare.conrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vmWare.services.ElasticOperator;

@RestController
public class CatalogController {

    @Autowired
    ElasticOperator operator;

    @GetMapping("/catalogs/{shopName}")
    public Object getCatalogs(@PathVariable String shopName) {
        return operator.getCatalogs(shopName);
    }

    @GetMapping("/catalog/{shopName}/{catalog}")
    public Object getCatalog(@PathVariable String shopName, @PathVariable String catalog) {
        return operator.getCatalog(shopName, catalog);
    }

    @GetMapping("/item/{shopName}/{catalog}/{name}")
    public Object getItem(@PathVariable String shopName, @PathVariable String catalog, @PathVariable String name) {
        return operator.getItem(shopName, catalog, name);
    }

}
