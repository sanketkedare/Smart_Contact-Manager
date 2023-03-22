package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Massege;

@Controller
@RequestMapping("/user")
public class UserController
{
	@Autowired
	UserRepository u;
	
	@Autowired
	ContactRepository contact;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal)
	{
		String name = principal.getName();
		System.out.println("USERNAME "+ name);
		
		User user = u.getUserByUsername(name);
		System.out.println(user.toString());
		
		model.addAttribute("user",user);
		
	}
	
	
	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal)
	{
		model.addAttribute("title","User DashBoard");
		return "normal/user_dashboard";
		
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
		
	//After adding contact on a web page
	@PostMapping("/process-contact")
	public String proccessContact(@ModelAttribute Contact contact,
			                      @RequestParam("profileImage")MultipartFile file,
			                      Principal principal,
			                      HttpSession session)
	{
        try 
        {
			String name = principal.getName();
			User user = this.u.getUserByUsername(name);
			
			//Processing image
			if(file.isEmpty())
			{
				System.out.println("No image");
				contact.setImage("blank.png");
			}
			else {
				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploded");
			}
			
			user.getContacts().add(contact);
			contact.setUser(user);
			this.u.save(user);
			System.out.println("Successfully added");
			
			//Success massage
			session.setAttribute("massege", new Massege("Your Contact is Added!!","success") );
			
			
		} 
        catch (Exception e) 
        {
        	//error massage
        	session.setAttribute("massege", new Massege("Somethig went wrong!!","danger") );
        	
        	
			e.printStackTrace();
			System.out.println("Error "+e.getMessage());
			return "normal/add_contact_form";
        }
		return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page,Model model, Principal principle)
	{
		model.addAttribute("title","Show Contacts");
		//Contact List
        String name = principle.getName();
        User user = this.u.getUserByUsername(name);
        int id = user.getId();
		
        Pageable p = PageRequest.of(page, 5);
        
		Page<Contact> contacts = this.contact.findContactsByUser(id, p);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
	
		return "normal/show_contacts";
	}
	
	@GetMapping("/{cId}/contact")
	public String shoContactDetails(@PathVariable("cId")Integer cId,
			                        Model model,
			                        Principal principal){
		
		Contact con = (this.contact.findById(cId)).get();
		
		String name = principal.getName();
		
		User userByUsername = this.u.getUserByUsername(name);
		
		if(userByUsername.getId()== con.getUser().getId())
		{
		   model.addAttribute("contact",con);
		   model.addAttribute("title",con.getName());
		}
	
	
		
		return "normal/contact_detail";
	}
	
	@RequestMapping("/delete/{cId}")
	public String deleteContactById(@PathVariable("cId")Integer id,
			                        Model model,HttpSession session,
			                        Principal principal)
	{
		Contact contact = (this.contact.findById(id)).get();
		contact.setUser(null);
		
       //this.contact.delete(contact);
		User user = this.u.getUserByUsername(principal.getName());
		user.getContacts().remove(contact);
		this.u.save(user);
		
		session.setAttribute("message",new Massege("Successfully Deleted Contact","success"));
		
		return "redirect:/user/show-contacts/0";
		
	}
	
	@RequestMapping("/update/{cId}")
	public String updateForm(@PathVariable("cId")Integer cId,Model model)
	{
		model.addAttribute("title","Update Contact");
		Contact con = this.contact.findById(cId).get();
		model.addAttribute("contact",con);
		
		return "normal/update_form";
	}
	
	//Update Contact Handler
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact con, 
			                    @RequestParam("profileImage")MultipartFile file, 
			                    Model model,Principal principal ,
			                    HttpSession session)
	{
		Contact oldContact = this.contact.findById(con.getcId()).get();
		
		try 
		{
			if(!file.isEmpty())
			{
				//Deleting old Photo
				File deletephoto = new ClassPathResource("static/img").getFile();
				File file1 = new File(deletephoto,oldContact.getImage());
				file1.delete();
				
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				con.setImage(file.getOriginalFilename());
			}
			else 
			{
				
				con.setImage(oldContact.getImage());
				
			}
			
			User user = this.u.getUserByUsername(principal.getName());
			
			con.setUser(user);
			
			this.contact.save(con);
			session.setAttribute("message", new Massege("Your Contact is Updated","success"));
			
		}
		catch(Exception e){}
		
		return"redirect:/user/"+con.getcId()+"/contact";
	}
	
	
	
	//My Profile
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// Updating Your profile
	@RequestMapping("/update-profile/{id}")
	public String updateprofile(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("title", "Update Contact");
		User user = this.u.findById(id).get();
		model.addAttribute("user", user);

		return "normal/update_profile";
	}

	// Update Contact Handler
	@PostMapping("/process-update-profile")
	public String updateContact(@ModelAttribute User user, 
			                    @RequestParam("profileImage") MultipartFile file,
			                    Model model, 
			                    HttpSession session) 
	{
		User oldUser = this.u.findById(user.getId()).get();

		try 
		{
			if (!file.isEmpty()) 
			{
				// Deleting old Photo
				File deletephoto = new ClassPathResource("static/img").getFile();
				File file1 = new File(deletephoto, oldUser.getImageUrl());
				file1.delete();

				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(file.getOriginalFilename());
			} 
			else 
			{
				
				user.setImageUrl(oldUser.getImageUrl());

			}


			this.u.save(user);
			session.setAttribute("message", new Massege("Your Contact is Updated", "success"));

		} catch (Exception e) {
		}

		return "normal/profile";
	}

	
	
	

}
