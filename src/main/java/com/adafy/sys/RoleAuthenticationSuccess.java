package com.adafy.sys;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class RoleAuthenticationSuccess implements AuthenticationSuccessHandler{
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		 handle(request, response, authentication);
	        clearAuthenticationAttributes(request);
		
	}
	
	protected void handle(HttpServletRequest request, 
		      HttpServletResponse response, Authentication authentication)
		      throws IOException {

		        String targetUrl = determineTargetUrl(authentication);

		        if (response.isCommitted()) {
		          
		            return;
		        }

		        redirectStrategy.sendRedirect(request, response, targetUrl);
		    }

		    protected String determineTargetUrl(Authentication authentication) {        
		        boolean isAdmin = false;
		        boolean isManager = false;
		        boolean isEmployee = false;
		        boolean isHeadofDepartment=false;
		        boolean isClerk=false;
		        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		        for (GrantedAuthority grantedAuthority : authorities) {
		            if (grantedAuthority.getAuthority().equals("SystemAdministrator")) {
		                isAdmin = true;
		                break;
		            } else if (grantedAuthority.getAuthority().equals("Manager")) {
		                isManager = true;
		                break;
		            } else if (grantedAuthority.getAuthority().equals("Staff")) {
		                isEmployee = true;
		                break;
		            }
		            else if (grantedAuthority.getAuthority().equals("Clerk")) {
		                isClerk = true;
		                break;
		            }
		            else if (grantedAuthority.getAuthority().equals("HeadOfDepartment")) {
		            	isHeadofDepartment = true;
		                break;
		            }
		        }

		        if (isAdmin | isManager) {
		            return "/admin/dashboard";
		        } else if (isHeadofDepartment) {
		            return "/admin/my/department/dashboard";
		        } else if (isEmployee) {
		            return "/admin/my/profile";
		        } 
		        else if (isClerk) {
		            return "/admin/my/profile";
		        }
		        else {
		            throw new IllegalStateException();
		        }
		    }

		    protected void clearAuthenticationAttributes(HttpServletRequest request) {
		        HttpSession session = request.getSession(false);
		        if (session == null) {
		            return;
		        }
		        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		    }

		    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		        this.redirectStrategy = redirectStrategy;
		    }
		    protected RedirectStrategy getRedirectStrategy() {
		        return redirectStrategy;
		    }

}
