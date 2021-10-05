// 
// Decompiled by Procyon v0.5.36
// 

package com.adafy.sys;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ComponentScan({ "com.*" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private DataSource dataSource;
    @Value("${spring.queries.users-query}")
    private String usersQuery;
    @Value("${spring.queries.roles-query}")
    private String rolesQuery;
    
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().usersByUsernameQuery(this.usersQuery).authoritiesByUsernameQuery(this.rolesQuery).dataSource(this.dataSource).passwordEncoder((PasswordEncoder)this.bCryptPasswordEncoder);
    }
    

    

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
	
		 http.authorizeRequests().requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
		   http.
           authorizeRequests()
		.antMatchers("/v1/api/authenticate","/v1/api/constants/**","/v1/api/institute/**","/home/**","/","/v2/api-docs","/webjars/**","/swagger-resources/**",  "/configuration/**", "/*.html","/favicon.ico","/**/*.html","/**/*.css","/**/*.js").permitAll().

           antMatchers("/add/**").permitAll()
           .antMatchers("/img/*").permitAll()
           .antMatchers("/downloadFile/*").permitAll()
           .antMatchers("/api/**").permitAll()
           .antMatchers("/login").permitAll()
           .antMatchers("/add/**").permitAll()
           .antMatchers("/register").permitAll()
           .antMatchers("/admin").hasAnyRole("Administrator", "Security", "Receptionist").anyRequest()
           .authenticated().and().csrf().disable().formLogin()
           .loginPage("/login").failureUrl("/login?error=true")
           .defaultSuccessUrl("/admin/dashboard",true)
          // .successHandler(successHandler())
           .usernameParameter("email")
           .passwordParameter("password")
           .and().logout()
           .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
           .logoutSuccessUrl("/login").and().exceptionHandling()
           .accessDeniedPage("/404");
	}

    
    public void configure(final WebSecurity web) throws Exception {
    //   web.ignoring().antMatchers(new String[] { "/resources/**", "/assets/**", "/static/**", "/css/**", "/js/**", "/images/**" });
    //   web.ignoring().antMatchers(new String[] { "*/fullcalendar.min.css","/assets/*","*/*.css","*/css/*","/bower_components/*/*/*/*.css*","/bower_components/*/*.css*","/bower_components/slick-carousel/slick/slick.css", "/icon_fonts_assets/*", "/assets/*", "/resources/*", "/static/css/*", "/js/*", "/img/*" });
		web.ignoring().antMatchers("/resources/*", "/img/*","/icon_fonts_assets/*","/webcam/*","/bower_components/**",   "/css/*", "/js/**", "dist/**");

    
    }
    
    @Bean
    public RoleAuthenticationSuccess successHandler() {
        return new RoleAuthenticationSuccess();
    }
}
