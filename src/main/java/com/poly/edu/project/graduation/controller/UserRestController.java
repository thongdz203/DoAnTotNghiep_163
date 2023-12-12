package com.poly.edu.project.graduation.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.edu.project.graduation.dao.FavouriteRepository;
import com.poly.edu.project.graduation.dao.UserRepository;
import com.poly.edu.project.graduation.model.AppUserEntity;
import com.poly.edu.project.graduation.model.ResponseObject;
import com.poly.edu.project.graduation.model.ShopFavoutiteEntity;
import com.poly.edu.project.graduation.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class UserRestController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FavouriteRepository favouriteRepository;
	
	
	// api lấy tất cả sản phẩm search theo keyword nhập vào
	@RequestMapping(value = "/getUsers", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public Page<AppUserEntity> findListProductByKey(
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort) throws Exception {
		try {
			Page<AppUserEntity> dataUsers = userService.findByKeyWord(keyword, PageRequest.of(page, size));
			return dataUsers;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// Lấy chi tiết sản phẩm
	@RequestMapping(value = "/getEmployeeById", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	// Let's return an object with: data, message, status
	ResponseEntity<ResponseObject> findById(@RequestParam(name = "id") Long id) {
		try {
			Optional<AppUserEntity> foundProduct = userRepository.findById(id);
			return foundProduct.isPresent()
					? ResponseEntity.status(HttpStatus.OK)
							.body(new ResponseObject("ok", "Tìm sản phẩm thành công", foundProduct))
					: ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(new ResponseObject("failed", "Cannot find employee with id = " + id, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	@RequestMapping(value = "/updateUser/isdeleted", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	void updateIsDeleted(@Param("id") long id) {
		userService.changeStatusIsdeleted(id);
	}
	
	@RequestMapping(value = "/updateUser/in_stock", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	void updateInstock(@Param("id") long id) {
		userService.changeStatusInstock(id);
	}
	@RequestMapping(value = "/hearth")
	void dropHearth(HttpSession session,Principal principal,
			@RequestParam(name = "idProduct", required = false, defaultValue = "0") String idProduct) {
		String userName = principal.getName();
		String id = userService.findIdUserByPrincipal(userName);
	}
	
	   @PostMapping("/update/{userName}")
	    public ResponseEntity<String> updateUser(@PathVariable String userName, @RequestBody AppUserEntity updatedUser) {
	        // Kiểm tra xem người dùng có tồn tại hay không
	        AppUserEntity existingUser = userRepository.findByUsername(userName);
	        if (existingUser == null) {
	            return new ResponseEntity<>("Người dùng không tồn tại", HttpStatus.NOT_FOUND);
	        }
	        System.out.println(updatedUser.getFirstName());
	        // Cập nhật thông tin người dùng
	        existingUser.setFirstName(updatedUser.getFirstName());
	        existingUser.setLastName(updatedUser.getLastName());
	        existingUser.setGender(updatedUser.getGender());
	        existingUser.setEmail(updatedUser.getEmail());
	        existingUser.setBirthday(updatedUser.getBirthday());
	        existingUser.setAddress(updatedUser.getAddress());
	        existingUser.setCountry(updatedUser.getCountry());
	        existingUser.setCity(updatedUser.getCity());
	        existingUser.setAvatar(updatedUser.getAvatar());

	        // Lưu người dùng đã cập nhật vào cơ sở dữ liệu
	        userRepository.save(existingUser);

	        return new ResponseEntity<>("Thông tin người dùng đã được cập nhật", HttpStatus.OK);
	    }
	
}
