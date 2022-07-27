package vmWare.models.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import vmWare.models.PaymentMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @JsonProperty
    @NonNull
    private String name;
    @JsonProperty
    @NonNull
    private String user;
    @JsonProperty
    @NonNull
    private String password;
    @JsonProperty
    @NonNull
    private String email;
    @JsonProperty
    @NonNull
    private String phone;
    @JsonProperty
    @NonNull
    private PaymentMethod paymentMethod;
    @JsonProperty
    @NonNull
    private JsonObject paymentDetails;


}
