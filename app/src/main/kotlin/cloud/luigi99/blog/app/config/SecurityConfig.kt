package cloud.luigi99.blog.app.config
import cloud.luigi99.blog.member.adapter.`in`.web.auth.filter.JwtAuthenticationFilter
import cloud.luigi99.blog.member.adapter.`in`.web.auth.oauth2.OAuth2AuthenticationSuccessHandler
import cloud.luigi99.blog.member.adapter.`in`.web.auth.security.CustomAccessDeniedHandler
import cloud.luigi99.blog.member.adapter.`in`.web.auth.security.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
        oauth2SuccessHandler: OAuth2AuthenticationSuccessHandler,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
        customAccessDeniedHandler: CustomAccessDeniedHandler,
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2Login { oauth2 ->
                oauth2.successHandler(oauth2SuccessHandler)
            }.exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/actuator/health",
                        "/api/v1/auth/**",
                        "/api/v1/posts/**",
                        "/oauth2/**",
                        "/login/oauth2/**",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000", "http://localhost:5173")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
