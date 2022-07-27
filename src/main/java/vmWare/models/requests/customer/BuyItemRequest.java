package vmWare.models.requests.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyItemRequest {

    @JsonProperty
    @NonNull
    private String name;
    @JsonProperty
    @NonNull
    private String catalog;
    @JsonProperty
    @NonNull
    private int amount;


}
