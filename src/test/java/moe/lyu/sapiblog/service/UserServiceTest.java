package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import moe.lyu.sapiblog.dto.JwtDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.exception.UserRegisterFailedException;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        LambdaUpdateChainWrapper<User> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
        categoryLambdaUpdateChainWrapper.likeRight(User::getUsername,"__test").remove();
    }

    @Test
    void login() throws NoSuchAlgorithmException {
        User user = new User();
        user.setUsername("__testLogin");
        user.setPassword("123456");
        user.setNickname("Test Login");
        user.setEmail("login@test.com");
        userService.register(user);

        User login = userService.login("__testLogin", "123456", null);
        assertNotNull(login);

        assertThrowsExactly(UserLoginFailed.class,()->userService.login("__testLogin2", "123456", null));
        assertThrowsExactly(UserLoginFailed.class,()->userService.login("__testLogin", "1234567", null));
        assertThrowsExactly(UserLoginFailed.class,()->userService.login(null, "1234567", null));
        assertThrowsExactly(UserLoginFailed.class,()->userService.login("__testLogin", null, null));

        assertEquals("__testlogin", login.getUsername());
    }

    @Test
    void register() throws NoSuchAlgorithmException {
        User userNull = null;
        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(userNull));

        User userUsernameNull = new User();
        userUsernameNull.setPassword("123456");
        userUsernameNull.setNickname("Test Register");
        userUsernameNull.setEmail("test@test.com");
        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(userUsernameNull));

        User userPasswordNull = new User();
        userPasswordNull.setUsername("__testRegisterPasswordNull");
        userPasswordNull.setNickname("Test Register");
        userPasswordNull.setEmail("test@test.com");
        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(userPasswordNull));

        User userNickNameNull = new User();
        userNickNameNull.setUsername("__testRegisterNickNameNull");
        userNickNameNull.setPassword("123456");
        userNickNameNull.setEmail("test@test.com");
        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(userNickNameNull));

        User userEmailNull = new User();
        userEmailNull.setUsername("__testRegisterEmailNull");
        userEmailNull.setPassword("123456");
        userEmailNull.setNickname("Test Register");
        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(userEmailNull));

        User user = new User();
        user.setUsername("__testRegister");
        user.setPassword("123456");
        user.setNickname("Test Register");
        user.setEmail("register@test.com");
        user.setPhone("123456");
        userService.register(user);

        assertThrowsExactly(UserRegisterFailedException.class,()->userService.register(user));

        LambdaQueryChainWrapper<User> lambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User one = lambdaQueryChainWrapper.eq(User::getUsername, user.getUsername()).one();
        assertEquals(user.getUsername(), one.getUsername());
    }

    @Test
    void update() throws NoSuchAlgorithmException {
        User user = new User();
        user.setUsername("__testUpdate");
        user.setPassword("123456");
        user.setNickname("Test Update");
        user.setEmail("update@test.com");
        user.setAvatar("avatar");
        user.setPhone("123456");
        user.setTotp("totp");
        user.setRole("USER");
        userService.register(user);

        User testUpdate = userService.login("__testUpdate", "123456", null);

        LambdaQueryChainWrapper<User> lambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = lambdaQueryChainWrapper.eq(User::getUsername, user.getUsername()).one();

        userDb.setUsername("__testUpdate2");
        userDb.setPassword("1234562");
        userDb.setNickname("Test Update2");
        userDb.setEmail("test@test.com2");
        userDb.setAvatar("avatar2");
        userDb.setPhone("1234562");
        userDb.setTotp("totp2");
        userDb.setRole("ADMIN");
        userService.update(userDb, testUpdate.getJwt());

        LambdaQueryChainWrapper<User> userUpdatedLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userUpdated = userUpdatedLambdaQueryChainWrapper.eq(User::getUsername, userDb.getUsername()).one();
        assertEquals(userDb.getUsername(), userUpdated.getUsername());
//        assertEquals(userDb.getPassword(), userUpdated.getPassword());
        assertEquals(userDb.getNickname(), userUpdated.getNickname());
        assertEquals(userDb.getEmail(), userUpdated.getEmail());
        assertEquals(userDb.getAvatar(), userUpdated.getAvatar());
        assertEquals(userDb.getJwt(), userUpdated.getJwt());
        assertEquals(userDb.getPhone(), userUpdated.getPhone());
        assertEquals(userDb.getTotp(), userUpdated.getTotp());
        assertEquals(userDb.getRole(), userUpdated.getRole());

        userDb.setId(-1);
        assertThrowsExactly(UserUpdateFailedException.class,()-> userService.update(userDb, userDb.getJwt()));
    }

    @Test
    void jwtDbVerify() throws NoSuchAlgorithmException {
        User user = new User();
        user.setUsername("__testJwtVerifyDb");
        user.setPassword("123456");
        user.setNickname("Test JwtVerifyDb");
        user.setEmail("jwtverifydb@test.com");
        userService.register(user);
        User login = userService.login("__testJwtVerifyDb", "123456", null);

        LambdaUpdateChainWrapper<User> lambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
        lambdaUpdateChainWrapper.eq(User::getId, login.getId()).set(User::getJwt, "").update();

        assertThrowsExactly(UserJwtVerifyFailedException.class,()->userService.jwtDbVerify(login.getJwt()));
    }

    @Test
    void jwtVerify() throws NoSuchAlgorithmException {
        User user = new User();
        user.setUsername("__testJwtVerify");
        user.setPassword("123456");
        user.setNickname("Test JwtVerify");
        user.setEmail("jwtverify@test.com");
        userService.register(user);
        User login = userService.login("__testJwtVerify", "123456", null);
        JwtDto jwtDto = userService.jwtVerify(login.getJwt());
        assertEquals(login.getId(), jwtDto.getUserId());
    }
}