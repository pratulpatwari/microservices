package dev.pratul.userservice.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import dev.pratul.UserServiceException;
import dev.pratul.userservice.entity.RoleType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private RedirectStrategy redirect = new DefaultRedirectStrategy();

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		log.debug("User {} succesfully authenticated", authentication.getName());
		String url = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			throw new UserServiceException("Cannot redirect!");
		}
		log.debug(authentication.getName() + " redirecting to: ", url);
		redirect.sendRedirect(request, response, url);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		String url = null;
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		List<String> roles = authorities.stream().map(role -> role.getAuthority()).collect(Collectors.toList());
		if (isTech(roles)) {
			url = "/tech";
		} else if (isBusiness(roles)) {
			url = "/business";
		} else {
			url = "/403";
		}
		return url;
	}

	private boolean isTech(List<String> roles) {
		if (roles.contains(RoleType.ROLE_TECH.name())) {
			return true;
		} else
			return false;
	}

	private boolean isBusiness(List<String> roles) {
		if (roles.contains(RoleType.ROLE_BUSINESS.name())) {
			return true;
		} else
			return false;
	}
}
