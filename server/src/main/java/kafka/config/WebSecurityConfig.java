package kafka.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @Value("${ldap.enabled:false}")
    private boolean ldapEnabled;

    @Value("${ldap.url}")
    private String ldapURL;

    @Value("${ldap.bind.dn}")
    private String ldapBindDN;

    @Value("${ldap.bind.password}")
    private String ldapBindPassword;

    @Value("${ldap.user.basedn}")
    private String ldapUserBaseDN;

    @Value("${ldap.user.id.attribute}")
    private String ldapUserIdAttribute;

    @Value("${ldap.group.basedn}")
    private String ldapGroupBaseDN;

    @Value("${ldap.group.name.attribute}")
    private String ldapGroupNameAttribute;


    @Value("${ldap.group.member.attribute}")
    private String ldapGroupMemberAttribute;


    @Value("${ldap.group.uppercase}")
    private boolean ldapGroupUpperCase;


    @Value("${ldap.group.lowercase}")
    private boolean ldapGroupLowerCase;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers("/authenticate","/dist/**").permitAll().
                // all other requests need to be authenticated
                        anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if(!ldapEnabled)
            super.configure(auth);
        else {
            SimpleAuthorityMapper simpleAuthorityMapper = new SimpleAuthorityMapper();
            simpleAuthorityMapper.setConvertToUpperCase(ldapGroupUpperCase);
            simpleAuthorityMapper.setConvertToLowerCase(ldapGroupLowerCase);
            simpleAuthorityMapper.setPrefix("");
            auth
                    .ldapAuthentication()
                    .contextSource()

                    .url(ldapURL)
                    .managerDn(ldapBindDN)
                    .managerPassword(ldapBindPassword)
                    .and()

                    .userSearchBase(ldapUserBaseDN)
                    .userSearchFilter(ldapUserIdAttribute+ "={0}")
                    .groupSearchBase(ldapGroupBaseDN)
                    .groupRoleAttribute(ldapGroupNameAttribute)
                    .authoritiesMapper(simpleAuthorityMapper)
                    .groupSearchFilter(ldapGroupMemberAttribute+"={0}")
                    .rolePrefix("");


        }

    }
}