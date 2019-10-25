package server.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.services.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }*/

    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                //.authenticationProvider(authProvider);  // option 1
                .userDetailsService(userService) // option 2
                .passwordEncoder(getPasswordEncoder());
    }*/


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/supplier", "/supplier/products", "/supplier/products/*").hasAuthority("ADMIN")
                    .mvcMatchers("/user/cabinet").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .formLogin()
                    //.loginPage("/user/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/")
                .and()
                //.httpBasic().and()
                .csrf()
                .disable();
    }


    @Bean
    public DaoAuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return authenticationProvider;
    }


    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Solution 1
        // auth.inMemoryAuthentication()
        // .withUser("admin").password("123456").roles("ADMIN");

        // Solution 2
        // auth.jdbcAuthentication().dataSource(this.dataSource)
        // .usersByUsernameQuery("SELECT username, password, enabled FROM
        // app_user WHERE username=?")
        // .authoritiesByUsernameQuery("SELECT username, role FROM app_user_role
        // WHERE username=?");

        // Solution 3
        auth.userDetailsService(userService).passwordEncoder(getPasswordEncoder());
    }*/

    /*@Bean
    public FilterRegistrationBean initCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        config.setAllowCredentials(true);

        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        config.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));

        config.addAllowedMethod("*");

        String origins = this.applicationConfig.getAllowedOrigins();
        if (origins != null && !"".equals(origins)) {
            config.setAllowedOrigins(Arrays.asList(StringHelper.splitWithoutWhitespace(origins, ",")));
        }
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ApplicationContext.language("error.msgUnauthorized"));
            }
       */ ;

}
