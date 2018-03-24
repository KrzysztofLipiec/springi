package com.project.security.controller;

import javax.servlet.http.HttpServletRequest;

import com.project.security.JwtUserFactory;
import com.project.security.entities.ActivationToken;
import com.project.security.mail.EmailSender;
import com.project.security.model.AuthorityName;
import com.project.security.model.User;
import com.project.security.repository.AuthorityRepository;
import com.project.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.security.JwtTokenUtil;
import com.project.security.JwtUser;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@RestController
public class UserRestController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public UserRestController(EmailSender emailSender,
                              TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @RequestMapping(value = "user/{username}", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(request.getParameter("username"));
        return user;
    }

    @RequestMapping(value = "users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ArrayList<JwtUser> getAllUsers() {
        ArrayList<JwtUser> result = new ArrayList<>();
        List<User> users = userRepository.findAll();
        users.forEach(user -> result.add(JwtUserFactory.create(user)));
        return result;
    }

    @RequestMapping(value = "user/activate", method = RequestMethod.POST)
    public JwtUser activateUser(@RequestBody ActivationToken body) {
        String token = body.getToken();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);

        return JwtUserFactory.create(user);
    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public JwtUser addUser(@RequestBody User body, Device device) {
        body.setAuthorities(Arrays.asList(authorityRepository.findByName(AuthorityName.ROLE_USER)));
        body.setEnabled(false);
        body.setLastPasswordResetDate(new Date());
        body.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.saveAndFlush(body);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(body.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        Context context = new Context();
        context.setVariable("token", token);

        String mailBody = templateEngine.process("template", context);
        emailSender.sendEmail(body.getEmail(), "App Register", mailBody);

        return JwtUserFactory.create(body);
    }
}
