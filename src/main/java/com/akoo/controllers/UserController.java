package com.akoo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.akoo.data.ResultResponse;
import com.akoo.data.User;
import com.akoo.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@RequestMapping("/api/get/users/")
	public ResponseEntity<List<User>> getUsers() {
		return new ResponseEntity<List<User>>(userRepository.getUsers(), HttpStatus.OK);

	}
	
	@RequestMapping("/api/get/user/{username}/{password}/")
	public ResponseEntity<User> getUser(@PathVariable(value="name") String username,@PathVariable(value="password") String pass) {
		return new ResponseEntity<User>(userRepository.getUser(username,pass), HttpStatus.OK);

	}

	@RequestMapping(value="/api/add/user",method=RequestMethod.POST)
	public ResultResponse addUser(@RequestParam(value = "name") String username,
			@RequestParam(value = "pass") String password, @RequestParam(value = "role") String role,
			@RequestParam(value = "email") String email) {
		User user = new User();
		user.setPassword(password);
		user.setUsername(username);
		user.setRole(role);
		user.setEmail(email);
		return  userRepository.insertUser(user);

	}

	@RequestMapping("/api/change/user/passsword")
	public ResponseEntity<List<User>> changeUserPassword() {
		return new ResponseEntity<List<User>>(userRepository.getUsers(), HttpStatus.OK);

	}

	@RequestMapping("/api/delete/user")
	public ResponseEntity<ResultResponse> deleteUser(@RequestParam(value = "id") int id) {
		return new ResponseEntity<ResultResponse>(userRepository.deletetUser(id), HttpStatus.OK);

	}

}
