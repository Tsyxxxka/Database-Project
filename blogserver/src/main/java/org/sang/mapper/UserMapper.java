package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.bean.UserCode;

import java.util.List;

/**
 * Created by sang on 2017/12/17.
 */
@Mapper
public interface UserMapper {

    User loadUserByUsername(@Param("username") String username);

    User loadUserByUserEmail(@Param("email") String email);

    int saveCode(UserCode userCode);

    int updateCode(@Param("email") String email, @Param("trueCode") String trueCode);

    UserCode findTrueCodeByEmail(@Param("email") String email);

    String findEmailByUid(@Param("id") Long id);

    long reg(User user);

    long regMail(User user);

    int updateUserEmail(@Param("email") String email, @Param("id") Long id);

    int updateUserNickname(@Param("nickname") String nickname, @Param("id") Long id);

    int updateUserPwd(@Param("password") String password, @Param("id") Long id);

    List<User> getUserByNickname(@Param("nickname") String nickname);

    //new add
    List<User> getAllNickname();

    List<Role> getAllRole();

    int updateUserEnabled(@Param("enabled") Boolean enabled, @Param("uid") Long uid);
    int updateUserAuth(@Param("auth") Boolean auth, @Param("uid") Long uid);

    int deleteUserById(Long uid);

    int deleteUserCodeByEmail(String email);

    int deleteUserRolesByUid(Long id);

    int setUserRoles(@Param("rids") Long[] rids, @Param("id") Long id);

    User getUserById(@Param("id") Long id);
}
