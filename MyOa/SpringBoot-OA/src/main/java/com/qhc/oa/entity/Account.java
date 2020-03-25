package com.qhc.oa.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author 
 * 
 */
public class Account implements Serializable {
    private Integer id;

    /**
     * 用户名
     */
    private String loginname;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 国籍
     */
    private String location;

    /**
     * 角色
     */
    private String role;

//    这里在bean里定义集合，是可以通过自定义SQL一次性全部查出
//    如果不用permission集合，那么每次查询权限的时候都需要迭代roles集合，这样也是为了简化开发
    List<Role> roles;
    List<Permission> permissions;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Account other = (Account) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLoginname() == null ? other.getLoginname() == null : this.getLoginname().equals(other.getLoginname()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getAge() == null ? other.getAge() == null : this.getAge().equals(other.getAge()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLoginname() == null) ? 0 : getLoginname().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getAge() == null) ? 0 : getAge().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        return result;
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getClass().getSimpleName());
//        sb.append(" [");
//        sb.append("Hash = ").append(hashCode());
//        sb.append(", id=").append(id);
//        sb.append(", loginname=").append(loginname);
//        sb.append(", password=").append(password);
//        sb.append(", nickname=").append(nickname);
//        sb.append(", age=").append(age);
//        sb.append(", location=").append(location);
//        sb.append(", role=").append(role);
//        sb.append(", serialVersionUID=").append(serialVersionUID);
//        sb.append("]");
//        return sb.toString();
//    }
}