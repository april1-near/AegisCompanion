package com.aegis.companion.utils;

import com.aegis.companion.security.CustomUserDetails;
import com.aegis.companion.model.entity.User;
import com.aegis.companion.model.vo.UserVO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthState(auth);

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.user(); // 此处直接获取原始User对象
        }
        throw new AccessDeniedException("不支持的用户凭证类型");
    }

    private static void checkAuthState(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("用户未登录");
        }
    }

    // 保持原有辅助方法不变
    public static boolean currentUserIsAdmin() {
        return getCurrentUser().isAdmin(); // 依然有效
    }


    public static void maskSensitiveInfo(UserVO vo) {
        vo.setPhone(maskMiddle(vo.getPhone(), 3, 4))
                .setEmail(maskEmail(vo.getEmail()));
    }

    private static String maskMiddle(String str, int prefix, int suffix) {
        if (str == null || str.length() < (prefix + suffix)) return str;
        return str.substring(0, prefix) + "****" + str.substring(str.length() - suffix);
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        return prefix.length() > 2
                ? prefix.charAt(0) + "****" + prefix.substring(prefix.length() - 1) + email.substring(atIndex)
                : "****" + email.substring(atIndex);
    }
}
