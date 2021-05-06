package com.chevron.edap.gomica.security;


import com.chevron.edap.gomica.security.dto.User;

public interface IAuthService {
    User whoAmI();
    String getUserDisplayName();

}
