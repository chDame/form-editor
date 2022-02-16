package com.cda.form.controller;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cda.form.controller.dto.AuthUser;
import com.cda.form.exception.UnauthorizedException;
import com.cda.form.security.SecurityUtils;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "*")
public class AuthenticationController extends  AbstractController {

    @Autowired
    private IdentityService identityService;
    
    private boolean init = false;
    @Value("${admin.login}")
    private String adminLogin;
    @Value("${admin.password}")
    private String adminPassword;
    
	private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public AuthUser login(@RequestBody AuthUser user) {
		
		if (identityService.checkPassword(user.getUsername(), user.getPassword())) {
			return getAuthUser(identityService.createUserQuery().userId(user.getUsername()).singleResult());
		} else {
			throw new UnauthorizedException("Account invalid. Please use the link in the confirmation email"); 
		}
	}
	
	private AuthUser getAuthUser(User user) {
		AuthUser authUser = new AuthUser();
		BeanUtils.copyProperties(user, authUser);
		authUser.setUsername(user.getId());
		authUser.setToken(SecurityUtils.getJWTToken(user.getEmail()));
		return authUser;
	}
	
	@PostConstruct
    public void init() {
		if (!init) {
			if (!identityService.checkPassword(adminLogin, adminPassword)) {
				User admin = identityService.newUser(adminLogin);
				admin.setEmail(adminLogin);
				admin.setId(adminLogin);
				admin.setPassword(adminPassword);
				admin.setFirstName("Admin");
				identityService.saveUser(admin);
			}
			init=true;
		}
    }
	
	@Override
	public Logger getLogger() {
		return logger;
	}

}
