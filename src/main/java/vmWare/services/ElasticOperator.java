package vmWare.services;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import vmWare.models.basic.Customer;
import vmWare.models.basic.Item;
import vmWare.models.basic.Owner;
import vmWare.models.basic.QuantityDiscount;
import vmWare.models.requests.customer.BuyItemRequest;
import vmWare.models.requests.owner.ItemChangeRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class ElasticOperator {

    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    public List<String> getOwners(String shopName) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .build();
        SearchHits<Owner> owners = elasticsearchTemplate.search(searchQuery, Owner.class, IndexCoordinates.of("owners-" + shopName));
        return owners.stream().map(e -> e.getContent().getName()).collect(Collectors.toList());
    }

    public Owner getOwner(String shopName, String user) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("user", user))
                .build();
        SearchHits<Owner> owners = elasticsearchTemplate.search(searchQuery, Owner.class, IndexCoordinates.of("owners-" + shopName));
        return owners.stream().map(e -> e.getContent()).collect(Collectors.toList()).get(0);
    }

    public void setShop(String shopName, List<Owner> owners) {
        for (Owner owner: owners) {
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(owner).build();
            elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("owners-" + shopName));
        }
    }

    public void createCustomer(String shopName, Customer customer) {
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(customer).build();
        elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("customers-" + shopName));
    }

    public Customer getCustomer(String shopName, String user) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("user", user))
                .build();
        SearchHits<Customer> customers = elasticsearchTemplate.search(searchQuery, Customer.class, IndexCoordinates.of("customers" + shopName));
        return customers.stream().map(e -> e.getContent()).collect(Collectors.toList()).get(0);
    }

    public void removeCustomer(String shopName, String user) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("user", user))
                .build();
        SearchHits<Customer> customers = elasticsearchTemplate.search(searchQuery, Customer.class, IndexCoordinates.of("customers" + shopName));
        SearchHit<Customer> element = customers.stream().collect(Collectors.toList()).get(0);
        elasticsearchTemplate.delete(Objects.requireNonNull(element.getId()), IndexCoordinates.of("customers-" + shopName));
    }

    public List<String> getCatalogs(String shopName) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withAggregations(AggregationBuilders.terms("catalogs").field("catalog").order(BucketOrder.key(true)))
                .build();
        SearchHits<Item> items = elasticsearchTemplate.search(searchQuery, Item.class, IndexCoordinates.of("items-" + shopName));
        List<? extends Terms.Bucket> buckets = ((ParsedStringTerms)((ElasticsearchAggregations)
                Objects.requireNonNull(items.getAggregations())).aggregations().asList().get(0)).getBuckets();
        return buckets.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
    }

    public List<Item> getCatalog(String shopName, String catalog) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("catalog", catalog))
                .build();
        SearchHits<Item> items = elasticsearchTemplate.search(searchQuery, Item.class, IndexCoordinates.of("items-" + shopName));
        return items.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    public QuantityDiscount getDiscountIfAvailable(String shopName, String catalog, String name) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("catalog", catalog))
                .withQuery(matchQuery("name", name))
                .build();
        SearchHits<QuantityDiscount> discounts = elasticsearchTemplate.search(searchQuery, QuantityDiscount.class,
                IndexCoordinates.of("discounts-" + shopName));
        return discounts.stream().map(SearchHit::getContent).collect(Collectors.toList()).get(0);
    }

    private SearchHits<Item> getItemHits(String shopName, String catalog, String itemName) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("catalog", catalog))
                .withQuery(matchQuery("name", itemName))
                .build();
        return elasticsearchTemplate.search(searchQuery, Item.class, IndexCoordinates.of("items-" + shopName));
    }

    public Item getItem(String shopName, String catalog, String itemName) {
        SearchHits<Item> items = getItemHits(shopName, catalog, itemName);
        return items.stream().map(SearchHit::getContent).collect(Collectors.toList()).get(0);
    }
    
    public void addItems(List<ItemChangeRequest> items, String shopName) {
        for (ItemChangeRequest item: items) {
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(item).build();
            elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("items-" + shopName));
        }
    }

    public void updateItems(List<ItemChangeRequest> items, String shopName) {
        for (ItemChangeRequest item: items) {
            SearchHit<Item> element = getItemHits(shopName, item.getCatalog(), item.getName())
                    .stream().collect(Collectors.toList()).get(0);
            IndexQuery indexQuery = new IndexQueryBuilder().withObject(item).withId(Objects.requireNonNull(element.getId())).build();
            elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("items-" + shopName));
        }
    }

    public Item buyItem(BuyItemRequest item, String shopName) {
        int quantity = 0;
        SearchHit<Item> element = getItemHits(shopName, item.getCatalog(), item.getName())
                .stream().collect(Collectors.toList()).get(0);
        Item newItem = element.getContent();
        if (newItem.getQuantity() <= item.getAmount()) {
            quantity = newItem.getQuantity();
            newItem.setQuantity(0);
        }
        else {
            newItem.setQuantity(newItem.getQuantity() - item.getAmount());
            quantity = item.getAmount();
        }
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(newItem).withId(Objects.requireNonNull(element.getId())).build();
        elasticsearchTemplate.index(indexQuery, IndexCoordinates.of("items-" + shopName));
        newItem.setQuantity(quantity);
        return newItem;
    }

    public void removeItems(List<ItemChangeRequest> items, String shopName) {
        for (ItemChangeRequest item: items) {
            SearchHit<Item> element = getItemHits(shopName, item.getCatalog(), item.getName())
                    .stream().collect(Collectors.toList()).get(0);
            elasticsearchTemplate.delete(Objects.requireNonNull(element.getId()), IndexCoordinates.of("items-" + shopName));
        }
    }


}
