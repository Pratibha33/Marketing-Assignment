package org.techArk.requestPOJO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDataRequest {
	
	@JsonProperty(value = "name")
    public String name;
	@JsonProperty(value = "email")
    public String email;
    @JsonProperty(value = "password")
    public String password;

}
