package com.hack.journal.config;

import com.hack.journal.controller.ErrorControllerImpl;
import com.hack.journal.entity.UnexpiredRevokedToken;
import com.hack.journal.entity.User;
import com.hack.journal.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Autowired
    private final ErrorControllerImpl errorController;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

//      todo: log this
//        System.out.println(request);
//        System.out.println("Auth Head" + authHeader);
//        System.out.println("JWT:" + jwt);

        try {
            userEmail = jwtService.extractUserName(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails) && userDetails.isEnabled()) {

                    User user = (User) userDetails;

                    if (user.getUnexpiredRevokedTokens().contains(new UnexpiredRevokedToken(jwt))) {
                        sendErrorResponse(response, 401, "Unauthorized", "Token was revoked");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (DataAccessResourceFailureException e) {
            sendErrorResponse(response, 503, "DataAccessResourceFailureException", e.getMessage());
            return;
        } catch (UsernameNotFoundException e) {
            sendErrorResponse(response, 404, "Not Found", "Couldn't find user");
            return;
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, 401, "Unauthorized", "Token expired");
            return;
        } catch (Exception e) {
            sendErrorResponse(response, 401, "Unauthorized", "Couldn't validate token");

            return;
        }


        filterChain.doFilter(request, response);

    }

    private void sendErrorResponse(HttpServletResponse response, int httpStatus, String errorCode, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setStatus(httpStatus);
        response.getWriter().write(new JSONObject()
                .put("error", errorCode)
                .put("message", errorMessage)
                .toString()
        );
    }
}
