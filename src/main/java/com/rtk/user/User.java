package com.rtk.user;

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
     private final String readOnly;
}
