package dev.pratul;

/*@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ZuulSecurityConfiguration extends ResourceServerConfigurerAdapter {

	private final ResourceServerProperties resource;

	public ZuulSecurityConfiguration(ResourceServerProperties resource) {
		this.resource = resource;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/oauth2/**").permitAll().antMatchers("/**").authenticated();
	}

	@Bean
	public TokenStore jwkTokenStore() {
		return new JwkTokenStore(Collections.singletonList(resource.getJwk().getKeySetUri()),
				new AccessTokenConverter(), null);
	}
}*/
