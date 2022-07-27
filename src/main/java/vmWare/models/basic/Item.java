package vmWare.models.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @JsonProperty
    @NonNull
    private String name;
    @JsonProperty
    @NonNull
    private double price;
    @JsonProperty
    @NonNull
    private int quantity;
    @JsonProperty
    @NonNull
    private String catalog;
}
