package com.smart.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.ContactRepository;
import com.smart.entity.User;
import com.smart.helper.*;



@Controller
@RequestMapping("/user")
public class EmailController 
{
	@Autowired
	ContactRepository contact;
	
	@Autowired
	private EmailService emailService;

	@RequestMapping("/send/{cId}")
	public String index(@PathVariable("cId")Integer cId,Model model,Principal principal)
	{
		
		String emailId = contact.findById(cId).get().getEmail();
		model.addAttribute("emailId",emailId);
		
		

		return "admin/index";
	}
	
	@PostMapping("/sendMail")
	public String sendMail(@ModelAttribute Email email, HttpSession session)
	{
		System.out.println(email);
		
		emailService.sendMail(email);
		session.setAttribute("msg", "Email send Sucessfully");
		
		return "normal/show_contacts";
	}

}
