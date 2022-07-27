package vmWare.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vmWare.models.basic.Customer;
import vmWare.models.basic.Item;
import vmWare.models.requests.customer.BuyItemRequest;
import vmWare.services.paymentMethods.Card;
import vmWare.services.paymentMethods.PayPal;
import vmWare.utils.CartCache;

@Service
public class CustomerOperator {

    @Autowired
    ElasticOperator operator;

    private static CartCache cache = new CartCache();

    public void createCart(String token, long ttl) {
        cache.put(token, ttl);
    }

    public double addToCart(String token, String shopName, BuyItemRequest item) {
        Item newItem = operator.buyItem(item, shopName);
        return cache.update(token, newItem, operator.getDiscountIfAvailable(shopName, item.getCatalog(), item.getName()));
    }

    public double getTotalPrice(String token) {
        return cache.get(token);
    }

    public String pay(String token, Customer customer) {
        boolean answer = false;
        switch (customer.getPaymentMethod()) {
            case CARD:
                answer = Card.pay(customer.getPaymentDetails(), getTotalPrice(token));
                break;
            case PAYPAL:
                answer = PayPal.pay(customer.getPaymentDetails(), getTotalPrice(token));
                break;
        }
        if (answer) {
            return "payment was successful";
        }
        return "unsuccesful payment";
    }

}
