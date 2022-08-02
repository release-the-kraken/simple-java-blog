package com.rtk.user;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
     private int userid;
     private final String username;
     private final String password;
     private final String permission;
     @SerializedName("read_only")
     private final String readOnly;
}
