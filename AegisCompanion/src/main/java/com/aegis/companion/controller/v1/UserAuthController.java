package com.aegis.companion.controller.v1;

import com.aegis.companion.model.dto.UserLoginDTO;
import com.aegis.companion.model.dto.UserRegisterDTO;
import com.aegis.companion.model.vo.ResponseResult;
import com.aegis.companion.model.vo.UserVO;
import com.aegis.companion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseResult<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        System.out.println(dto);
        return ResponseResult.success(userService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseResult<Map<String, String>> login(@Valid @RequestBody UserLoginDTO dto) {
        String tokenStr = userService.login(dto);
        Map<String, String> tokenReq = new HashMap<>();
        tokenReq.put("token",tokenStr);
        return ResponseResult.success(tokenReq);
    }
}
