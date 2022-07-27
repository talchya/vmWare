package vmWare.models.basic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Owner {
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
}
