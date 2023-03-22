package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Massege;

@Controller
public class HomeController
{
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userrepo;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Hame - Smart Contact Manager");

		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");

		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user", new User());

		return "signup";
	}

	// Register handler
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) 
	{
		try 
		{
			if (!agreement) 
			{
				System.out.println("Not agreed terms and Condition");
				throw new Exception("Not agreed terms and Condition");
			}
			if(result1.hasErrors())
			{
				System.out.println("Error "+ result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement " + agreement);
			System.out.println("User " + user);

			User result = this.userrepo.save(user);
			model.addAttribute("user", new User());

			session.setAttribute("massege", new Massege("Successfully Register !!", "alert-success"));

			return "signup";

		} 
		
		catch (Exception e) 
		{

			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("massege", new Massege("Someting went Wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
