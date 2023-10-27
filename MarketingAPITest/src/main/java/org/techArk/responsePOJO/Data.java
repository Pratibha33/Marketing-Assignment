package org.techArk.responsePOJO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"Id",
"Name",
"Email",
"Token"
})
public class Data {

@JsonProperty("Id")
public Integer id;
@JsonProperty("Name")
public String name;
@JsonProperty("Email")
public String email;
@JsonProperty("Token")
public String token;
@JsonProperty("Id")
public Integer getId() {
return id;
}

@JsonProperty("Id")
public void setId(Integer id) {
this.id = id;
}

@JsonProperty("Name")
public String getName() {
return name;
}

@JsonProperty("Name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("Email")
public String getEmail() {
return email;
}

@JsonProperty("Email")
public void setEmail(String email) {
this.email = email;
}

@JsonProperty("Token")
public String getToken() {
return token;
}

}