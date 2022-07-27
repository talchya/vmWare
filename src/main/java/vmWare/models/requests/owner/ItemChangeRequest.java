package vmWare.models.requests.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemChangeRequest {
    @JsonProperty
    @NonNull
    private String name;
    @JsonProperty
    @NonNull
    private String catalog;
    @JsonProperty
    @NonNull
    private double price;
    @JsonProperty
    @NonNull
    private int quantity;
}
