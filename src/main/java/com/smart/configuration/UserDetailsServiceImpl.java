package com.smart.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entity.User;

public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	private UserRepository userrepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		//getting user from database
		User user = userrepo.getUserByUsername(username);
		if(user == null)
		{
			throw new UsernameNotFoundException("Could not found User");
		}
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
	
		return customUserDetails;
	}

}
