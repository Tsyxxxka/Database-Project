package org.sang.service;

import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.bean.UserCode;
import org.sang.config.MyPasswordEncoder;
import org.sang.mapper.RolesMapper;
import org.sang.mapper.UserMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sang on 2017/12/17.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RolesMapper rolesMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(s);
        if (user == null) {
            //避免返回null，这里返回一个不含有任何值的User对象，在后期的密码比对过程中一样会验证失败
            return new User();
        }
        //查询用户的角色信息，并返回存入user中
        List<Role> roles = new ArrayList<>();
        Role r = new Role();
        Long i = user.getAuth();
        r.setId(i);
        if (i==1) {
            r.setName("超级管理员");
        } else {
            r.setName("普通用户");
        }
        user.setRoles(roles);
        return user;
    }

    /**
     * @param user
     * @return 0表示成功
     * 1表示用户名重复
     * 2表示失败
     */
    public int reg(User user) {
        User loadUserByUsername = userMapper.loadUserByUsername(user.getUsername());
        if (loadUserByUsername != null) {
            return 1;
        }
        //插入用户,插入之前先对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);//用户可用

        //Date date = new Date();
        //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //Date date = new Date(System.currentTimeMillis());
        //user.setRegTime(formatter.format(date));
        //user.setRegTime(date);

        long result = userMapper.reg(user);
        //配置用户的角色，默认都是普通用户
        String[] roles = new String[]{"2"};
        int i = rolesMapper.addRoles(roles, user.getId());
        boolean b = i == roles.length && result == 1;
        if (b) {
            return 0;
        } else {
            return 2;
        }
    }

    public int insertUser(User user) {
        User loadUserByUsername = userMapper.loadUserByUsername(user.getUsername());
        if (loadUserByUsername != null) {
            return 1;
        }
        //插入用户,插入之前先对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);//用户可用
        user.setAuth(2L);
        UserCode userCode = userMapper.findTrueCodeByEmail(user.getEmail());
        boolean a = (user.getCode()).equals(userCode.getTrueCode());
        if(!a)
            return 3;
        long result = userMapper.regMail(user);
        if (result == 1) {
            return 0;
        } else {
            return 2;
        }
    }


    public int saveCode(String email,String resultCode) {

        UserCode userCode1 = userMapper.findTrueCodeByEmail(email);

        if(userCode1 == null){
            UserCode userCode = new UserCode();
            userCode.setTrueCode(resultCode);
            userCode.setEmail(email);
            int result = userMapper.saveCode(userCode);
            return result;
        }else{
            userCode1.setTrueCode(resultCode);
            int result = userMapper.updateCode(email,resultCode);
            return result;
        }
    }

    public int updateUserEmail(String email) {
        return userMapper.updateUserEmail(email, Util.getCurrentUser().getId());
    }

    public int updateUserNickname(String nickname) {
        return userMapper.updateUserNickname(nickname, Util.getCurrentUser().getId());
    }

    public List<User> getUserByNickname(String nickname) {
        List<User> list = userMapper.getUserByNickname(nickname);
        return list;
    }

    public List<User> getAllNickname() {
        List<User> list = userMapper.getAllNickname();
        return list;
    }

    public List<Role> getAllRole() {
        return userMapper.getAllRole();
    }

    public int updateUserEnabled(Boolean enabled, Long uid) {
        return userMapper.updateUserEnabled(enabled, uid);
    }

    public int deleteUserById(Long uid) {
        return userMapper.deleteUserById(uid);
    }

    public int updateUserRoles(Long[] rids, Long id) {
        int i = userMapper.deleteUserRolesByUid(id);
        return userMapper.setUserRoles(rids, id);
    }

    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    public int updateUserPwd(String password) {
        return userMapper.updateUserPwd(password, Util.getCurrentUser().getId());
    }
}
