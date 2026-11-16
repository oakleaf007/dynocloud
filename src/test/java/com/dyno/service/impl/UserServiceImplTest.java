//package com.dyno.service.impl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.dyno.entity.User;
//import com.dyno.repository.UserRepository;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceImplTest {
//
// @Mock
// private UserRepository userRepo;
// 
// @InjectMocks
// private UserServiceImpl userService;
// 
// @Test
// void shouldReturnUser() {
////	 User user = new User();
//	 
////	 user.setUsername("oak");
//	 
//	 when(userRepo.findByUsername("unknown"))
//	 .thenReturn(Optional.empty());
//	 
//	 RuntimeException exception = assertThrows(
//			 RuntimeException.class,
//			 ()->userService.getCurrentUser("unknown")
//			 );
//	 
////	 User res = userService.getUserByUsername("oak");
//	 
//	 assertEquals("User not found", exception.getMessage());
//	 
//	 verify(userRepo).findByUsername("unknown");
// }
//}
