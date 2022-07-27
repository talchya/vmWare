package vmWare.models.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityDiscount {
    @JsonProperty
    @NonNull
    private String name;
    @JsonProperty
    @NonNull
    private String catalog;
    @JsonProperty
    @NonNull
    private int quantity;
    @JsonProperty
    @NonNull
    private double price;
}
